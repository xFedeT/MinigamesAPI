package it.fedet.minigames.api.provider;

public class MinigamesProviderException extends IllegalStateException {

    private static final String MESSAGE = "The Minigames API isn't loaded yet!\nThis could be because:\n  a) the Minigames plugin is not installed or it failed to enable\n  b) the plugin in the stacktrace does not declare a dependency on Minigames\n  c) the plugin in the stacktrace is retrieving the API before the plugin 'enable' phase\n     (call the #get method in onEnable, not the constructor!)\n";

    MinigamesProviderException() {
        super("The Minigames API isn't loaded yet!\nThis could be because:\n  a) the Dictation plugin is not installed or it failed to enable\n  b) the plugin in the stacktrace does not declare a dependency on Minigames\n  c) the plugin in the stacktrace is retrieving the API before the plugin 'enable' phase\n     (call the #get method in onEnable, not the constructor!)\n");
    }

}
