package org.springframework.samples.petclinic.builders;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.stereotype.Service;


@Service
public class TurnBuilderService {

    private final PlayerService playerService;

    @Autowired
    public TurnBuilderService(PlayerService playerService) {
        this.playerService = playerService;
    }


    public void distributionTurns(Game game) {
        List<Player> players = game.getPlayers();
        Collections.shuffle(players);
        players.get(0).setTurn(true);
        for(int i=1; i<players.size(); i++) {
            players.get(i).setTurn(false);
        }
        for(Player p: players) {
            playerService.updatePlayerTurn(p, p.getId());
        }
    }
    
}
