import '../static/css/game/gameRoom.css';
import useFetchState from "../util/useFetchState";
import tokenService from "../services/token.service";
import { useEffect, useState } from "react";
import getIdFromUrl from "../util/getIdFromUrl";
import { Button } from "reactstrap";
import useIntervalFetchState from "../util/useIntervalFetchState";
import getErrorModal from "../util/getErrorModal";
import getImageForCardType from './util/getImageForCardType';
import selectCard from './util/selectCard';
import ShowIsland from './components/showIsland';
import ShowMyPlayer from './components/showMyPlayer';
import DadoStart from '../static/images/Dado_Start.png';
import throwDice from './util/throwDice';
import getDiceNumber from './util/getImageForResultDice';
import finishGame from './util/finishGame';
import Countdown from "./components/countdown";
import getProfileImage from "../util/getProfileImage";


// Obtenemos el token y el usuario actual
const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function GameRoom() {
    // Estado para manejar mensajes y visibilidad de modales de error
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);

    // ID del juego obtenido desde la URL
    const id = getIdFromUrl(2);

    // Definición de objetos vacíos para inicializar estados
    const emptyPlayer = {
        id: "",
        firstName: "",
        lastName: "",
        registrationDate: "",
        user: ""
    };
    const emptyItemGame = {
        id: "",
        code: "",
        createdAt: "",
        startedAt: "",
        finishedAt: "",
        players: [emptyPlayer, emptyPlayer, emptyPlayer, emptyPlayer],
        creator: ""
    };
    const emptyItemCard = {
        id: "",
        gameId: "",
        islandId: "",
        playerId: "",
        reversed: "",
        selected: "",
        type: ""
    };
    const emptyItemIsland = {
        id: "",
        gameId: "",
        number: ""
    };

    // Fetch de datos utilizando hooks personalizados
    const [cards] = useIntervalFetchState(
        emptyItemCard,
        `/api/v1/cards/game/${id}`,
        jwt,
        setMessage,
        setVisible,
        null,
        1000
    );
    const [islands] = useFetchState(
        emptyItemIsland,
        `/api/v1/islands/game/${id}`,
        jwt,
        setMessage,
        setVisible,
    );
    const [game] = useIntervalFetchState(
        emptyItemGame,
        `/api/v1/games/${id}`,
        jwt,
        setMessage,
        setVisible,
        null,
        1000
    );

    // Estado para manejar el resultado del dado y su visualización
    const [resultadoTirada, setResultadoTirada] = useState(0);
    const [visualTirada, setVisualTirada] = useState([]);
    const [diceIsThrown, setDiceIsThrown] = useState(false);

    // Fetch del jugador actual
    const [player] = useFetchState(
        emptyPlayer,
        `/api/v1/players/user/${user.id}`,
        jwt,
        setMessage,
        setVisible
    );

    // Fetch de la mano del jugador actual
    const [mano] = useIntervalFetchState(
        [],
        `/api/v1/players/${player.id}/cards`,
        jwt,
        setMessage,
        setVisible,
        null,
        1000
    );

    // Para ver la mano del jugador actual
    const [visualHand, setVisualHand] = useState([]);

    // Para indicar si es el turno del jugador actual
    const [isPlayerTurn, setIsPlayerTurn] = useState(player.turn);

    // Para contar las islas vacías
    const [emptyIslands, setEmptyIslands] = useState(0);

    // Para indicar si el juego ha terminado
    const [gameFinished, setGameFinished] = useState(false);

    // Asignación de las islas a variables individuales para facilitar el acceso
    let island1 = islands[0];
    let island2 = islands[1];
    let island3 = islands[2];
    let island4 = islands[3];
    let island5 = islands[4];
    let island6 = islands[5];
    let cardsPlayer1 = [];
    let cardsPlayer2 = [];
    let cardsPlayer3 = [];
    let cardsPlayer4 = [];
    let cardsIsland1 = null;
    let cardsIsland2 = null;
    let cardsIsland3 = null;
    let cardsIsland4 = null;
    let cardsIsland5 = null;
    let cardsIsland6 = null;
    let cardsIsland7 = [];

    // Iteración sobre las cartas para asignarlas a los jugadores y a las islas
    for (let i = 0; i < cards.length; i++) {
        const card = cards[i];
        if (card.player) {
            if (game.players[0] && card.player.id === game.players[0].id) {
                cardsPlayer1.push(card);
            } else if (game.players[1] && card.player.id === game.players[1].id) {
                cardsPlayer2.push(card);
            } else if (game.players[2] && card.player.id === game.players[2].id) {
                cardsPlayer3.push(card);
            } else if (game.players[3] && card.player.id === game.players[3].id) {
                cardsPlayer4.push(card);
            }
        }
        if (card.island !== null) {
            if (card.island.id === island1.id) {
                cardsIsland1 = card;
            } else if (card.island.id === island2.id) {
                cardsIsland2 = card;
            } else if (card.island.id === island3.id) {
                cardsIsland3 = card;
            } else if (card.island.id === island4.id) {
                cardsIsland4 = card;
            } else if (card.island.id === island5.id) {
                cardsIsland5 = card;
            } else if (card.island.id === island6.id) {
                cardsIsland6 = card;
            } else {
                cardsIsland7.push(card);
            }
        }
    }

    // Asignación de las cartas a los jugadores en el estado del juego
    let cartasJugadores = [cardsPlayer1, cardsPlayer2, cardsPlayer3, cardsPlayer4];
    let indiceCartas = 0;
    game.players.map((player) => {
        player.cards = cartasJugadores[indiceCartas];
        indiceCartas += 1;
    })

    // Renderizado de los jugadores que no son el jugador actual
    const restoJugadores = game.players.map((jugador) => {
        if (jugador.id !== player.id) {
            return (
                <div className="game-jugador2">
                    <h4>{jugador.user.username}</h4>
                    <div className='game-cartas'>
                        {jugador.cards.length > 0 &&
                            <div>
                                <div className='game-cartas'>
                                    <img
                                        src={getProfileImage(jugador.image)}
                                        alt={getProfileImage(jugador.image)}
                                        style={{ width: '80px', height: '80px' }}
                                    />
                                </div>
                                <h6>Tiene {jugador.cards.length} cartas</h6>
                                {jugador.turn &&
                                    <h6>Es su turno</h6>
                                }
                            </div>
                        }
                    </div>
                </div>
            );
        }
    })

    // Verificación del fin del juego y efecto asociado
    useEffect(() => {
        if (!gameFinished && (game.players.length === 1 || (game.players.length === emptyIslands && cardsIsland7.length === 0))) {
            finishGame(game, jwt, setMessage, setVisible);
            setGameFinished(true);
        }
    }, [game, emptyIslands, cardsIsland7, gameFinished]);

    // Conteo de islas vacías y efecto asociado
    useEffect(() => {
        let numIslasVacias = 0;
        if (cardsIsland1 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland2 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland3 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland4 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland5 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland6 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        setEmptyIslands(numIslasVacias);
    }, [cardsIsland1, cardsIsland2, cardsIsland3, cardsIsland4, cardsIsland5, cardsIsland6])

    // Verificación del turno del jugador actual y efecto asociado
    useEffect(() => {
        const currentPlayer = game.players.find((jugador) => jugador.id === player.id);
        setIsPlayerTurn(currentPlayer && currentPlayer.turn);
    }, [game, player, mano]);

    // Renderizado de la mano del jugador actual
    useEffect(() => {
        let visual = mano.map((card) => {
            return (
                <div className='game-cartas' key={card.id}>
                    <img
                        src={getImageForCardType(card.type)}
                        alt={card.type}
                        style={{ width: '80px', height: '80px' }}
                    />
                    {isPlayerTurn &&
                        <Button
                            color='danger'
                            size="md"
                            onClick={() => {
                                selectCard(card, jwt, setMessage, setVisible);
                            }}>
                            {card.isSelected ? "DeSelect" : "Select"}
                        </Button>
                    }
                </div>
            )
        });
        setVisualHand(visual);
    }, [mano, isPlayerTurn]);

    // Renderizado de la tirada de dados
    useEffect(() => {
        let tirada =
            <div>
                {isPlayerTurn && !diceIsThrown &&
                    <img
                        src={DadoStart}
                        alt={DadoStart}
                        style={{ width: '80px', height: '80px' }}
                        onClick={() => {
                            throwDice(setResultadoTirada, setDiceIsThrown, diceIsThrown)
                        }}
                    />
                }
                {isPlayerTurn && diceIsThrown &&
                    <img
                        src={getDiceNumber(resultadoTirada)}
                        alt={getDiceNumber(resultadoTirada)}
                        style={{ width: '80px', height: '80px' }}
                    />
                }
            </div>
        setVisualTirada(tirada);
    }, [diceIsThrown, isPlayerTurn, resultadoTirada])

    // Renderizado del modal de error
    const modal = getErrorModal(setVisible, visible, message);

    // Renderizado final del componente
    return (
        <div className="game-fondo">
            {modal}
            <div className='game-up'>
                <div className="game-restoJugadores">
                    {restoJugadores}
                </div>
            </div>
            <div className='game-middle'>
                <div className='game-tablero'>
                    <ShowIsland number={1} cardsIsland={cardsIsland1} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />
                    <ShowIsland number={2} cardsIsland={cardsIsland2} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />
                    <ShowIsland number={3} cardsIsland={cardsIsland3} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />
                    <ShowIsland number={4} cardsIsland={cardsIsland4} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />
                    <ShowIsland number={5} cardsIsland={cardsIsland5} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />
                    <ShowIsland number={6} cardsIsland={cardsIsland6} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />
                    <ShowIsland number={7} cardsIsland={cardsIsland7} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />
                    {isPlayerTurn &&
                        <Countdown startingSeconds={60} isPlayerTurn={isPlayerTurn} game={game} user={user} jwt={jwt}
                            setMessage={setMessage} setVisible={setVisible} />
                    }
                </div>
            </div>
            <div className='game-down'>
                <ShowMyPlayer player={player} visualHand={visualHand} diceIsThrown={diceIsThrown} setMessage={setMessage}
                    setVisible={setVisible} isPlayerTurn={isPlayerTurn} user={user} jwt={jwt} visualTirada={visualTirada}
                    game={game} />
            </div>
        </div>
    );
}
