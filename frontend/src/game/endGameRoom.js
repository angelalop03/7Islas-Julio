import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { Button } from "reactstrap";
import getIdFromUrl from "../util/getIdFromUrl";
import { useState, useEffect } from "react";
import getErrorModal from "../util/getErrorModal";
import removeDependencies from "./util/removeDependencies";
import '../static/css/game/gameEndGameRoom.css';

const jwt = tokenService.getLocalAccessToken();

export default function EndGameRoom() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const gameId = getIdFromUrl(2); 
    const [game] = useFetchState({}, `/api/v1/games/${gameId}`, jwt, setMessage, setVisible, gameId);
    const [canGoHome, setCanGoHome] = useState(false);

    useEffect(() => {
        const timer1 = setTimeout(() => {
            removeDependencies(game, jwt);
            setCanGoHome(true); 
        }, 2000);

        return () => clearTimeout(timer1);
    }, [game]);

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="gameend-fondo">
            <h1>GAME ENDED</h1>
            <h2>¡Espero que te lo hayas pasado bien!</h2>
            {modal}

            {canGoHome && (
                <Button
                    size="lg"
                    color="danger"
                    onClick={() => {
                        window.location.href = "/";
                    }}
                >
                    Back to home
                </Button>
            )}
        </div>
    );
}
