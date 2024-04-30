package it.fedet.minigames;

import it.fedet.minigames.api.MinigamesAPI;
import it.fedet.minigames.api.provider.MinigamesProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinigamesCore extends JavaPlugin implements MinigamesAPI {

    public static MinigamesCore instance;

    @Override
    public void onEnable() {
        MinigamesProvider.register(this);
    }

    @Override
    public void onDisable() {
        MinigamesProvider.unregister();
    }

}
