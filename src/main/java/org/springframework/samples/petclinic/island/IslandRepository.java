package org.springframework.samples.petclinic.island;

import java.util.List;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.card.Card;

public interface IslandRepository extends CrudRepository<Island,Integer>  {

    @Query("SELECT i FROM Island i WHERE i.game.id =:id")
    public List<Island> findByGameId(Integer id);

    @Query("SELECT c FROM Card c WHERE c.island.id =:id")
    public List<Card> findCardsByIslandId(int id);
}
