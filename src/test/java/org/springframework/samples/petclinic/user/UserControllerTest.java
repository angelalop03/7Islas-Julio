package org.springframework.samples.petclinic.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class UserControllerTest {
    private static final Integer TEST_USER_ID = 1;
    private static final Integer TEST_AUTH_ID = 1;
    private static final Integer TEST_PLAYER_ID = 1;
    private static final String BASE_URL = "/api/v1/users";

    @MockBean
    private AuthoritiesService as;
    @MockBean
    private UserService us;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Authorities auth;
    private Authorities auth2;
    private User user1;
    private User sara;
    private User juan;
    private Player player1;

    @BeforeEach
    void setup() {
        auth = new Authorities();
        auth.setId(TEST_AUTH_ID);
        auth.setAuthority("player");

        auth2 = new Authorities();
        auth2.setId(2);
        auth2.setAuthority("admin");

        user1 = new User();
        user1.setId(TEST_USER_ID);
        user1.setUsername("usuario1");
        user1.setPassword("contrasenaUsuario1");
        user1.setAuthority(auth);

        sara = new User();
        sara.setId(2);
        sara.setPassword("Acotar54");
        sara.setUsername("Feyre");
        sara.setAuthority(auth);

        juan = new User();
        juan.setId(3);
        juan.setPassword("Acotar54");
        juan.setUsername("Rhysand");
        juan.setAuthority(auth2);

        player1 = new Player();
        player1.setId(TEST_PLAYER_ID);
        player1.setFirstName("Feyre");
        player1.setLastName("Archeron");
        player1.setEmail("FeyreArcheron@gmail.com");
        player1.setBirthdayDate(LocalDate.of(2003, 1, 15));
        player1.setRegistrationDate(LocalDate.of(2013, 5, 16));
        player1.setUser(user1);
    }

    @Test
    @WithMockUser("admin")
    void testShouldFindAll() throws Exception {

        when(this.us.findAllPagination(any())).thenReturn(new PageImpl<>(List.of(user1, sara, juan)));

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.size()").value(3));

    }

    @Test
    @WithMockUser("admin")
    void testShouldFindAllAuths() throws Exception {

        when(this.as.findAll()).thenReturn(List.of(auth, auth2));

        mockMvc.perform(get(BASE_URL + "/authorities")).andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[?(@.id == 1)].authority").value("player"))
            .andExpect(jsonPath("$[?(@.id == 2)].authority").value("admin"));
    }

    @Test
    @WithMockUser("admin")
    void testShouldFindUserById() throws Exception {
        when(this.us.findUserById(TEST_USER_ID)).thenReturn(user1);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_USER_ID)).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(TEST_USER_ID))
            .andExpect(jsonPath("$.username").value(user1.getUsername()))
            .andExpect(jsonPath("$.authority.authority").value(user1.getAuthority().getAuthority()));
    }

    @Test
    @WithMockUser("admin")
    void testShouldFindPlayerByUserId() throws Exception {

        when(this.us.findPlayerByUserId(TEST_USER_ID)).thenReturn(player1);

        mockMvc.perform(get(BASE_URL + "/{userId}/player", TEST_USER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(TEST_PLAYER_ID));

    }

    @Test
    @WithMockUser("admin")
    void testShouldReturnNotFoundUser() throws Exception {
        when(this.us.findUserById(TEST_USER_ID)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_USER_ID)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("admin")
    void testShouldCreateUser() throws Exception {
        User aux = new User();
        aux.setUsername("Prueba");
        aux.setPassword("Prueba");
        aux.setAuthority(auth);

        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("admin")
    void testShouldUpdateUser() throws Exception {
        user1.setUsername("UPDATED");
        user1.setPassword("CHANGED");

        when(this.us.findUserById(TEST_USER_ID)).thenReturn(user1);
        when(this.us.updateUser(any(User.class), any(Integer.class))).thenReturn(user1);

        mockMvc.perform(put(BASE_URL + "/{userId}", TEST_USER_ID).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User edited successfully!"));
    }

    @Test
    @WithMockUser("admin")
    void testShouldReturnNotFoundUpdateUser() throws Exception {
        user1.setUsername("UPDATED");
        user1.setPassword("UPDATED");

        when(this.us.findUserById(TEST_USER_ID)).thenThrow(ResourceNotFoundException.class);
        when(this.us.updateUser(any(User.class), any(Integer.class))).thenReturn(user1);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user1))).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("admin")
    void testShouldDeleteOtherUser() throws Exception {

        when(this.us.findUserById(TEST_USER_ID)).thenReturn(user1);
        doNothing().when(this.us).deleteUser(TEST_USER_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_USER_ID).with(csrf())).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User deleted!"));
    }

}
