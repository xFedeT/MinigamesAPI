package it.fedet.minigames.logger;

import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameLogger extends Logger {
    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name               A name for the logger.  This should
     *                           be a dot-separated name and should normally
     *                           be based on the package name or class name
     *                           of the subsystem, such as java.net
     *                           or javax.swing.  It may be null for anonymous Loggers.
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger.  May be null if none
     *                           of the messages require localization.
     * @throws MissingResourceException if the resourceBundleName is non-null and
     *                                  no corresponding resource can be found.
     */
    private final static String PREFIX = "[MINIGAME - %LEVEL%] ";

    public GameLogger(Logger logger) {
        super(logger.getName(), logger.getResourceBundleName());
    }

    @Override
    public void warning(String msg) {
        super.warning(PREFIX.replace("%LEVEL%", "WARNING") + msg);
    }

    @Override
    public void severe(String msg) {
        super.severe(PREFIX.replace("%LEVEL%", "ERROR") + msg);
    }

    @Override
    public void info(String msg) {
        super.info(PREFIX.replace("%LEVEL%", "INFO") + msg);
    }

    @Override
    public void fine(String msg) {
        super.fine(PREFIX.replace("%LEVEL%", "FINE") + msg);
    }

    @Override
    public void finest(String msg) {
        super.finest(PREFIX.replace("%LEVEL%", "FINEST") + msg);
    }

    @Override
    public void finer(String msg) {
        super.finer(PREFIX.replace("%LEVEL%", "FINER") + msg);
    }

    @Override
    public void config(String msg) {
        super.config(PREFIX.replace("%LEVEL%", "CONFIG") + msg);
    }



    @Override
    public void log(Level level, String msg) {
        super.log(level, PREFIX.replace("%LEVEL%", level.getName().toUpperCase()) + msg);
    }
}
