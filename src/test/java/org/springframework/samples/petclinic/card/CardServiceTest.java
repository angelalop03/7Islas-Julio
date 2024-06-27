package org.springframework.samples.petclinic.card;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.island.Island;
import org.springframework.samples.petclinic.island.IslandService;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CardServiceTest {

    @Autowired
    protected IslandService islandService;

    @Autowired
    protected PlayerService playerService;

    @Autowired
    protected CardService cardService;

    @Autowired
    protected GameService gameService;

    @Test
    void testFindAll() {
        Card carta1 = createCard(Type.Caliz, false, true);
        Card carta2 = createCard(Type.Revolver, false, true);

        cardService.saveCard(carta1);
        cardService.saveCard(carta2);

        List<Card> cards = (List<Card>) cardService.findAll();

        assertEquals(2, cards.size());
    }

    @Test
    void testFindCardById() {
        Island newIsland = createIsland(2, 100);
        islandService.saveIsland(newIsland);

        Card card = createCard(Type.BarrilDeRon, false, true);
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);

        cardService.saveCard(card);

        Card createdCard = cardService.findCardById(card.getId());
        assertEquals(card.getId(), createdCard.getId());
    }

    @Test
    void testShouldntfindCardById() {
        assertThrows(ResourceNotFoundException.class, () -> cardService.findCardById(10000));
    }

    @Test
    void testSaveCard() {
        Island newIsland = createIsland(2, 100);
        islandService.saveIsland(newIsland);

        Card card = createCard(Type.BarrilDeRon, false, true);
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);

        cardService.saveCard(card);

        Card createdCard = cardService.findCardById(card.getId());
        assertEquals(card.getId(), createdCard.getId());
    }

    @Test
    void testDeleteCard() {
        Island newIsland = createIsland(2, 100);
        islandService.saveIsland(newIsland);

        Card card = createCard(Type.BarrilDeRon, false, true);
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);

        cardService.saveCard(card);

        List<Card> cardsCreated = (List<Card>) cardService.findAll();
        cardService.deleteCard(cardsCreated.get(0).getId());
        List<Card> cardsDeleted = (List<Card>) cardService.findAll();
        assertEquals(cardsCreated.size() - 1, cardsDeleted.size());
    }

    @Test
    void testFindCardByGameId() {
        Card card = createCard(Type.BarrilDeRon, false, true);
        card.setGame(gameService.findGameById(2));

        cardService.saveCard(card);

        assertEquals(1, cardService.findCardsByGameId(2).size());
    }

    @Test
    void testShouldUpdateCard() {
        Island newIsland = createIsland(2, 100);
        islandService.saveIsland(newIsland);

        Card card = createCard(Type.BarrilDeRon, false, true);
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);

        cardService.saveCard(card);

        Card createdCard = cardService.findCardById(card.getId());

        card.setIsSelected(false);
        cardService.updateCard(createdCard.getId(), 2);
    }

    @Test
    void testShouldUpdateSelectedCard() {
        Island newIsland = createIsland(2, 100);
        islandService.saveIsland(newIsland);

        Card card = createCard(Type.BarrilDeRon, false, true);
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);

        cardService.saveCard(card);
        Card createdCard = cardService.findCardById(card.getId());

        cardService.updateSelected(createdCard.getId());
        Card updateCard = cardService.findCardById(card.getId());

        assertEquals(card.getIsSelected(), !updateCard.getIsSelected());
    }

    @Test
    void testShouldcogerCartaMazo() {
        Island newIsland = createIsland(2, 100);
        islandService.saveIsland(newIsland);

        Card card = createCard(Type.BarrilDeRon, false, true);
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);

        cardService.saveCard(card);

        List<Card> cards = cardService.findCardsByGameId(2);
        CardSelected selectedCardS = new CardSelected(cards);
        cardService.updateSelectedCards(selectedCardS, card.getId(), 2);

        List<Card> updatedCards = cardService.findCardsByGameId(2);
        for (Card updatedCard : updatedCards) {
            Assertions.assertNull(updatedCard.getPlayer(), "El jugador de la carta seleccionada no se estableció como null.");
        }
    }

    @Test
    void testShouldUpdateSelected() {
        Island newIsland = createIsland(2, 100);
        islandService.saveIsland(newIsland);

        Card card = createCard(Type.BarrilDeRon, false, true);
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);

        cardService.saveCard(card);

        cardService.updateSelected(card.getId());
        Card newCard = cardService.findCardById(card.getId());
        assertEquals(card.getIsSelected(), !newCard.getIsSelected());
    }

    private Island createIsland(int game_id, int island_id) {
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(game_id));
        newIsland.setNum(island_id);
        newIsland.setId(island_id);
        return newIsland;
    }

    private Card createCard(Type type, boolean reversed, boolean isSelected) {
        Card card = new Card();
        card.setType(type);
        card.setReversed(reversed);
        card.setIsSelected(isSelected);
        return card;
    }
}
