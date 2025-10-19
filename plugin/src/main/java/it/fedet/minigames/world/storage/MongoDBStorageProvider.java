package it.fedet.minigames.world.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import it.fedet.minigames.api.world.data.WorldData;
import it.fedet.minigames.api.world.storage.WorldStorageProvider;
import org.bson.Document;
import org.bson.types.Binary;
import org.bukkit.World;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MongoDBStorageProvider implements WorldStorageProvider {

    private final String connectionString;
    private final String databaseName;
    private final String collectionName;
    private final ExecutorService executor;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBStorageProvider(String connectionString, String databaseName, String collectionName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
        this.executor = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("world-loader-mongodb");
            return t;
        });
    }

    @Override
    public void initialize() {
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(databaseName);
        collection = database.getCollection(collectionName);
    }

    @Override
    public CompletableFuture<Optional<WorldData>> getWorldData(String worldName) {
        return CompletableFuture.supplyAsync(() -> {
            Document doc = collection.find(Filters.eq("worldName", worldName)).first();
            
            if (doc == null) {
                return Optional.empty();
            }

            Binary worldDataBinary = doc.get("worldData", Binary.class);
            String envStr = doc.getString("environment");
            long seed = doc.getLong("seed");
            boolean generateStructures = doc.getBoolean("generateStructures");

            World.Environment environment = World.Environment.valueOf(envStr);

            WorldData worldData = WorldData.builder()
                    .worldName(worldName)
                    .worldData(worldDataBinary.getData())
                    .environment(environment)
                    .seed(seed)
                    .generateStructures(generateStructures)
                    .build();

            return Optional.of(worldData);
        }, executor);
    }

    @Override
    public CompletableFuture<Void> saveWorldData(WorldData worldData) {
        return CompletableFuture.runAsync(() -> {
            Document doc = new Document()
                    .append("worldName", worldData.getWorldName())
                    .append("worldData", new Binary(worldData.getWorldData()))
                    .append("environment", worldData.getEnvironment().name())
                    .append("seed", worldData.getSeed())
                    .append("generateStructures", worldData.isGenerateStructures())
                    .append("lastUpdated", System.currentTimeMillis());

            collection.deleteOne(Filters.eq("worldName", worldData.getWorldName()));
            collection.insertOne(doc);
        }, executor);
    }

    @Override
    public CompletableFuture<Boolean> exists(String worldName) {
        return CompletableFuture.supplyAsync(() -> {
            long count = collection.countDocuments(Filters.eq("worldName", worldName));
            return count > 0;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteWorldData(String worldName) {
        return CompletableFuture.runAsync(() -> {
            collection.deleteOne(Filters.eq("worldName", worldName));
        }, executor);
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
        executor.shutdown();
    }
}