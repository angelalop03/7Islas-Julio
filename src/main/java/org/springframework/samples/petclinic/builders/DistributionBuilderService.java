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
        resetGame(deck);  

        List<Card> doblones = new ArrayList<>(deck.stream().filter(c -> c.getType() == Type.Doblon).toList());
        List<Player> players = game.getPlayers();
        List<Card> cartasEnUso = new ArrayList<>();

        for (Player p : players) {
            List<Card> cartasAsignadas = doblones.subList(0, 3);
            cartasAsignadas.forEach(c -> c.setPlayer(p));
            cartasEnUso.addAll(cartasAsignadas);
            doblones.removeAll(cartasAsignadas);
        }

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

    private void resetGame(List<Card> deck) {
        for (Card card : deck) {
            card.setPlayer(null);
            card.setIsland(null);
            card.setReversed(false);
        }
    }
}
