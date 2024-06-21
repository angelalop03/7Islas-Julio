package org.springframework.samples.petclinic.builders;


import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.Type;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.island.Island;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class DistributionBuilderService {

    public void distributeCards(Game game, List<Card> deck, List<Island> islas) {
        // Reparte 3 doblones a cada jugador
        List<Card> doblones= new ArrayList<>(deck.stream().filter(c -> c.getType() == Type.Doblon).toList());
        List<Player> players = game.getPlayers();
        List<Card> cartasEnUso = new ArrayList<>();

        for (Player p : players) {
            List<Card> cartasAsignadas = doblones.subList(0, 3);
            cartasAsignadas.forEach(c -> c.setPlayer(p));
            cartasEnUso.addAll(cartasAsignadas);
            doblones.removeAll(cartasAsignadas);
        }

        // Reparte una carta boca arriba en cada isla (1-6) y el resto de cartas boca abajo en la isla 7
        deck.removeAll(cartasEnUso);
        Collections.shuffle(deck);

        for (Island i : islas.subList(0, 6)) {
            Card cartaAsignada = deck.get(0);
            cartaAsignada.setIsland(i);
            cartaAsignada.setReversed(true);
            deck.remove(cartaAsignada);
        }

        deck.forEach(c -> c.setIsland(islas.get(6)));
    }

}
