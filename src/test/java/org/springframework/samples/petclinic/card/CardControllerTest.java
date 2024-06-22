package org.springframework.samples.petclinic.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.island.Island;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CardController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
        excludeAutoConfiguration = SecurityConfiguration.class)
public class CardControllerTest {

    private static final Integer TEST_USER_ID = 2;
    private static final Integer TEST_PLAYER_ID = 1;
    private static final Integer TEST_CARD_ID = 1;
    private static final Integer TEST_GAME_ID = 1;
    private static final String BASE_URL = "/api/v1/cards";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CardService cardService;

    @MockBean
    PlayerService playerService;

   
    private Card card1;
    private Card card2;
    private Island island2;
    private Player player1;
    private CardSelected cardSelected;

    @BeforeEach
    void setup() {
        Game game1 = new Game();
        game1.setId(TEST_GAME_ID);

        Authorities userAuth = new Authorities();
        userAuth.setId(1);
        userAuth.setAuthority("player");

        User user1 = new User();
        user1.setId(TEST_USER_ID);
        user1.setUsername("usuario1");
        user1.setPassword("contrasenaUsuario1");
        user1.setAuthority(userAuth);

        player1 = new Player();
        player1.setId(TEST_PLAYER_ID);
        player1.setFirstName("Feyre");
        player1.setLastName("Acotar");
        player1.setEmail("FeyreAcotar@gmail.com");
        player1.setBirthdayDate(LocalDate.of(1998, 3, 22));
        player1.setRegistrationDate(LocalDate.of(2015, 2, 21));
        player1.setUser(user1);

        Island island1 = new Island();
        island1.setNum(1);
        island1.setGame(game1);

        island2 = new Island();
        island2.setNum(2);
        island2.setGame(game1);

        card1 = new Card();
        card1.setId(TEST_CARD_ID);
        card1.setPlayer(player1);
        card1.setReversed(false);
        card1.setIsSelected(false);
        card1.setGame(game1);
        card1.setType(Type.BarrilDeRon);

        card2 = new Card();
        card2.setId(2);
        card2.setReversed(true);
        card2.setType(Type.Caliz);
        card2.setIsSelected(false);
        card2.setGame(game1);

        cardSelected = new CardSelected();
        cardSelected.setCardsSelected(List.of());
    }

    @Test
    @WithMockUser("admin")
    void testShouldFindAll() throws Exception {
        when(cardService.findAll()).thenReturn(List.of(card1, card2));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[?(@.id == 1)].reversed").value(false))
                .andExpect(jsonPath("$[?(@.id == 2)].reversed").value(true));
    }

    @Test
    @WithMockUser("admin")
    void testShouldReturnCardById() throws Exception {
        when(cardService.findCardById(TEST_CARD_ID)).thenReturn(card1);

        mockMvc.perform(get(BASE_URL + "/{id}", TEST_CARD_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CARD_ID))
                .andExpect(jsonPath("$.reversed").value(card1.getReversed()))
                .andExpect(jsonPath("$.type").value(card1.getType().toString()));
    }

    @Test
    @WithMockUser("admin")
    void testShouldReturnCardByGameId() throws Exception {
        when(cardService.findCardsByGameId(TEST_GAME_ID)).thenReturn(List.of(card1));

        mockMvc.perform(get(BASE_URL + "/game/{id}", TEST_GAME_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[?(@.id == 1)].reversed").value(false));
    }

    @Test
    @WithMockUser("admin")
    void testShouldReturnNotFoundCard() throws Exception {
        when(cardService.findCardById(TEST_CARD_ID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/{id}", TEST_CARD_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("admin")
    void testShouldCreateCard() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(card1)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("admin")
    void testShouldUpdateCard() throws Exception {
        card1.setIsland(island2);

        when(cardService.findCardById(TEST_CARD_ID)).thenReturn(card1);
        when(playerService.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        when(cardService.updateCard(card1.getId(), TEST_PLAYER_ID)).thenReturn(card1);

        mockMvc.perform(put(BASE_URL + "/{cardId}/player/{playerId}", TEST_CARD_ID, TEST_PLAYER_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(card1)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void testShouldDeleteCard() throws Exception {
        when(cardService.findCardById(TEST_CARD_ID)).thenReturn(card1);
        doNothing().when(cardService).deleteCard(TEST_CARD_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_CARD_ID)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void testShouldUpdateSelectedCards() throws Exception {
        card1.setIsSelected(true);
        cardSelected.setCardsSelected(List.of(card1));

        when(this.cardService.findCardById(TEST_CARD_ID)).thenReturn(card1);
        when(this.playerService.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        when(this.cardService.updateSelectedCards
            (any(CardSelected.class), any(Integer.class), any(Integer.class)))
            .thenReturn(card1);


        mockMvc.perform(put(BASE_URL + "/{cardId}/player/{playerId}/selectedCards", 1,1)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cardSelected)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSelected").value(true));

    }

    @Test
    @WithMockUser("admin")
    void testShouldUpdateSelected() throws Exception {
        card1.setIsSelected(true);
        when(this.cardService.findCardById(TEST_CARD_ID)).thenReturn(card1);
        when(this.cardService.updateSelected(any(Integer.class))).thenReturn(card1);

        mockMvc.perform(put(BASE_URL + "/{cardId}/updateSelection", 1)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(card1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSelected").value(true));
    }

}
