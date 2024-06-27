package org.springframework.samples.petclinic.card;


import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.island.Island;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final PlayerService playerService;

    @Autowired
    public CardService(CardRepository cardRepository, PlayerService playerService){
        this.cardRepository=cardRepository;
        this.playerService=playerService;
    }

    @Transactional(readOnly = true)
    public Iterable<Card> findAll() throws DataAccessException {
        return cardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Card findCardById(int id) throws DataAccessException {
        return this.cardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Card", "ID", id));
    }

    @Transactional
    public Card saveCard(Card card) throws DataAccessException {
        cardRepository.save(card);
        return card;
    }

    @Transactional
    public void deleteCard(int id) throws DataAccessException {
        Card card = findCardById(id);
        cardRepository.delete(card);
    }

    @Transactional(readOnly = true)
    public List<Card> findCardsByGameId(Integer gameId) {
        return cardRepository.findCardsByGameId(gameId);
    }

    @Transactional
    public Card updateSelectedCards(CardSelected cardSelected, Integer cardId, Integer playerId) {
        Card cardToUpdate = updateCard(cardId, playerId);
        for (Card card : cardSelected.getCardsSelected()) {
            card.setPlayer(null);
            saveCard(card);
        }
        return cardToUpdate;
    }

    @Transactional
    public Card updateSelected(Integer cardId) {
        Card cardToUpdate = findCardById(cardId);
        cardToUpdate.setIsSelected(!cardToUpdate.isSelected);
        saveCard(cardToUpdate);
        return cardToUpdate;
    }
    

    private Card cogerCarta(Card cardUpdate) {
        Integer gameId = cardUpdate.getGame().getId();
        List<Card> cartasMazo = cardRepository.findCardsByGameIdAndIsland(gameId, 7);
        Collections.shuffle(cartasMazo);
        return cartasMazo.isEmpty() ? null : cartasMazo.get(0);
    }

    @Transactional
    public Card updateCard(Integer cardId, Integer playerId) {
        Card cardToUpdate = findCardById(cardId);
        Island isla = cardToUpdate.getIsland();
        cardToUpdate.setIsland(null);
        Player player = playerService.findPlayerById(playerId);
        cardToUpdate.setPlayer(player);
        Card cartaParaIsla = cogerCarta(cardToUpdate);
        if (cartaParaIsla != null) {
            cartaParaIsla.setIsland(isla);
            saveCard(cartaParaIsla);
        }
        return saveCard(cardToUpdate);
    }

}
