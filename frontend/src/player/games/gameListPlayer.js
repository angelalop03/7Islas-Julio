import { useState } from "react";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();

export default function GameListPlayer() {
    const [message, setMessage] = useState(null);
const [visible, setVisible] = useState(false);
const [games, setGames] = useFetchState(
    [],
    `api/v1/games/user/${user.id}`,
    jwt,
    setMessage,
    setVisible
);

const getGameState = (game) => {
    let State = "Finished";
    let color = "Black";
    let rol = "";

    if (game.createDate && !game.startDate) {
        State = "Waiting for start";
        color = "DarkPink";
    } else if (game.createDate && game.startDate && !game.endDate) {
        State = "In progress";
        color = "DarkBlue";
    }

    if (game.creator !== null) {
        rol = game.creator.user.id === user.id ? "Creator" : "Player";
        if (State === "Finished") {
            color = "Black";
        }
    }

    return { State, color, rol };
};

const gameList = games.length > 0 ? games.map((game) => {
    const { State, color, rol } = getGameState(game);

    const playersAux = game.players.map((g) => g.user.username).join(" ");

    return (
        <tr key={game.id}>
            <td style={{ color: color, padding: "8px" }}>{game.code}</td>
            <td style={{ color: color, padding: "8px" }}>{game.creator !== null ? game.creator.user.username : "delete account"}</td>
            <td style={{ color: color, padding: "8px" }}>{playersAux}</td>
            <td style={{ color: color, padding: "8px" }}>{game.createDate}</td>
            <td style={{ color: color, padding: "8px" }}>{game.startDate}</td>
            <td style={{ color: color, padding: "8px" }}>{game.endDate}</td>
            <td style={{ color: color, padding: "8px" }}>{State}</td>
            <td style={{ color: color, padding: "8px" }}>{rol}</td>
        </tr>
    );
}) : (
    <tr>
        <td colSpan="10" style={{ textAlign: "center", padding: "8px" }}>No games...</td>
    </tr>
);

const modal = getErrorModal(setVisible, visible, message);

return (
    <div className="admin-page-container">
        <div className="admin-page-container-head">
            <h1 className="text-center">Games</h1>
            {modal}
        </div>
        <div>
            <table className="table-admin">
                <thead>
                    <tr>
                        <th style={{ fontSize: 25, width: "5%" }}>Code</th>
                        <th style={{ fontSize: 25, width: "5%" }}>Creator</th>
                        <th style={{ fontSize: 25, width: "10%" }}>Players</th>
                        <th style={{ fontSize: 25, width: "7%" }}>CreateDate</th>
                        <th style={{ fontSize: 25, width: "7%" }}>StartDate</th>
                        <th style={{ fontSize: 25, width: "7%" }}>EndDate</th>
                        <th style={{ fontSize: 25, width: "5%" }}>State</th>
                        <th style={{ fontSize: 25, width: "5%" }}>Role</th>
                    </tr>
                </thead>
                <tbody>
                    {gameList}
                </tbody>
            </table>
        </div>
    </div>
);

}
