package org.springframework.samples.petclinic.island;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IslandService {

    private final IslandRepository islandRepository;

    @Autowired
    public IslandService(IslandRepository islandRepository){
        this.islandRepository = islandRepository;
    }

    @Transactional(readOnly=true)
    public Iterable<Island> findAll() throws DataAccessException{
        return islandRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Island findIslandById(int id) throws DataAccessException {
        return islandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Island", "ID", id));
    }

    @Transactional(readOnly = true)
    public List<Card> findCardsByIslandId(int islandId) {
        return this.islandRepository.findCardsByIslandId(islandId);
    }

    @Transactional
    public Island saveIsland(Island island) throws DataAccessException {
        islandRepository.save(island);
        return island;
    }
    
    @Transactional(readOnly = true)
    public List<Island> findIslandsByGameId(Integer gameId) {
        return islandRepository.findByGameId(gameId);
    }

    @Transactional
    public void deleteIsland(int id) throws DataAccessException {
        Island island = findIslandById(id);
        islandRepository.delete(island);
    }

    


}
