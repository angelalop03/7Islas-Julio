package org.springframework.samples.petclinic.island;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.Type;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = IslandController.class)
public class IslandControllerTest {

    private static final String BASE_URL = "/api/v1/islands";
    private static final Integer TEST_GAME_ID = 1;
    private static final Integer TEST_ISLAND_ID = 1;

    @MockBean
    private IslandService islandService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Island island1;
    private Island island2;
    private Card card1;
    private Card card2;

    @BeforeEach
    void setup() {
        Game game1 = new Game();
        game1.setId(TEST_GAME_ID);

        island1 = new Island();
        island1.setGame(game1);
        island1.setId(TEST_ISLAND_ID);
        island1.setNum(1);

        island2 = new Island();
        island2.setGame(game1);
        island2.setId(2);
        island2.setNum(2);

        card1 = new Card();
        card1.setId(1);
        card1.setGame(game1);
        card1.setType(Type.Doblon);
        card1.setReversed(false);
        card1.setIsSelected(true);
        card1.setIsland(island1);

        card2 = new Card();
        card2.setId(2);
        card2.setGame(game1);
        card2.setType(Type.Caliz);
        card2.setReversed(true);
        card2.setIsSelected(false);
        card2.setIsland(island2);
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldFindAllIslands() throws Exception {
        when(islandService.findAll()).thenReturn(List.of(island1, island2));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].num").value(1))
                .andExpect(jsonPath("$.[1].num").value(2));
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldFindIslandById() throws Exception {
        when(islandService.findIslandById(TEST_ISLAND_ID)).thenReturn(island1);

        mockMvc.perform(get(BASE_URL + "/{islandId}", TEST_ISLAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.num").value(1))
                .andExpect(jsonPath("$.game.id").value(TEST_GAME_ID));
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldFindIslandsByGameId() throws Exception {
        when(islandService.findIslandsByGameId(TEST_GAME_ID)).thenReturn(List.of(island1, island2));

        mockMvc.perform(get(BASE_URL + "/game/{gameId}", TEST_GAME_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].num").value(1))
                .andExpect(jsonPath("$.[1].num").value(2));
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldFindCardsByIslandId() throws Exception {
        when(islandService.findCardsByIslandId(TEST_ISLAND_ID)).thenReturn(List.of(card1));

        mockMvc.perform(get(BASE_URL + "/{islandId}/cards", TEST_ISLAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].reversed").value(false));
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldCreateIsland() throws Exception {
        mockMvc.perform(post(BASE_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(island1)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldDeleteIsland() throws Exception {
        when(islandService.findIslandById(TEST_ISLAND_ID)).thenReturn(island1);
        doNothing().when(islandService).deleteIsland(TEST_ISLAND_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ISLAND_ID).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldNotFindIslandById() throws Exception {
        when(islandService.findIslandById(TEST_ISLAND_ID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/{islandId}", TEST_ISLAND_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldNotFindIslandsByGameId() throws Exception {
        when(islandService.findIslandsByGameId(TEST_GAME_ID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/game/{gameId}", TEST_GAME_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldNotFindCardsByIslandId() throws Exception {
        when(islandService.findCardsByIslandId(TEST_ISLAND_ID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/{islandId}/cards", TEST_ISLAND_ID))
                .andExpect(status().isNotFound());
    }
}
