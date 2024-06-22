package org.springframework.samples.petclinic.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthoritiesService authService;

    @Test
    @WithMockUser(username = "admin1", password = "4dm1n")
    void testShouldFindCurrentUser() {
        User user = this.userService.findCurrentUser();
        assertEquals("admin1", user.getUsername());
    }

    @Test
    @WithMockUser(username = "userTest")
    void testShouldNotFindCorrectCurrentUser() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
    }

    @Test
    void testShouldNotFindAuthenticated() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
    }

    @Test
    void testShouldFindAllUsers() {
        List<User> users = (List<User>) this.userService.findAll();
        assertEquals(7, users.size());
    }

    @Test
    void testShouldFindUsersByUsername() {
        Optional<User> optionalUser = this.userService.findUserByUsername("admin1");
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        assertEquals("admin1", user.getUsername());
    }

    @Test
    void testShouldFindUsersByAuthority() {
        List<User> users = (List<User>) this.userService.findAllUserByAuthority("ADMIN");
        assertEquals(3, users.size());

        List<User> admins = (List<User>) this.userService.findAllUserByAuthority("PLAYER");
        assertEquals(4, admins.size());
    }

    @Test
    void testShouldNotFindUserByIncorrectUsername() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUserByUsername("usernamenotexists"));
    }

    @Test
    void testShouldFindPlayerByUsername() {
        Player player = this.userService.findPlayerByUsername("player1");
        assertEquals("player1", player.getUser().getUsername());
    }

    @Test
    void testShouldNotFindPlayerWithBadUsername() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findPlayerByUsername("badusername"));
    }

    @Test
    void testShouldFindPlayerByUserId() {
        Player player = this.userService.findPlayerByUserId(4);
        assertEquals("player1", player.getUser().getUsername());
    }

    @Test
    void testShouldNotFindPlayerWithBadUserId() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findPlayerByUserId(100));
    }

    @Test
    void testShouldFindUser() {
        User user = this.userService.findUserById(1);
        assertEquals("admin1", user.getUsername());
    }

    @Test
    void testShouldNotFindUserWithBadID() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUserById(100));
    }

    @Test
    void testShouldExistUser() {
        assertEquals(true, this.userService.existsUserByUsername("admin1"));
    }

    @Test
    void testShouldNotExistUser() {
        assertEquals(false, this.userService.existsUserByUsername("player10000"));
    }

    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void testPlayerCanUpdatePlayer() {
        Optional<User> optionalUser = this.userService.findUserByUsername("player1");
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        user.setUsername("Change");
        userService.updateUser(user, user.getId());
        user = this.userService.findUserById(user.getId());
        assertEquals("Change", user.getUsername());
    }

    @Test
    @Transactional
    @WithMockUser(username = "player2", password = "0wn3r")
    void testPlayerCantUpdateOtherPlayer() {
        Optional<User> optionalUserToUpdate = this.userService.findUserByUsername("player1");
        assertTrue(optionalUserToUpdate.isPresent());
        User user = optionalUserToUpdate.get();
        assertThrows(AccessDeniedException.class, () -> this.userService.updateUser(user, user.getId()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin1", password = "4dm1n")
    void testAdminCanUpdatePlayer() {
        Optional<User> optionalUserToUpdate = userService.findUserByUsername("player1");
        assertTrue(optionalUserToUpdate.isPresent());
        User userToUpdate = optionalUserToUpdate.get();
        userToUpdate.setUsername("Change");
        userService.updateUser(userToUpdate, userToUpdate.getId());
        userToUpdate = this.userService.findUserById(userToUpdate.getId());
        assertEquals("Change", userToUpdate.getUsername());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin1", password = "4dm1n")
    void testAdminCantUpdateOtherAdmin() {
        Optional<User> optionalAdminToUpdate = userService.findUserByUsername("admin2");
        assertTrue(optionalAdminToUpdate.isPresent());
        User user = optionalAdminToUpdate.get();
        assertThrows(AccessDeniedException.class, () -> this.userService.updateUser(user, user.getId()));
    }

    @Test
    @Transactional
    void testShouldCreateUser() {
        int count = ((Collection<User>) this.userService.findAll()).size();

        User user = new User();
        user.setUsername("Sam");
        user.setPassword("password");
        user.setAuthority(authService.findByAuthority("ADMIN"));
        this.userService.saveUser(user);
        assertNotEquals(0, user.getId().longValue());
        assertNotNull(user.getId());

        int finalCount = ((Collection<User>) this.userService.findAll()).size();
        assertEquals(count + 1, finalCount);
    }

    @Test
    @Transactional
    @WithMockUser(username = "player1", password = "0wn3r")
    void testPlayerCanDeleteOwnPlayer() {
        Integer firstCount = ((Collection<User>) userService.findAll()).size();
        Optional<User> optionalUserToDelete = userService.findUserByUsername("player1");
        assertTrue(optionalUserToDelete.isPresent());
        User userToDelete = optionalUserToDelete.get();
        userService.deleteUser(userToDelete.getId());
        int lastCount = ((Collection<User>) userService.findAll()).size();
        assertEquals(firstCount, lastCount + 1);
    }

    @Test
    @Transactional
    @WithMockUser(username = "player2", password = "0wn3r")
    void testPlayerCantDeleteOtherPlayer() {
        Optional<User> optionalUser = userService.findUserByUsername("player1");
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        assertThrows(AccessDeniedException.class, () -> this.userService.deleteUser(user.getId()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin1", password = "4dm1n")
    void testAdminCanDeleteOtherPlayer() {
        Integer firstCount = ((Collection<User>) userService.findAll()).size();
        Optional<User> optionalUserToDelete = userService.findUserByUsername("player1");
        assertTrue(optionalUserToDelete.isPresent());
        User userToDelete = optionalUserToDelete.get();
        userService.deleteUser(userToDelete.getId());
        int lastCount = ((Collection<User>) userService.findAll()).size();
        assertEquals(firstCount, lastCount + 1);
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin1", password = "4dm1n")
    void testAdminCantDeleteOtherAdmin() {
        Optional<User> optionalAdmin = userService.findUserByUsername("admin2");
        assertTrue(optionalAdmin.isPresent());
        User user = optionalAdmin.get();
        assertThrows(AccessDeniedException.class, () -> this.userService.deleteUser(user.getId()));
    }

    @Test
    void testShouldFindAllPagination() {
        Page<User> pageUser = userService.findAllPagination(Pageable.ofSize(3));
        assertEquals(pageUser.get().toList().size(), 3);
    }

    @Test
    void testShouldFindAllUserByAuthorityPagination() {
        Page<User> pageUser = userService.findAllUserByAuthorityPagination("ADMIN", Pageable.ofSize(1));
        assertEquals(pageUser.get().toList().size(), 1);
    }
}
