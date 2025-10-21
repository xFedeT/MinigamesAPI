//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.fedet.minigames.swm.service.log;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logging {
    private static final String CONSOLE_PREFIX = ChatColor.GRAY + "[" + ChatColor.WHITE + "Minigames" + ChatColor.DARK_GRAY + " >> " + ChatColor.BLUE + "WorldService" + ChatColor.GRAY + "] ";

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + ChatColor.GRAY + message);
    }

    public static void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + ChatColor.YELLOW + message);
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + ChatColor.RED + message);
    }

}
