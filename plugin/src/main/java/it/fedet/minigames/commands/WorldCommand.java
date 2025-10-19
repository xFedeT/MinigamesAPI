package it.fedet.minigames.commands;


import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.commands.GameCommand;
import it.fedet.minigames.world.service.WorldService;
import it.fedet.minigames.world.uploader.WorldUploader;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("world")
@CommandPermission("minigames.world.admin")
public class WorldCommand implements GameCommand {

    private final MinigamesCore plugin;

    public WorldCommand(MinigamesCore plugin) {
        this.plugin = plugin;
    }

    @Subcommand("load")
    public void loadWorld(CommandSender sender, String worldName) {
        WorldService worldService = plugin.getService(WorldService.class);

        sender.sendMessage(ChatColor.YELLOW + "Loading world: " + worldName + "...");

        worldService.loadWorld(worldName)
                .thenAccept(world -> {
                    sender.sendMessage(ChatColor.GREEN + "World loaded successfully: " + world.getName());
                })
                .exceptionally(e -> {
                    sender.sendMessage(ChatColor.RED + "Failed to load world: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("loadgame")
    public void loadWorldForGame(CommandSender sender, String worldName, int gameId) {
        WorldService worldService = plugin.getService(WorldService.class);

        sender.sendMessage(ChatColor.YELLOW + "Loading world for game #" + gameId + ": " + worldName + "...");

        worldService.loadWorldForGame(worldName, gameId)
                .thenAccept(world -> {
                    sender.sendMessage(ChatColor.GREEN + "World loaded successfully: " + world.getName());

                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.teleport(world.getSpawnLocation());
                    }
                })
                .exceptionally(e -> {
                    sender.sendMessage(ChatColor.RED + "Failed to load world: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("unload")
    public void unloadWorld(CommandSender sender, String worldName) {
        WorldService worldService = plugin.getService(WorldService.class);

        sender.sendMessage(ChatColor.YELLOW + "Unloading world: " + worldName + "...");

        worldService.unloadWorld(worldName)
                .thenRun(() -> {
                    sender.sendMessage(ChatColor.GREEN + "World unloaded successfully: " + worldName);
                })
                .exceptionally(e -> {
                    sender.sendMessage(ChatColor.RED + "Failed to unload world: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("exists")
    public void checkWorldExists(CommandSender sender, String worldName) {
        WorldService worldService = plugin.getService(WorldService.class);

        worldService.worldExists(worldName)
                .thenAccept(exists -> {
                    if (exists) {
                        sender.sendMessage(ChatColor.GREEN + "World '" + worldName + "' exists in storage");
                    } else {
                        sender.sendMessage(ChatColor.RED + "World '" + worldName + "' does not exist in storage");
                    }
                })
                .exceptionally(e -> {
                    sender.sendMessage(ChatColor.RED + "Failed to check world: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("upload")
    public void uploadWorld(CommandSender sender, String worldName) {
        WorldService worldService = plugin.getService(WorldService.class);

        sender.sendMessage(ChatColor.YELLOW + "Uploading world to storage: " + worldName + "...");
        sender.sendMessage(ChatColor.YELLOW + "This may take a while...");

        WorldUploader uploader = new WorldUploader(
                worldService.getStorageProvider(),
                plugin.getLogger()
        );

        uploader.uploadWorld(worldName)
                .thenRun(() -> {
                    sender.sendMessage(ChatColor.GREEN + "World uploaded successfully: " + worldName);
                })
                .exceptionally(e -> {
                    sender.sendMessage(ChatColor.RED + "Failed to upload world: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("save")
    public void saveWorld(CommandSender sender, String worldName) {
        WorldService worldService = plugin.getService(WorldService.class);

        org.bukkit.World world = plugin.getServer().getWorld(worldName);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "World not loaded: " + worldName);
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Saving world to storage: " + worldName + "...");

        worldService.saveWorld(world)
                .thenRun(() -> {
                    sender.sendMessage(ChatColor.GREEN + "World saved successfully: " + worldName);
                })
                .exceptionally(e -> {
                    sender.sendMessage(ChatColor.RED + "Failed to save world: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("tp")
    public void teleportToWorld(Player player, String worldName) {
        org.bukkit.World world = plugin.getServer().getWorld(worldName);

        if (world == null) {
            player.sendMessage(ChatColor.RED + "World not loaded: " + worldName);
            return;
        }

        player.teleport(world.getSpawnLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported to world: " + worldName);
    }
}