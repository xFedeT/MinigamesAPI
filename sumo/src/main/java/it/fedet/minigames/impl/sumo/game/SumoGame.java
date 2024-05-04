package it.fedet.minigames.impl.sumo.game;

import it.fedet.minigames.api.game.Game;
import it.fedet.minigames.api.game.phase.MinigamePhase;
import it.fedet.minigames.impl.sumo.Sumo;
import it.fedet.minigames.impl.sumo.game.phase.WaitingPlayerPhase;

public class SumoGame extends Game<Sumo> {

    public SumoGame(Sumo game, int gameId) {
        super(game, gameId);
    }

    @Override
    public MinigamePhase<Sumo> initialPhase() {
        return new WaitingPlayerPhase(this);
    }
}
