//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.swm.world.properties;

public class SlimeProperties {
    public static final SlimeProperty SPAWN_X;
    public static final SlimeProperty SPAWN_Y;
    public static final SlimeProperty SPAWN_Z;
    public static final SlimeProperty DO_DAY_CYCLE;
    public static final SlimeProperty DIFFICULTY;
    public static final SlimeProperty ALLOW_MONSTERS;
    public static final SlimeProperty ALLOW_ANIMALS;
    public static final SlimeProperty PVP;
    public static final SlimeProperty ENVIRONMENT;
    public static final SlimeProperty WORLD_TYPE;
    public static final SlimeProperty[] VALUES;

    static {
        SPAWN_X = new SlimeProperty("spawnX", PropertyType.INT, 0);
        SPAWN_Y = new SlimeProperty("spawnY", PropertyType.INT, 255);
        SPAWN_Z = new SlimeProperty("spawnZ", PropertyType.INT, 0);
        DO_DAY_CYCLE = new SlimeProperty("doDayCycle", PropertyType.BOOLEAN, true);
        DIFFICULTY = new SlimeProperty("difficulty", PropertyType.STRING, "peaceful", (value) -> {
            String difficulty = (String)value;
            return difficulty.equalsIgnoreCase("peaceful") || difficulty.equalsIgnoreCase("easy") || difficulty.equalsIgnoreCase("normal") || difficulty.equalsIgnoreCase("hard");
        });
        ALLOW_MONSTERS = new SlimeProperty("allowMonsters", PropertyType.BOOLEAN, true);
        ALLOW_ANIMALS = new SlimeProperty("allowAnimals", PropertyType.BOOLEAN, true);
        PVP = new SlimeProperty("pvp", PropertyType.BOOLEAN, true);
        ENVIRONMENT = new SlimeProperty("environment", PropertyType.STRING, "normal", (value) -> {
            String env = (String)value;
            return env.equalsIgnoreCase("normal") || env.equalsIgnoreCase("nether") || env.equalsIgnoreCase("the_end");
        });
        WORLD_TYPE = new SlimeProperty("worldtype", PropertyType.STRING, "default", (value) -> {
            String worldType = (String)value;
            return worldType.equalsIgnoreCase("default") || worldType.equalsIgnoreCase("flat") || worldType.equalsIgnoreCase("large_biomes") || worldType.equalsIgnoreCase("amplified") || worldType.equalsIgnoreCase("customized") || worldType.equalsIgnoreCase("debug_all_block_states") || worldType.equalsIgnoreCase("default_1_1");
        });
        VALUES = new SlimeProperty[]{SPAWN_X, SPAWN_Y, SPAWN_Z, DIFFICULTY, ALLOW_MONSTERS, ALLOW_ANIMALS, PVP, ENVIRONMENT, WORLD_TYPE};
    }
}
