package it.fedet.minigames.board;

import it.fedet.minigames.MinigamesCore;
import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.services.Service;
import it.fedet.minigames.game.GameService;
import me.missionary.board.BoardManager;
import me.missionary.board.provider.BoardProvider;
import me.missionary.board.settings.BoardSettings;
import me.missionary.board.settings.ScoreDirection;
import org.bukkit.entity.Player;

import java.util.List;

public class ScoreboardService implements BoardProvider, Service {

    private final MinigamesCore plugin;
    private BoardManager boardManager;

    public ScoreboardService(MinigamesCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        this.boardManager = new BoardManager(
                plugin,
                BoardSettings.builder()
                        .boardProvider(this)
                        .scoreDirection(ScoreDirection.UP)
                        .build()
        );
    }

    @Override
    public void stop() {
        this.boardManager.onDisable();
    }

    @Override
    public String getTitle(Player player) {
        Game game = plugin.getService(GameService.class).getGameBy(player);

        if (game == null)
            return "";

        return game.getCurrentPhase().getScoreboard(player).getTitle();
    }

    @Override
    public List<String> getLines(Player player) {
        Game game = plugin.getService(GameService.class).getGameBy(player);

        if (game == null)
            return List.of();

        return game.getCurrentPhase().getScoreboard(player).getLines();
    }

}
