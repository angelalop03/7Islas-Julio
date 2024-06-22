package org.springframework.samples.petclinic.player;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.Type;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.island.Island;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PlayerController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class PlayerControllerTest {
    private static final Integer TEST_PLAYER_ID = 1;
    private static final Integer TEST_USER_ID = 2;
    private static final String BASE_URL = "/api/v1/players";

    @MockBean
    PlayerService ps;
    @MockBean
    UserService us;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Player player1;
    private Player player2;
    private Player player3;
    private User user1;

    private Card card1;

    private PlayerEdit editplayer;

    @BeforeEach
    void setup() {
        Authorities userAuth = new Authorities();
        userAuth.setId(1);
        userAuth.setAuthority("player");

        user1 = new User();
        user1.setId(TEST_USER_ID);
        user1.setUsername("usuario1");
        user1.setPassword("contrasenaUsuario1");
        user1.setAuthority(userAuth);

        player1 = new Player();
        player1.setId(TEST_PLAYER_ID);
        player1.setFirstName("Feyre");
        player1.setLastName("Acotar");
        player1.setEmail("FeyreAcotar@gmail.com");
        player1.setBirthdayDate(LocalDate.of(1999, 1, 14));
        player1.setRegistrationDate(LocalDate.of(2014, 3, 17));
        player1.setUser(user1);
        player1.setImage("Default");
        player1.setIsConnected(false);

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("usuario2");
        user2.setPassword("contrasenaUsuario2");
        user2.setAuthority(userAuth);

        player2 = new Player();
        player2.setId(2);
        player2.setFirstName("Rhysand");
        player2.setLastName("Acotar");
        player2.setEmail("RhysandAcotar@gmail.com");
        player2.setBirthdayDate(LocalDate.of(1987, 7, 2));
        player2.setRegistrationDate(LocalDate.of(2016, 1, 10));
        player2.setUser(user2);

        User user3 = new User();
        user3.setId(3);
        user3.setUsername("usuario3");
        user3.setPassword("contrasenaUsuario3");
        user3.setAuthority(userAuth);

        player3 = new Player();
        player3.setId(3);
        player3.setFirstName("Cassian");
        player3.setLastName("Sombras");
        player3.setEmail("CassianAcosf@gmail.com");
        player3.setBirthdayDate(LocalDate.of(1997, 10, 14));
        player3.setRegistrationDate(LocalDate.of(2017, 1, 22));
        player3.setUser(user3);

        editplayer = new PlayerEdit();
        editplayer.setFirstName(player1.getFirstName());
        editplayer.setLastName(player1.getLastName());
        editplayer.setEmail(player1.getEmail());
        editplayer.setUsername(player1.getUser().getUsername());
        editplayer.setPassword(player1.getUser().getPassword());
        editplayer.setBirthdayDate(player1.getBirthdayDate());

        Game game1 = new Game();
        game1.setId(1);
        game1.setCode("BsaA");

        Island island1 = new Island();
        island1.setId(1);
        island1.setNum(1);

        card1 = new Card();
        card1.setPlayer(player1);
        card1.setIsSelected(false);
        card1.setReversed(false);
        card1.setGame(game1);
        card1.setType(Type.BarrilDeRon);
        card1.setId(1);
        card1.setIsland(island1);

    }

    @Test
    @WithMockUser("admin")
    void adminShouldFindAll() throws Exception {
        when(this.ps.findAll()).thenReturn(List.of(player1, player2, player3));
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[?(@.id == 1)].firstName").value("Feyre"))
                .andExpect(jsonPath("$[?(@.id == 2)].firstName").value("Rhysand"))
                .andExpect(jsonPath("$[?(@.id == 3)].firstName").value("Cassian"));
    }

    @Test
    @WithMockUser("player")
    void playerShouldReturnPlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PLAYER_ID)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_PLAYER_ID))
                .andExpect(jsonPath("$.firstName").value(player1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(player1.getLastName()))
                .andExpect(jsonPath("$.email").value(player1.getEmail()));
    }

    @Test
    @WithMockUser("admin")
    void adminShouldReturnPlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PLAYER_ID)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_PLAYER_ID))
                .andExpect(jsonPath("$.firstName").value(player1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(player1.getLastName()))
                .andExpect(jsonPath("$.email").value(player1.getEmail()));
    }

    @Test
    @WithMockUser("player")
    void playerShouldReturnNotFoundPlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PLAYER_ID)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("player")
    void adminShouldReturnNotFoundPlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PLAYER_ID)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("player")
    void playerShouldCreatePlayer() throws Exception {
        Player player = new Player();
        player.setFirstName("Azriel");
        player.setLastName("Sombras");
        player.setEmail("AzrielSombras@gmail.com");
        player.setId(TEST_PLAYER_ID);
        player.setRegistrationDate(LocalDate.of(2017, 10, 14));
        player.setBirthdayDate(LocalDate.of(1999, 7, 06));
        player.setUser(user1);
        player.setImage("Default");
        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("admin")
    void shouldFindCardsByPlayerId() throws Exception {
        when(this.ps.findCardsByPlayerId(TEST_PLAYER_ID)).thenReturn(List.of(card1));

        mockMvc.perform(get(BASE_URL + "/{playerId}/cards", TEST_PLAYER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$.[?(@.id == 1)].id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void adminShouldCreatePlayer() throws Exception {
        Player player = new Player();
        player.setFirstName("Elain");
        player.setLastName("Archeron");
        player.setEmail("ElainArcheron@gmail.com");
        player.setId(TEST_PLAYER_ID);
        player.setRegistrationDate(LocalDate.of(2025, 11, 10));
        player.setBirthdayDate(LocalDate.of(2003, 1, 4));
        player.setUser(user1);
        player.setImage("Default");

        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("player")
    void shouldUpdatePlayer() throws Exception {
        player1.setFirstName("UPDATED");
        player1.setEmail("updatedMail@gmail.com");
        player1.setImage("Default");


        editplayer.setFirstName(player1.getFirstName());
        editplayer.setEmail(player1.getEmail());
        editplayer.setImage(player1.getImage());

        when(ps.findPlayerByUsername(player1.getUser().getUsername())).thenReturn(Optional.ofNullable(player1));
        when(ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        when(ps.updatePlayer(any(PlayerEdit.class), any(Integer.class))).thenReturn(player1);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_PLAYER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editplayer))).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Player edited!"));
    }

    @Test
    @WithMockUser("player")
    void shouldReturnNotFoundUpdateOwner() throws Exception {
        player1.setFirstName("UPDATED");
        player1.setLastName("UPDATED");

        editplayer.setFirstName(player1.getFirstName());
        editplayer.setEmail(player1.getEmail());
        editplayer.setImage("Default");

        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenThrow(ResourceNotFoundException.class);
        when(this.ps.updatePlayer(any(PlayerEdit.class), any(Integer.class))).thenReturn(player1);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_PLAYER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editplayer))).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateConnection() throws Exception {
        player1.setIsConnected(true);

        when(this.ps.findPlayerByUser(TEST_USER_ID)).thenReturn(Optional.ofNullable(player1));
        when(this.ps.updatePlayerConnection(any(Integer.class))).thenReturn(player1);

        mockMvc.perform(put(BASE_URL + "/{userId}/connection", TEST_USER_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isConnected").value(true));
    }

    @Test
    @WithMockUser("player")
    void playerShouldDeletePlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);

        doNothing().when(this.ps).deletePlayer(TEST_PLAYER_ID);
        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_PLAYER_ID).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void adminShouldDeletePlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);

        doNothing().when(this.ps).deletePlayer(TEST_PLAYER_ID);
        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_PLAYER_ID).with(csrf()))
                .andExpect(status().isOk());
    }

}
