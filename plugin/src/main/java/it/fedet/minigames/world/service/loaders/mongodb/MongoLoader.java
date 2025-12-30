//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.service.loaders.mongodb;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import it.fedet.minigames.api.logging.Logging;
import it.fedet.minigames.api.world.database.WorldDbProvider;
import it.fedet.minigames.api.world.exceptions.UnknownWorldException;
import it.fedet.minigames.api.world.exceptions.WorldInUseException;
import it.fedet.minigames.world.service.WorldService;
import it.fedet.minigames.world.service.loaders.UpdatableLoader;
import org.bson.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MongoLoader extends UpdatableLoader {
    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(2, (new ThreadFactoryBuilder()).setNameFormat("SWM MongoDB Lock Pool Thread #%1$d").build());
    private final Map<String, ScheduledFuture> lockedWorlds = new HashMap();
    private final MongoClient client;
    private final String database;
    private final String collection;

    public MongoLoader(WorldDbProvider provider) {
        this.database = provider.getDatabaseName();
        this.collection = provider.getTableOrCollectionName();
        this.client = MongoClients.create(provider.getConnectionOrHostString());
        MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
        mongoCollection.createIndex(Indexes.ascending("name"), (new IndexOptions()).unique(true));
    }

    public void update() {
        MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
        MongoCursor var2 = mongoDatabase.listCollectionNames().iterator();

        while (var2.hasNext()) {
            String collectionName = (String) var2.next();
            if (collectionName.equals(this.collection + "_files.files") || collectionName.equals(this.collection + "_files.chunks")) {
                Logging.info(WorldService.class, "Updating MongoDB database...");
                mongoDatabase.getCollection(this.collection + "_files.files").renameCollection(new MongoNamespace(this.database, this.collection + ".files"));
                mongoDatabase.getCollection(this.collection + "_files.chunks").renameCollection(new MongoNamespace(this.database, this.collection + ".chunks"));
                Logging.info(WorldService.class, "MongoDB database updated!");
                break;
            }
        }

        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
        MongoCursor<Document> documents = mongoCollection.find(Filters.or(Filters.eq("locked", true), Filters.eq("locked", false))).cursor();
        if (documents.hasNext()) {
            Logging.warning(WorldService.class, "Your SWM MongoDB database is outdated. The update process will start in 10 seconds.");
            Logging.warning(WorldService.class, "Note that this update will make your database incompatible with older SWM versions.");
            Logging.warning(WorldService.class, "Make sure no other servers with older SWM versions are using this database.");
            Logging.warning(WorldService.class, "Shut down the server to prevent your database from being updated.");

            try {
                Thread.sleep(10000L);
            } catch (InterruptedException var5) {
            }

            while (documents.hasNext()) {
                String worldName = ((Document) documents.next()).getString("name");
                mongoCollection.updateOne(Filters.eq("name", worldName), Updates.set("locked", 0L));
            }
        }

    }

    public byte[] loadWorld(String worldName, boolean readOnly) throws UnknownWorldException, IOException, WorldInUseException {
        return this.loadWorld(worldName, readOnly, false);
    }

    public byte[] loadWorld(String worldName, boolean readOnly, boolean ignoreLocked) throws UnknownWorldException, IOException, WorldInUseException {
        try {
            MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
            Document worldDoc = (Document) mongoCollection.find(Filters.eq("name", worldName)).first();
            if (worldDoc == null) {
                throw new UnknownWorldException(worldName);
            } else {
                if (!readOnly) {
                    long lockedMillis = ignoreLocked ? 0L : worldDoc.getLong("locked");
                    if (System.currentTimeMillis() - lockedMillis <= 300000L) {
                        throw new WorldInUseException(worldName);
                    }

                    this.updateLock(worldName, true);
                }

                GridFSBucket bucket = GridFSBuckets.create(mongoDatabase, this.collection);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bucket.downloadToStream(worldName, stream);
                return stream.toByteArray();
            }
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    private void updateLock(String worldName, boolean forceSchedule) {
        try {
            MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
            mongoCollection.updateOne(Filters.eq("name", worldName), Updates.set("locked", System.currentTimeMillis()));
        } catch (MongoException ex) {
            Logging.error(WorldService.class, "Failed to update the lock for world " + worldName + ":");
            ex.printStackTrace();
        }

        if (forceSchedule || this.lockedWorlds.containsKey(worldName)) {
            this.lockedWorlds.put(worldName, SERVICE.schedule(() -> this.updateLock(worldName, false), 60000L, TimeUnit.MILLISECONDS));
        }

    }

    public boolean worldExists(String worldName) throws IOException {
        try {
            MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
            Document worldDoc = (Document) mongoCollection.find(Filters.eq("name", worldName)).first();
            return worldDoc != null;
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    public List<String> listWorlds() throws IOException {
        List<String> worldList = new ArrayList();

        try {
            MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
            MongoCursor<Document> documents = mongoCollection.find().cursor();

            while (documents.hasNext()) {
                worldList.add(((Document) documents.next()).getString("name"));
            }

            return worldList;
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    public void saveWorld(String worldName, byte[] serializedWorld, boolean lock) throws IOException {
        try {
            MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
            GridFSBucket bucket = GridFSBuckets.create(mongoDatabase, this.collection);
            GridFSFile oldFile = (GridFSFile) bucket.find(Filters.eq("filename", worldName)).first();
            if (oldFile != null) {
                bucket.rename(oldFile.getObjectId(), worldName + "_backup");
            }

            bucket.uploadFromStream(worldName, new ByteArrayInputStream(serializedWorld));
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
            Document worldDoc = (Document) mongoCollection.find(Filters.eq("name", worldName)).first();
            long lockMillis = lock ? System.currentTimeMillis() : 0L;
            if (worldDoc == null) {
                mongoCollection.insertOne((new Document()).append("name", worldName).append("locked", lockMillis));
            } else if (System.currentTimeMillis() - worldDoc.getLong("locked") > 300000L && lock) {
                this.updateLock(worldName, true);
            }

        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    public void unlockWorld(String worldName) throws IOException, UnknownWorldException {
        ScheduledFuture future = this.lockedWorlds.remove(worldName);
        if (future != null) {
            future.cancel(false);
        }

        try {
            MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
            UpdateResult result = mongoCollection.updateOne(Filters.eq("name", worldName), Updates.set("locked", 0L));
            if (result.getMatchedCount() == 0L) {
                throw new UnknownWorldException(worldName);
            }
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    public boolean isWorldLocked(String worldName) throws IOException, UnknownWorldException {
        if (this.lockedWorlds.containsKey(worldName)) {
            return true;
        } else {
            try {
                MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
                MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
                Document worldDoc = (Document) mongoCollection.find(Filters.eq("name", worldName)).first();
                if (worldDoc == null) {
                    throw new UnknownWorldException(worldName);
                } else {
                    return System.currentTimeMillis() - worldDoc.getLong("locked") <= 300000L;
                }
            } catch (MongoException ex) {
                throw new IOException(ex);
            }
        }
    }

    public void deleteWorld(String worldName) throws IOException, UnknownWorldException {
        ScheduledFuture future = this.lockedWorlds.remove(worldName);
        if (future != null) {
            future.cancel(false);
        }

        try {
            MongoDatabase mongoDatabase = this.client.getDatabase(this.database);
            GridFSBucket bucket = GridFSBuckets.create(mongoDatabase, this.collection);
            GridFSFile file = (GridFSFile) bucket.find(Filters.eq("filename", worldName)).first();
            if (file == null) {
                throw new UnknownWorldException(worldName);
            } else {
                bucket.delete(file.getObjectId());
                file = (GridFSFile) bucket.find(Filters.eq("filename", worldName + "_backup")).first();
                if (file != null) {
                    bucket.delete(file.getObjectId());
                }

                MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(this.collection);
                mongoCollection.deleteOne(Filters.eq("name", worldName));
            }
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }
}
