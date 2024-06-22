package org.springframework.samples.petclinic.game;

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
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = GameController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class GameControllerTest {

    private static final Integer TEST_GAME_ID = 1;
    private static final Integer TEST_USER_ID = 2;
    private static final Integer TEST_PLAYER_ID = 1;
    private static final String BASE_URL = "/api/v1/games";

    @MockBean
    GameService gameService;

    @MockBean
    PlayerService playerService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Game game1;
    private Game game2;
    private Player player1;

    @BeforeEach
    void setup() {
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
        player1.setLastName("Archeron");
        player1.setEmail("FeyreArcheron@gmail.com");
        player1.setBirthdayDate(LocalDate.of(1999, 10, 14));
        player1.setRegistrationDate(LocalDate.of(2016, 8, 03));
        player1.setUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("usuario2");
        user2.setPassword("contrasenaUsuario2");
        user2.setAuthority(userAuth);

        Player player2 = new Player();
        player2.setId(2);
        player2.setFirstName("Rhysand");
        player2.setLastName("Acotar");
        player2.setEmail("RhysandAcotar@gmail.com");
        player2.setBirthdayDate(LocalDate.of(1985, 7, 3));
        player2.setRegistrationDate(LocalDate.of(2012, 9, 22));
        player2.setUser(user2);

        User user3 = new User();
        user3.setId(3);
        user3.setUsername("usuario3");
        user3.setPassword("contrasenaUsuario3");
        user3.setAuthority(userAuth);

        Player player3 = new Player();
        player3.setId(3);
        player3.setFirstName("Cassian");
        player3.setLastName("Acosf");
        player3.setEmail("CassianAcosf@gmail.com");
        player3.setBirthdayDate(LocalDate.of(1990, 1, 11));
        player3.setRegistrationDate(LocalDate.of(2018, 2, 23));
        player3.setUser(user3);

        User user4 = new User();
        user4.setId(4);
        user4.setUsername("usuario4");
        user4.setPassword("contrasenaUsuario4");
        user4.setAuthority(userAuth);

        Player player4 = new Player();
        player4.setId(4);
        player4.setFirstName("Nesta");
        player4.setLastName("Archeron");
        player4.setEmail("NestaArcheron@gmail.com");
        player4.setBirthdayDate(LocalDate.of(2000, 7, 23));
        player4.setRegistrationDate(LocalDate.of(2018, 1, 17));
        player4.setUser(user3);

        game1 = new Game();
        game1.setId(TEST_GAME_ID);
        game1.setPlayers(List.of(player1, player2, player3));
        game1.setCreator(player1);
        game1.setCode("BaSh");
        game1.setStartDate(LocalDateTime.of(2023, 12, 12, 18, 10, 0));
        game1.setEndDate(LocalDateTime.of(2023, 12, 12, 18, 45, 12));

        game2 = new Game();
        game2.setId(2);
        game2.setPlayers(List.of(player1, player2, player3));
        game2.setCreator(player2);
        game2.setCode("PlIf");
        game2.setCreateDate(LocalDateTime.of(2024, 1, 1, 10, 5, 12));

    }

    @Test
    @WithMockUser("admin")
    void shouldFindAllGames() throws Exception {
        when(this.gameService.findAll()).thenReturn(List.of(game1));
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("[?(@.id == 1)].id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindAllGamesByStatus() throws Exception {
        when(this.gameService.findByState(GameState.FINISHED)).thenReturn(List.of(game1));
        mockMvc.perform(get(BASE_URL + "/state/{state}", GameState.FINISHED))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$.[?(@.id == 1)].id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindGamesUnstartedByUserId() throws Exception {
        when(this.gameService.findGamesUnstartedByUserId(TEST_USER_ID)).thenReturn(Optional.ofNullable(game2));
        mockMvc.perform(get(BASE_URL + "/reconnectLobby/{userId}", 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindGameById() throws Exception {
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game1);
        mockMvc.perform(get(BASE_URL + "/{gameId}", TEST_GAME_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindGamesByUserId() throws Exception {
        when(this.playerService.findPlayerByUser(TEST_USER_ID)).thenReturn(Optional.ofNullable(player1));
        when(this.gameService.findGamesByUserId(TEST_USER_ID)).thenReturn(List.of(game1));
        mockMvc.perform(get(BASE_URL + "/user/{userId}", 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("[?(@.id == 1)].id").value(1));
    }


    @Test
    @WithMockUser("admin")
    void shouldFindRecentGameByUserId() throws Exception {
        when(this.gameService.findRecentGameByPlayerId()).thenReturn(game1);
        mockMvc.perform(get(BASE_URL + "/userMostRecent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    @WithMockUser("admin")
    void shouldCreateGame() throws Exception {
        mockMvc.perform(post(BASE_URL).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isCreated());

    }


    @Test
    @WithMockUser("admin")
    void shouldUpdatePlayerByCode() throws Exception {
        game1.setCode("ALgs");
        when(this.gameService.invitationByCode(game1.getCode())).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/code/{codigo}", game1.getCode())
                .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    @WithMockUser("admin")
    void shouldUpdateExitPlayer() throws Exception {
        when(this.gameService.exitUserById(TEST_GAME_ID, TEST_USER_ID)).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/game/{gameId}/exit/{userId}", 1, 2)
            .with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateStartGame() throws Exception {
        when(this.gameService.startPlayGameById(TEST_GAME_ID)).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/start/{gameId}", 1)
            .with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateTurnByGameId() throws Exception {
        when(this.gameService.updateTurnByGameId(TEST_GAME_ID)).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/{gameId}/turn", TEST_GAME_ID)
            .with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateFinishGame() throws Exception {
        when(this.gameService.finishGame(TEST_GAME_ID)).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/finish/{gameId}", TEST_GAME_ID)
            .with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateGameFinished() throws Exception {
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(new Game());
        doNothing().when(this.gameService).updateGameFinishedDependencies(TEST_GAME_ID);
        mockMvc.perform(put(BASE_URL + "/finish/{gameId}", TEST_GAME_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void shouldDelete() throws Exception {
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game1);

        doNothing().when(this.gameService).deleteGame(TEST_GAME_ID);
        mockMvc.perform(delete(BASE_URL + "/{gameId}", TEST_GAME_ID).with(csrf()))
            .andExpect(status().isOk());
    }

}
