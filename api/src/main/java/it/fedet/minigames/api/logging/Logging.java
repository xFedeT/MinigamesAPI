//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.api.logging;

import it.fedet.minigames.api.Minigame;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.api.services.Service;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logging {

    private static final String CONSOLE_PREFIX = ChatColor.GRAY + "[" + ChatColor.WHITE + "Minigames" + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + "Core" + ChatColor.GRAY + "] ";
    private static final String CONSOLE_PREFIX_SERVICE = ChatColor.GRAY + "[" + ChatColor.WHITE + "Minigames" + ChatColor.DARK_GRAY + " >> " + ChatColor.BLUE + "%service%" + ChatColor.GRAY + "] ";
    private static final String CONSOLE_PREFIX_GAME = ChatColor.GRAY + "[" + ChatColor.WHITE + "Minigames" + ChatColor.DARK_GRAY + " >> " + ChatColor.GREEN + "%service%" + ChatColor.GRAY + "] ";
    private static final String CONSOLE_PREFIX_DOUBLE_SERVICE = ChatColor.GRAY + "[" + ChatColor.WHITE + "Minigames" + ChatColor.DARK_GRAY + " >> " + ChatColor.GREEN + "%service%" + ChatColor.DARK_GRAY + " >> " + ChatColor.GOLD + "%second_service%" + ChatColor.GRAY + "] ";


    public static void info(Class<? extends Service> service, String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX_SERVICE.replace("%service%", service.getSimpleName()) + ChatColor.GRAY + message);
    }

    public static void infoGame(Class<? extends Minigame> minigame, String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX_GAME.replace("%service%", minigame.getSimpleName()) + ChatColor.GRAY + message);
    }

    public static void infoPhase(Class<? extends Minigame> minigame, Class<? extends MinigamePhase> phase, String message) {
        Bukkit.getConsoleSender().sendMessage(
                CONSOLE_PREFIX_DOUBLE_SERVICE
                        .replace("%service%", minigame.getSimpleName())
                        .replace("%second_service%", phase.getSimpleName())
                        + ChatColor.GRAY + message
        );
    }

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + ChatColor.GRAY + message);
    }

    public static void warning(Class<? extends Service> service, String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX_SERVICE.replace("%service%", service.getSimpleName()) + ChatColor.YELLOW + message);
    }

    public static void warningGame(Class<? extends Minigame> minigame, String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX_GAME.replace("%service%", minigame.getSimpleName()) + ChatColor.YELLOW + message);
    }

    public static void warningPhase(Class<? extends Minigame> minigame, Class<? extends MinigamePhase> phase, String message) {
        Bukkit.getConsoleSender().sendMessage(
                CONSOLE_PREFIX_DOUBLE_SERVICE
                        .replace("%service%", minigame.getSimpleName())
                        .replace("%second_service%", phase.getSimpleName())
                        + ChatColor.GRAY + message
        );
    }

    public static void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + ChatColor.YELLOW + message);
    }

    public static void error(Class<? extends Service> service, String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX_SERVICE.replace("%service%", service.getSimpleName()) + ChatColor.RED + message);
    }

    public static void errorGame(Class<? extends Minigame> minigame, String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX_GAME.replace("%service%", minigame.getSimpleName()) + ChatColor.RED + message);
    }

    public static void errorPhase(Class<? extends Minigame> minigame, Class<? extends MinigamePhase> phase, String message) {
        Bukkit.getConsoleSender().sendMessage(
                CONSOLE_PREFIX_DOUBLE_SERVICE
                        .replace("%service%", minigame.getSimpleName())
                        .replace("%second_service%", phase.getSimpleName())
                        + ChatColor.GRAY + message
        );
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + ChatColor.RED + message);
    }

}
