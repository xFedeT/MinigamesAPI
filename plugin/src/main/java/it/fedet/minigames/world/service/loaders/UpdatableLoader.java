//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.world.service.loaders;


import it.fedet.minigames.api.world.loaders.SlimeLoader;

import java.io.IOException;

public abstract class UpdatableLoader implements SlimeLoader {
    public abstract void update() throws NewerDatabaseException, IOException;

    public class NewerDatabaseException extends Exception {
        private final int currentVersion;
        private final int databaseVersion;

        public int getCurrentVersion() {
            return this.currentVersion;
        }

        public int getDatabaseVersion() {
            return this.databaseVersion;
        }

        public NewerDatabaseException(int currentVersion, int databaseVersion) {
            this.currentVersion = currentVersion;
            this.databaseVersion = databaseVersion;
        }
    }
}
