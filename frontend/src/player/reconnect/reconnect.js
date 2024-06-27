import useFetchState from "../../util/useFetchState";
import tokenService from "../../services/token.service";
import { useState } from "react";
import getErrorModal from "../../util/getErrorModal";
import '../../static/css/player/reconnect.css';

const jwt = tokenService.getLocalAccessToken();

export default function Reconnect() {
    const user = tokenService.getUser();
const [message, setMessage] = useState(null);
const [visible, setVisible] = useState(false);

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

const FetchData = (endpoint) => useFetchState(
    emptyItemGame,
    endpoint,
    jwt,
    setMessage,
    setVisible,
    user.id
);

const [lobby, setLobby] = FetchData(`/api/v1/games/reconnectLobby/${user.id}`);
const [game, setGame] = FetchData(`/api/v1/games/reconnectGame/${user.id}`);

const handleBackTo = (type, id) => {
    const isLobby = type === "lobby";
    const targetUrl = isLobby ? `/game/${id}/waitingRoom/` : `/gameRoom/${id}`;

    if (!id) {
        setMessage(`No estás en ninguna ${isLobby ? "lobby" : "partida en juego"} o en la que estabas ya no está disponible, prueba a unirte a una nueva.`);
        setVisible(true);
        return;
    }

    window.location.href = targetUrl;
};

return (
    <div className="player-reconnect">
        {getErrorModal(setVisible, visible, message)}
        <div className="reconnect-message">
            ¿Dónde quieres volver a conectarte, pirata?
        </div>
        <div className="custom-button-row">
            <button type="button" className="auth-button" onClick={() => handleBackTo("lobby", lobby.id)}>
                Volver a la Lobby
            </button>
            <span className="or-separator">o</span>
            <button type="button" className="auth-button" onClick={() => handleBackTo("game", game.id)}>
                Volver a la Partida
            </button>
        </div>
    </div>
);

}
