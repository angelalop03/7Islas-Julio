package org.springframework.samples.petclinic.builders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.card.Type;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
public class CardBuilderService {

    private final CardService cardService;

    @Autowired
    public CardBuilderService(CardService cardService) {
        this.cardService = cardService;
    }


    public void createCards(Game game) {
        List<Card> cards = new ArrayList<>();
        createDoblones(cards, game);
        createCollarMapaCorona(cards,game);
        createCalizRubiDiamante(cards,game);
        createRevolverEspadaBarril(cards,game);
        cards.forEach(cardService::saveCard);
    }

    private void createDoblones(List<Card> cards,Game game) {
        for (int i = 0; i < 27; i++) {
            Card doblon = new Card();
            doblon.setType(Type.Doblon);
            doblon.setReversed(false);
            doblon.setIsSelected(false);
            doblon.setGame(game);
            cards.add(doblon);
        }
    }
   

    private void createCollarMapaCorona(List<Card> cards,Game game) {
        for (int i = 0; i < 4; i++) {

            Card mapa = new Card();
            mapa.setType(Type.MapaDelTesoro);
            mapa.setReversed(false);
            mapa.setIsSelected(false);
            mapa.setGame(game);
            cards.add(mapa);

            Card corona = new Card();
            corona.setType(Type.Corona);
            corona.setReversed(false);
            corona.setIsSelected(false);
            corona.setGame(game);
            cards.add(corona);
            
            Card collar = new Card();
            collar.setType(Type.Collar);
            collar.setReversed(false);
            collar.setIsSelected(false);
            collar.setGame(game);
            cards.add(collar);

            
        }
    }

    private void createCalizRubiDiamante(List<Card> cards,Game game) {
        for (int i = 0; i < 3; i++) {
            Card caliz = new Card();
            caliz.setType(Type.Caliz);
            caliz.setReversed(false);
            caliz.setIsSelected(false);
            caliz.setGame(game);
            cards.add(caliz);

            Card diamante = new Card();
            diamante.setType(Type.Diamante);
            diamante.setReversed(false);
            diamante.setIsSelected(false);
            diamante.setGame(game);
            cards.add(diamante);

            Card rubi = new Card();
            rubi.setType(Type.Rubi);
            rubi.setReversed(false);
            rubi.setIsSelected(false);
            rubi.setGame(game);
            cards.add(rubi);

            
        }
    }

    private void createRevolverEspadaBarril(List<Card> cards,Game game) {
        for (int i = 0; i < 6; i++) {
            Card espada = new Card();
            espada.setType(Type.Espada);
            espada.setReversed(false);
            espada.setIsSelected(false);
            espada.setGame(game);
            cards.add(espada);

            Card barril = new Card();
            barril.setType(Type.BarrilDeRon);
            barril.setReversed(false);
            barril.setIsSelected(false);
            barril.setGame(game);
            cards.add(barril);

            Card revolver = new Card();
            revolver.setType(Type.Revolver);
            revolver.setReversed(false);
            revolver.setIsSelected(false);
            revolver.setGame(game);
            cards.add(revolver);

            
        }
    }

    

}
