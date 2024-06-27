package org.springframework.samples.petclinic.player;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder encoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, GameRepository gameRepository,UserRepository userRepository, UserService userService,PasswordEncoder encoder) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.encoder = encoder;


    }

    @Transactional(readOnly = true)
    public Iterable<Player> findAll() throws DataAccessException{
        return playerRepository.findAll();
    }

   
    @Transactional(readOnly = true)
    public Optional<Player> findPlayerByUsername(String username) throws DataAccessException{
        return playerRepository.findByUsername(username);
    }
    @Transactional(readOnly = true)
    public Player findPlayerById(int id) throws DataAccessException{
        return playerRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public Optional<Player> findPlayerByUser(int userId) throws DataAccessException{
        return playerRepository.findByUser(userId);
    }

    @Transactional(readOnly = true)
	public List<Card> findCardsByPlayerId(int playerID) {
		return this.playerRepository.findCardsByPlayerId(playerID);
	}

    @Transactional
	public Player savePlayer(Player player, Boolean update) throws DataAccessException {
		if (player.getIsConnected() == null) {
			player.setIsConnected(true);
		}
		playerRepository.save(player);
		return player;
	}
    //CREATE STAT
    //ASIGANR LOGROS  
    //updatePlayerForAchievements  

    @Transactional
    public Player updatePlayer(PlayerEdit player, int id) throws DataAccessException{
        Player updatePlayer = findPlayerById(id);
        LocalDate registrationDate = updatePlayer.getRegistrationDate();
        BeanUtils.copyProperties(player,updatePlayer,"username","password");
        updatePlayer.setRegistrationDate(registrationDate);

        User updateUser = userService.findUserById(updatePlayer.getUser().getId());
        if (!updateUser.getPassword().equals(player.getPassword())) {
			player.setPassword(encoder.encode(player.getPassword()));
			BeanUtils.copyProperties(player, updateUser, "id");
		} else { 
			BeanUtils.copyProperties(player, updateUser, "firstName", "image", 
				"lastName", "birthdayDate", "email", "password");
		}
        userRepository.save(updateUser);
        return savePlayer(updatePlayer, true);
    }

   
    @Transactional
    public void deletePlayer(int id) throws DataAccessException{
        Player player = findPlayerById(id);
        Integer userId = player.getUser().getId();
        User user =playerRepository.findUserById(userId);
        List<Game> games = gameRepository.findGamesByPlayerId(id);
        if(!games.isEmpty()){
            for(Game game: games){
                game.getPlayers().remove(player);
                gameRepository.save(game);
            }
        }

        playerRepository.delete(player);
        userRepository.delete(user);
    }

    @Transactional
	public Player updatePlayerTurn(Player player, Integer id) {
		Player playerToUpdate = findPlayerById(id);
		BeanUtils.copyProperties(player, playerToUpdate, "id");
		return playerRepository.save(playerToUpdate);
	}

    @Transactional
    public Player updatePlayerConnection(Integer id){
        Player playerToUpdate = findPlayerById(id);
        playerToUpdate.setIsConnected(!playerToUpdate.getIsConnected());
        return playerRepository.save(playerToUpdate);
    }


    @Transactional(readOnly = true)
    public Map<String, Object> getNumbersPlayers(){
        Map<String , Object> numbers= new HashMap<>();
        numbers.put("total", playerRepository.countAll());
        return numbers;
    }

    public User findUserById(int i) {
        return userRepository.findById(i).get();
    }

    


}
