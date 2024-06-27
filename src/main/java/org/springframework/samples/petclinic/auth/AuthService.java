package org.springframework.samples.petclinic.auth;

import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.auth.payload.request.SignupRequest;
import org.springframework.samples.petclinic.auth.payload.request.SignupRequestAdmin;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.AuthoritiesService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final PasswordEncoder encoder;
    private final AuthoritiesService authoritiesService;
    private final UserService userService;
    private final PlayerService playerService;

    @Autowired
    public AuthService(PasswordEncoder encoder, AuthoritiesService authoritiesService, UserService userService,
            PlayerService playerService) {
        this.encoder = encoder;
        this.authoritiesService = authoritiesService;
        this.userService = userService;
        this.playerService = playerService;
    }

    @Transactional
    public void createUser(@Valid SignupRequest request) {
        Authorities role;
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        role = authoritiesService.findByAuthority("PLAYER");
        user.setAuthority(role);
        userService.saveUser(user);
        Player player = new Player();
        player.setFirstName(request.getFirstName());
        player.setLastName(request.getLastName());
        player.setRegistrationDate(LocalDate.now());
        player.setBirthdayDate(request.getBirthdayDate());
        player.setEmail(request.getEmail());
        player.setImage("Estandar");
        player.setUser(user);
        playerService.savePlayer(player,false);
    }

    @Transactional
    public void createAdmin(@Valid SignupRequestAdmin request) {
        Authorities role;
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        role = authoritiesService.findByAuthority("ADMIN");
        user.setAuthority(role);
        userService.saveUser(user);
    }

}
