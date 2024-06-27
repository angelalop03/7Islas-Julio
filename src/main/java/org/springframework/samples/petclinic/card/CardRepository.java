package org.springframework.samples.petclinic.card;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardRepository extends CrudRepository<Card, Integer>{
    
    @Query("SELECT c FROM Card c WHERE c.game.id = ?1")
    public List<Card> findCardsByGameId(Integer gameId);

    @Query("SELECT c FROM Card c WHERE c.game.id = :gameId AND c.island.num = :islandNum")
    public List<Card> findCardsByGameIdAndIsland(Integer gameId, Integer islandNum);


}


