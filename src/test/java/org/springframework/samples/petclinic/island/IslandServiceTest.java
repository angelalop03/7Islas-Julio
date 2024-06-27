package org.springframework.samples.petclinic.island;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IslandServiceTest {

    @Autowired
    protected IslandService islandService;

    @Autowired
    protected GameService gameService;

    @Test
    void testShouldFindAllIslands() {
        Island island1 = createIsland(1);
        Island island2 = createIsland(2);

        
        islandService.saveIsland(island1);
        islandService.saveIsland(island2);

        
        List<Island> islands = (List<Island>) islandService.findAll();
        assertEquals(2, islands.size());
    }

    @Test
    void testShouldFindIslandById() {
        Island island = createIsland(2);
        islandService.saveIsland(island);

        Island foundIsland = islandService.findIslandById(island.getId());

        assertSame(island.getId(), foundIsland.getId());
    }

    @Test
    void testShouldNotFindIslandById() {
        assertThrows(ResourceNotFoundException.class, () -> islandService.findIslandById(0));
    }

    @Test
    void testShouldSaveIsland() {
        Island island = createIsland(1);

        Island savedIsland = islandService.saveIsland(island);

        assertNotNull(savedIsland.getId());
    }

    @Test
    void testShouldFindCardsByIslandId() {
        Island island = createIsland(2);
        islandService.saveIsland(island);

        List<Card> cards = islandService.findCardsByIslandId(island.getId());

        assertEquals(0, cards.size());
    }

    @Test
    void testShouldDeleteIsland() {
        Island island = createIsland(2);
        islandService.saveIsland(island);
        List<Island> islandListCreated = (List<Island>) islandService.findAll();

        islandService.deleteIsland(island.getId());
        List<Island> islandListDeleted = (List<Island>) islandService.findAll();

        assertEquals(islandListCreated.size() - 1, islandListDeleted.size());
    }

    @Test
    void testShouldFindIslandsByGameId() {
        Island island1 = createIsland(2);
        Island island2 = createIsland(2);
        islandService.saveIsland(island1);
        islandService.saveIsland(island2);

        List<Island> islands = islandService.findIslandsByGameId(2);

        assertEquals(2, islands.size());
    }

    private Island createIsland(int num) {
        Island island = new Island();
        island.setGame(gameService.findGameById(2));
        island.setNum(num);
        return island;
    }
}
