import useFetchState from "../util/useFetchState";
import tokenService from "../services/token.service";
import { useState, useEffect } from "react";
import getIdFromUrl from "../util/getIdFromUrl";
import '../static/css/game/gameWaitingRoom.css';
import { Button } from 'reactstrap';
import ShowPlayers from "./components/showPlayers";
import getErrorModal from "../util/getErrorModal";


const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function GameWaitingRoom() {
    const id = getIdFromUrl(2);

    // Estado del juego y gestión de errores
    const emptyItem = {
        id: "",
        code: "",
        createDate: "",
        startDate: "",
        endDate: "",
        players: [],
        creator: ""
    };
    const [game, setGame] = useFetchState(emptyItem, `/api/v1/games/${id}`, jwt);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);

    // Efecto para comprobar el estado del juego cada 2 segundos
    useEffect(() => {
        const intervalId = setInterval(() => {
            fetchGame();
        }, 2000);
        return () => clearInterval(intervalId);
    }, [id]);

    useEffect(() => {
        // Verificar condiciones después de que el juego se actualice
        if (game.id) {
            handleGameChecks();
        }
    }, [game.id, game.players, game.startDate]);

    // Función para obtener y actualizar el juego desde la API
    const fetchGame = async () => {
        try {
            const response = await fetch(`/api/v1/games/${id}`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            });
            if (response.status === 404) {
                window.location.href = `/`;
            } else {
                const data = await response.json();
                setGame(data);
            }
        } catch (error) {
            console.error("Error fetching game:", error);
        }
    };

    // Función para manejar las comprobaciones del juego
    const handleGameChecks = () => {
        if (game.startDate !== null && game.startDate !== "") {
            window.location.href = `/gameRoom/${game.id}`;
        }
        if (!(game.players.some(player => player.user.id === user.id))) {
            window.location.href = `/`;
        }
    };

    // Función para mostrar el modal de error
    const modal = getErrorModal(setVisible, visible, message);

    // Función para iniciar el juego
    const startGame = async () => {
        let confirmMessage = window.confirm("Are you sure you want to start the game?");
        if (confirmMessage) {
            try {
                await fetch(`/api/v1/games/start/${game.id}`, {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });
                window.location.href = `/gameRoom/${game.id}`;
            } catch (error) {
                console.error("Error starting game:", error);
            }
        }
    };

    // Función para eliminar el juego
    const deleteGame = async () => {
        let confirmMessage = window.confirm("Are you sure you want to delete it?");
        if (confirmMessage) {
            try {
                await fetch(`/api/v1/games/${game.id}`, {
                    method: "DELETE",
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });
                window.location.href = `/`;
            } catch (error) {
                console.error("Error deleting the game: ", error);
            }
        }
    };

    // Función para salir del juego
    const exitGame = async () => {
        let confirmMessage = window.confirm("Are you sure you want to exit the game?");
        if (confirmMessage) {
            try {
                await fetch(`/api/v1/games/game/${game.id}/exit/${user.id}`, {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });
                window.location.href = `/`;
            } catch (error) {
                console.error("Error going away from the game:", error);
            }
        }
    };

    return (
        <div className="game-home-background">
            <div className="game-home-arriba">
                <div className="game-page-container-head">
                    <h1 className="text-center">LOBBY</h1>
                    <h3>Code of the game: {game.code}</h3>
                    {modal}
                </div>
            </div>

            <ShowPlayers game={game} />

            <div className="game-centrado">
                {(user.id === (game.creator.user ? game.creator.user.id : 0)) && game.players.length > 1 &&
                    <div>
                        <Button size="lg" color='danger' onClick={startGame}>
                            Start play
                        </Button>
                    </div>
                }
                {(user.id === (game.creator.user ? game.creator.user.id : 0)) &&
                    <div className="game-centrado">
                        <Button size="lg" color="danger" onClick={deleteGame}>
                            Delete game
                        </Button>
                    </div>
                }
                {user.id !== (game.creator.user ? game.creator.user.id : 0) &&
                    <div className="game-centrado">
                        <Button size="lg" color='danger' onClick={exitGame}>
                            Exit the game
                        </Button>
                    </div>
                }
            </div>
        </div>
    );
}
