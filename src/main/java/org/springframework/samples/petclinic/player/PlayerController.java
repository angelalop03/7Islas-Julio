package org.springframework.samples.petclinic.player;

import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.util.RestPreconditions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/v1/players")
@Tag(name="Players",description = "API for the management of players")
@SecurityRequirement(name = "bearerAuth")
@RestController
public class PlayerController {

    private final PlayerService playerService;
    private final UserService userService;

    @Autowired
    public PlayerController(PlayerService playerService, UserService userService){
        this.playerService= playerService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> findAll(){
        return new ResponseEntity<>((List<Player>) playerService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "{playerId}")
	public ResponseEntity<Player> findById(@PathVariable("playerId") int id) {
		return new ResponseEntity<>(playerService.findPlayerById(id), HttpStatus.OK);
	}

    @GetMapping("user/{userId}")
    public ResponseEntity<Player> findByUser(@PathVariable("userId") int userId) {
        return new ResponseEntity<>(playerService.findPlayerByUser(userId).get(), HttpStatus.OK);
    }

    @GetMapping(value= "{playerId}/cards")
    public ResponseEntity<List<Card>> findCardsByPlayerId(@PathVariable("playerId") int id) {
        return new ResponseEntity<>(playerService.findCardsByPlayerId(id), HttpStatus.OK);
    }

    
    @PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Player> create(@RequestBody @Valid Player player) throws URISyntaxException {
		Player newPlayer = new Player();
		BeanUtils.copyProperties(player, newPlayer, "id");
		newPlayer.setImage("Estandar");
		User user = userService.findCurrentUser();
		newPlayer.setUser(user);
		Player savedPlayer = this.playerService.savePlayer(newPlayer,false);

		return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
	}   

    @PutMapping("{playerId}")
    public ResponseEntity<MessageResponse> update(@PathVariable("playerId") int id, @RequestBody @Valid PlayerEdit player){
        Player updatePlayer = playerService.findPlayerByUsername(player.getUsername()).orElse(null);
        if(updatePlayer !=null){
            if(userService.existsUserByUsername(player.getUsername()).equals(true) && updatePlayer.getId() != id){
                return ResponseEntity.badRequest().body(new MessageResponse("This user name is already taken"));
            }
        }
        RestPreconditions.checkNotNull(playerService.findPlayerById(id), "Player", "id", id);
        playerService.updatePlayer(player, id);
        return ResponseEntity.ok(new MessageResponse("Player edited!"));
    }

    @PutMapping("{userId}/connection")
    public ResponseEntity<Player> updateConnection(@PathVariable("userId") int id){
        RestPreconditions.checkNotNull(playerService.findPlayerByUser(id).get(), "Player", "id", id);
        return new ResponseEntity<>(this.playerService.updatePlayerConnection(playerService.findPlayerByUser(id).get().getId()),HttpStatus.OK);
    }

    @DeleteMapping("{playerId}")
    public ResponseEntity<MessageResponse> delete(@PathVariable("playerId") int id){
        RestPreconditions.checkNotNull(playerService.findPlayerById(id), "Player", "ID", id);
		playerService.deletePlayer(id);
        return new ResponseEntity<>(new MessageResponse("Player deleted"), HttpStatus.OK);
    }

}
