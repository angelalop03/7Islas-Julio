package org.springframework.samples.petclinic.game;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.builders.GameBuilderService;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.island.Island;
import org.springframework.samples.petclinic.island.IslandService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;
    private final UserService userService;
    private final CardService cardService;
    private final GameBuilderService gameBuilderService;
    private final IslandService islandService;


    @Autowired
    public GameService(GameRepository gameRepository,PlayerService playerService,UserService userService,CardService cardService,GameBuilderService gameBuilderService,IslandService islandService){
        this.gameRepository= gameRepository;
        this.playerService = playerService;
        this.userService = userService;
        this.cardService = cardService;
        this.gameBuilderService= gameBuilderService;
        this.islandService = islandService;
    }

    @Transactional(readOnly = true)
    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    @Transactional(readOnly= true)
    public List<Game> findByState(GameState state){
        List<Game> res ;
        GameState CREATED= GameState.CREATED;
        GameState STARTED= GameState.STARTED;
        if(state == CREATED){
            res= gameRepository.findByStartDateIsNull();
        }else if(state == STARTED){
            res= gameRepository.findByEndDateIsNull();
        }else{
            res = gameRepository.findByEndDateIsNotNull();
        }

        return res;
    }

    @Transactional(readOnly = true)
    public Game findGameById(Integer gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "ID", gameId));
    }

    @Transactional(readOnly = true)
    public Optional<Game> findGamesUnstartedByUserId(Integer userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Integer id = player.get().getId();
        return gameRepository.findUnstartedGameByPlayerId(id);
    }

    @Transactional(readOnly = true)
    public Optional<Game> findGamesUnfinishedByUserId(Integer userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Integer id = player.get().getId();
        return gameRepository.findUnfinishedGameByPlayerId(id);
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByUserId(Integer userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Integer id = player.get().getId();
        return gameRepository.findGamesByPlayerId(id);
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByUserIdNotStarted(Integer userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Integer id = player.get().getId();
        return gameRepository.findGamesByPlayerIdNotStarted(id);
    }

    @Transactional
    public Game saveGame() {
        Game newGame = new Game();
        String code = generateCode(4);
        newGame.setCode(code);
        LocalDateTime createDate = LocalDateTime.now();
        newGame.setCreateDate(createDate);
        LocalDateTime startDate = null;
        newGame.setStartDate(startDate);
        LocalDateTime endDate = null;
        newGame.setEndDate(endDate);
        User user = userService.findCurrentUser();
        Optional<Player> player = playerService.findPlayerByUser(user.getId());
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player with userId", "ID", user.getId());
        }
        Player creator = player.get();
        Game lobby = gameRepository.findUnstartedGameByPlayerId(creator.getId()).orElse(null);
        Game game = gameRepository.findUnfinishedGameByPlayerId(creator.getId()).orElse(null);

        if (lobby != null) {
            throw new IllegalStateException(
                    "You cant be in two lobbys at the same time");
        }
        if (game != null) {
            throw new IllegalStateException(
                    "You cant be in two games at the same time");
        }
        newGame.setCreator(creator);
        List<Player> players = new ArrayList<>();
        players.add(creator);
        newGame.setPlayers(players);
        return gameRepository.save(newGame);
    }

    private static String generateCode(Integer length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = rnd.nextInt(chars.length());
            code.append(chars.charAt(randomIndex));
        }
        return code.toString();
    }

    @Transactional
    public Game invitationByCode(String codigo) {
        Optional<Game> gameToUpdate = gameRepository.findGameByCode(codigo);
        if (!gameToUpdate.isPresent()) {
            throw new ResourceNotFoundException("Game with code", "Code", codigo);
        }
        Game game = gameToUpdate.get();
        User user = userService.findCurrentUser();
        Optional<Player> player = playerService.findPlayerByUser(user.getId());
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", user.getId());
        }
        Player playerToAdd = player.get();
        addPlayer(playerToAdd, game);
        gameRepository.save(game);
        return game;
    }

    private void addPlayer(Player player, Game game) {
        Game lobby = gameRepository.findUnstartedGameByPlayerId(player.getId()).orElse(null);
        Game game1 = gameRepository.findUnfinishedGameByPlayerId(player.getId()).orElse(null);

        if (lobby != null) {
            throw new IllegalStateException(
                    "No se puede estar en dos lobbys simultáneamente, revise si ya está en una.");
        }
        if (game1 != null) {
            throw new IllegalStateException(
                    "No se puede estar en dos partidas simultáneamente, revise si ya está en una.");
        }
        if (game.getPlayers().size() < 4) {
            game.getPlayers().add(player);
        } else {
            throw new IllegalStateException("La partida esta llena, ya tiene 4 jugadores.");
        }
    }

    private void removeCardDependencies(Integer playerId) {
        List<Card> cardsPlayer = playerService.findCardsByPlayerId(playerId);
        cardsPlayer.forEach(card -> cardService.deleteCard(card.getId()));
    }

    public LocalTime timeExpend(LocalDateTime creacion, LocalDateTime ahora) {
        LocalTime time1 = creacion.toLocalTime();
        LocalTime time2 = ahora.toLocalTime();
        Long diferenciaEnSegundos = ChronoUnit.SECONDS.between(time1, time2);
        return LocalTime.ofSecondOfDay(diferenciaEnSegundos);
    }


    @Transactional
    public Game startPlayGameById(Integer gameId) {
        Game gameStart = findGameById(gameId);
        gameStart.setStartDate(LocalDateTime.now());
        gameBuilderService.startGame(gameStart);
        return gameRepository.save(gameStart);
    }

    @Transactional
    public Game updateTurnByGameId(Integer gameId) {
        Game gameToChangeTurn = findGameById(gameId);
        List<Player> players = gameToChangeTurn.getPlayers();
        Integer positionOfTurnNow = findPositionInTurn(players);
        Integer positionOfTurnInFuture = findPositionInTurnAfter(positionOfTurnNow, players);
        updatePlayersTurn(players, positionOfTurnNow, positionOfTurnInFuture);
        return gameToChangeTurn;
    }

    @Transactional(readOnly = true)
    public Game findRecentGameByPlayerId() {
        User user = userService.findCurrentUser();
        Player player = userService.findPlayerByUserId(user.getId());
        return gameRepository.findMostRecentGameByPlayerId(player.getId());
    }

    private Integer findPositionInTurn(List<Player> players) {
        Integer position = null;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getTurn()) {
                position = i;
            }
        }
        return position;
    }

    private Integer findPositionInTurnAfter(Integer positionOfTurnNow, List<Player> players) {
        Integer positionOfTurnInFuture = null;
        if (positionOfTurnNow == players.size() - 1) {
            positionOfTurnInFuture = 0;
        } else {
            positionOfTurnInFuture = positionOfTurnNow + 1;
        }
        return positionOfTurnInFuture;
    }

    private void updatePlayersTurn(List<Player> players, Integer positionOfTurnNow, Integer positionOfTurnInFuture) {
        Player playerInTurnNow = players.get(positionOfTurnNow);
        playerInTurnNow.setTurn(false);
        Player playerInTurnFuture = players.get(positionOfTurnInFuture);
        playerInTurnFuture.setTurn(true);
        playerService.updatePlayerTurn(playerInTurnNow, playerInTurnNow.getId());
        playerService.updatePlayerTurn(playerInTurnFuture, playerInTurnFuture.getId());
    }


    @Transactional
    public Game finishGame(Integer gameId) throws Exception {
        Game gameToUpdate = findGameById(gameId);
        gameToUpdate.setEndDate(LocalDateTime.now());
        return gameRepository.save(gameToUpdate);
    }

    @Transactional
    public Game exitUserById(Integer gameId, Integer userId) {
        Game gameToUpdate = findGameById(gameId);
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Player playerToExit = player.get();
        if (gameToUpdate.getPlayers().contains(playerToExit)) {
            List<Player> players = gameToUpdate.getPlayers();
            players.remove(playerToExit);
            gameToUpdate.setPlayers(players);
            if (gameToUpdate.getStartDate() != null) {
                removeCardDependencies(playerToExit.getId());
            }
        }
        gameRepository.save(gameToUpdate);
        return gameToUpdate;
    }

    @Transactional
    public Game updateGameByGameId(Integer gameId, Game game) {
        Game gameToUpdate = findGameById(gameId);
        BeanUtils.copyProperties(game, gameToUpdate, "id");
        return gameRepository.save(gameToUpdate);
    }

    @Transactional
    public void deleteGame(Integer id) {
        Game game = findGameById(id);
        gameRepository.delete(game);
    }

    @Transactional
    public void updateGameFinishedDependencies(Integer gameId) {
        List<Card> cardsGame = cardService.findCardsByGameId(gameId);
        List<Island> islandsGame = islandService.findIslandsByGameId(gameId);
        cardsGame.forEach(card -> cardService.deleteCard(card.getId()));
        islandsGame.forEach(island -> islandService.deleteIsland(island.getId()));
    }

    
}

    
