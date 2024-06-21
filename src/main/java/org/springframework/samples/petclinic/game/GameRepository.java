package org.springframework.samples.petclinic.game;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends CrudRepository<Game, Integer> {

    @Query("SELECT g FROM Game g")
    List<Game> findAll();

    @Query("SELECT DISTINCT g FROM Game g WHERE g.creator.id = :playerId OR g.id IN (SELECT g.id FROM Game g JOIN g.players p WHERE p.id = :playerId)")
    List<Game> findGamesByPlayerId(@Param("playerId") Integer playerId);

    @Query("SELECT g FROM Game g WHERE g.startDate = null")
    List<Game> findByStartDateIsNull();

    @Query("SELECT g from Game g WHERE g.endDate != null")
    List<Game> findByEndDateIsNotNull();

    @Query("SELECT g from Game g WHERE g.endDate = null and g.startDate != null")
    List<Game> findByEndDateIsNull();

    @Query("SELECT g FROM Game g JOIN g.players p WHERE (p.id = :playerId AND g.startDate = Null)")
    List<Game> findGamesByPlayerIdNotStarted(Integer playerId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE (p.id = :playerId) ORDER BY g.createDate DESC LIMIT 1")
    Game findMostRecentGameByPlayerId(Integer playerId);

    @Query("SELECT g FROM Game g WHERE g.code = :codigo")
    Optional<Game> findGameByCode(String codigo);

    @Query("SELECT g.id FROM Game g WHERE g.creator.id = :playerId ORDER BY g.createDate DESC LIMIT 1")
    Integer findMostRecentGameByPlayerIdForInvitation(Integer playerId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE p.id= :playerId AND g.startDate = Null")
    Optional<Game> findUnstartedGameByPlayerId(Integer playerId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE p.id= :playerId AND g.startDate != Null AND g.endDate = Null")
    Optional<Game> findUnfinishedGameByPlayerId(Integer playerId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE (p.id= :playerId AND g.createDate != Null AND g.startDate != Null AND g.endDate != Null)")
    List<Game> findFinishedGameByPlayerId(Integer playerId);

}
