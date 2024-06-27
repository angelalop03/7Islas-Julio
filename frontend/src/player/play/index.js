import '../../static/css/player/homePlayer.css';
import { Button } from 'reactstrap';
import { useState } from 'react';
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";

export default function Play() {
  const [message, setMessage] = useState(null);
const [visible, setVisible] = useState(false);
const [game, setGame] = useState(null);
const jwt = tokenService.getLocalAccessToken();

let state = "";

const modal = getErrorModal(setVisible, visible, message);

const handleCreateGame = () => {
  fetch("/api/v1/games", {
    headers: {
      Authorization: `Bearer ${jwt}`,
      "Content-Type": "application/json"
    },
    method: "POST",
  })
  .then(function (response) {
    if (response.status === 201) {
      state = "201";
      return response.json();
    } else {
      state = "";
      return response.json();
    }
  })
  .then(function (data) {
    if (state !== "201") {
      alert(data.message);
    } else {
      setGame(data);
      window.location.href = `/game/${data.id}/waitingRoom`;
    }
  })
  .catch((message) => {
    alert(message);
  });
};

return (
  <div>
    <div className="player-home-background">
      <h1 className="home-title">¡Vamos a pasarlo bien grumete!</h1>
      
      <div className='player-home-enlaces'>
        <div className='player-home-create-game'>
        <button
          type="button"
          className="auth-button"
          onClick={handleCreateGame}
        >
          Crear partida
        </button>
        <span className="or-separator">o</span>
        <button
          type="button"
          className="auth-button"
          onClick={() => window.location.href = `/gameCodeRoom`}
        >
          Unirse partida
        </button>
        </div>
      </div>
    </div>
    {modal}
  </div>
);

}
