package org.springframework.samples.petclinic.player;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.user.User;

public interface PlayerRepository extends CrudRepository<Player, Integer>{

    @Query("SELECT p FROM Player p WHERE p.id = :id")
    public Optional<Player> findById(int id);

    @Query("SELECT DISTINCT p FROM Player p WHERE p.user.id = :userId")
	public Optional<Player> findByUser(int userId);


    @Query("SELECT p FROM Player p WHERE p.user.username = :username")
	public Optional<Player> findByUsername(String username);

    @Query("SELECT COUNT(p) FROM Player p")
	public Integer countAll();

    @Query("SELECT c FROM Card c WHERE c.player.id =:playerId")
    public List<Card> findCardsByPlayerId(int playerId);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    public User findUserById(int id);
}
