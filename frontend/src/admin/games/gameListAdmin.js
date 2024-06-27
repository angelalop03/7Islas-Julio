import { useState } from "react";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function GameListAdmin() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const modal = getErrorModal(setVisible, visible, message);

    const jwt = tokenService.getLocalAccessToken(); 

    const [games, setGames] = useFetchState(
        [], 
        `/api/v1/games`, 
        jwt, 
        setMessage,
        setVisible
    );

    const getGameState = (game) => {
        let state = "Finished"; 
        let color = "Black";

        if (game.createDate && !game.startDate) {
            state = "Waiting for start";
            color = "DarkPink";
        } else if (game.createDate && game.startDate && !game.endDate) {
            state = "In progress";
            color = "DarkBlue";
        }

        return { state, color };
    };

    const gameList = games.map((game) => {
        const { state, color } = getGameState(game);

        const players = game.players.map((g) => g.user.username).join(", ");

        return (
            <tr key={game.id}>
                <td style={{ color: color, padding: "10px" }}>{game.code}</td>
                <td style={{ color: color, padding: "10px" }}>{game.creator ? game.creator.user.username : "delete account"}</td>
                <td style={{ color: color, padding: "10px" }}>{players}</td>
                <td style={{ color: color, padding: "10px" }}>{game.createDate}</td>
                <td style={{ color: color, padding: "10px" }}>{game.startDate}</td>
                <td style={{ color: color, padding: "10px" }}>{game.endDate}</td>
                <td style={{ color: color, padding: "10px" }}>{state}</td>
            </tr>
        );
    });

    return (
        <div className="admin-page-container">
            <div className="admin-page-container-head">
                <h1 className="text-center">Games Management</h1>
                {modal}
            </div>
            <div>
                <table className="table-admin">
                    <thead>
                        <tr>
                            <th style={{ fontSize: 25, width: "5%", padding: "10px" }}>Code</th>
                            <th style={{ fontSize: 25, width: "15%", padding: "10px" }}>Creator</th>
                            <th style={{ fontSize: 25, width: "25%", padding: "10px" }}>Players</th>
                            <th style={{ fontSize: 25, width: "10%", padding: "10px" }}>Created At</th>
                            <th style={{ fontSize: 25, width: "10%", padding: "10px" }}>Start</th>
                            <th style={{ fontSize: 25, width: "10%", padding: "10px" }}>Finish</th>
                            <th style={{ fontSize: 25, width: "15%", padding: "10px" }}>State</th>
                        </tr>
                    </thead>
                    <tbody>{gameList.length > 0 ? gameList : <tr><td colSpan="7" style={{ textAlign: "center", padding: "10px" }}>No games...</td></tr>}</tbody>
                </table>
            </div>
        </div>
    );
}