//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.world.world.properties;

public class WorldProperties {
    public static final WorldProperty SPAWN_X;
    public static final WorldProperty SPAWN_Y;
    public static final WorldProperty SPAWN_Z;
    public static final WorldProperty DO_DAY_CYCLE;
    public static final WorldProperty DIFFICULTY;
    public static final WorldProperty ALLOW_MONSTERS;
    public static final WorldProperty ALLOW_ANIMALS;
    public static final WorldProperty PVP;
    public static final WorldProperty ENVIRONMENT;
    public static final WorldProperty WORLD_TYPE;
    public static final WorldProperty[] VALUES;

    static {
        SPAWN_X = new WorldProperty("spawnX", PropertyType.INT, 0);
        SPAWN_Y = new WorldProperty("spawnY", PropertyType.INT, 255);
        SPAWN_Z = new WorldProperty("spawnZ", PropertyType.INT, 0);
        DO_DAY_CYCLE = new WorldProperty("doDayCycle", PropertyType.BOOLEAN, true);
        DIFFICULTY = new WorldProperty("difficulty", PropertyType.STRING, "peaceful", (value) -> {
            String difficulty = (String) value;
            return difficulty.equalsIgnoreCase("peaceful") || difficulty.equalsIgnoreCase("easy") || difficulty.equalsIgnoreCase("normal") || difficulty.equalsIgnoreCase("hard");
        });
        ALLOW_MONSTERS = new WorldProperty("allowMonsters", PropertyType.BOOLEAN, true);
        ALLOW_ANIMALS = new WorldProperty("allowAnimals", PropertyType.BOOLEAN, true);
        PVP = new WorldProperty("pvp", PropertyType.BOOLEAN, true);
        ENVIRONMENT = new WorldProperty("environment", PropertyType.STRING, "normal", (value) -> {
            String env = (String) value;
            return env.equalsIgnoreCase("normal") || env.equalsIgnoreCase("nether") || env.equalsIgnoreCase("the_end");
        });
        WORLD_TYPE = new WorldProperty("worldtype", PropertyType.STRING, "default", (value) -> {
            String worldType = (String) value;
            return worldType.equalsIgnoreCase("default") || worldType.equalsIgnoreCase("flat") || worldType.equalsIgnoreCase("large_biomes") || worldType.equalsIgnoreCase("amplified") || worldType.equalsIgnoreCase("customized") || worldType.equalsIgnoreCase("debug_all_block_states") || worldType.equalsIgnoreCase("default_1_1");
        });
        VALUES = new WorldProperty[]{SPAWN_X, SPAWN_Y, SPAWN_Z, DIFFICULTY, ALLOW_MONSTERS, ALLOW_ANIMALS, PVP, ENVIRONMENT, WORLD_TYPE};
    }
}
