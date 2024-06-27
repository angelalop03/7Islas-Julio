package org.springframework.samples.petclinic.player;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.card.Type;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.island.Island;
import org.springframework.samples.petclinic.island.IslandService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CardService cardService;

    @Autowired
    private GameService gameService;

    @Autowired
    private IslandService islandService;

    @Test
    void testFindAllPlayers() {
        List<Player> players = (List<Player>) playerService.findAll();
        assertEquals(4, players.size());
    }

    @Test
    void testFindPlayerById() {
        Player player = playerService.findPlayerById(1);
        assertEquals("player1", player.getUser().getUsername());
    }

    @Test
    void testFindPlayerByUserId() {
        Optional<Player> player = playerService.findPlayerByUser(4);
        assertTrue(player.isPresent());
        assertEquals("player1", player.get().getUser().getUsername());
    }

    @Test
    void testNotFindUserByPlayerId() {
        Optional<Player> player = playerService.findPlayerByUser(1);
        assertFalse(player.isPresent());
    }

    @Test
    void testFindCardsByPlayerId() {
        Game game = gameService.findGameById(2);
        Island island = new Island();
        island.setGame(game);
        island.setNum(2);
        islandService.saveIsland(island);

        Card card = new Card();
        card.setGame(game);
        card.setPlayer(playerService.findPlayerById(1));
        card.setReversed(false);
        card.setIsSelected(false);
        card.setType(Type.BarrilDeRon);
        card.setIsland(island);
        cardService.saveCard(card);

        List<Card> cards = playerService.findCardsByPlayerId(1);
        assertEquals(1, cards.size());
    }

    @Test
    void testSavePlayer() {
        Player player = playerService.findPlayerById(1);
        player.setFirstName("TEST_NAME");
        playerService.savePlayer(player, false);
        Player savedPlayer = playerService.findPlayerById(1);
        assertEquals("TEST_NAME", savedPlayer.getFirstName());
    }

    @Test
    void testUpdatePlayer() {
        Player player = playerService.findPlayerById(2);
        PlayerEdit playerEdit = new PlayerEdit(
                "TEST_USERNAME",
                "image",
                "0wn3r",
                player.getFirstName(),
                player.getLastName(),
                player.getEmail(),
                player.getBirthdayDate()
        );

        playerService.updatePlayer(playerEdit, 2);
        Player savedPlayer = playerService.findPlayerById(2);
        assertEquals("TEST_USERNAME", savedPlayer.getUser().getUsername());
    }

    @Test
    void testUpdatePlayerConnection() {
        Player player = playerService.findPlayerById(2);
        playerService.updatePlayerConnection(2);
        Player savedPlayer = playerService.findPlayerById(2);
        assertEquals(player.getIsConnected(), !savedPlayer.getIsConnected());
    }

    @Test
    void testDeletePlayer() {
        List<Player> players = (List<Player>) playerService.findAll();
        int playerCount = players.size();

        playerService.deletePlayer(2);

        List<Player> playersAfterDelete = (List<Player>) playerService.findAll();
        assertEquals(playerCount - 1, playersAfterDelete.size());
    }

    @Test
    void testFindUserById() {
        User user = playerService.findUserById(1);
        assertNotNull(user);
    }

    @Test
    void testGetNumberPlayers() {
        Map<String, Object> map = playerService.getNumbersPlayers();
        List<Player> players = (List<Player>) playerService.findAll();
        assertEquals(players.size(), map.get("total"));
    }
}
