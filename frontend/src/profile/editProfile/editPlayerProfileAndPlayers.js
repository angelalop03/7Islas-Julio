import { useEffect, useState } from "react";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";
import Estandar from '../../static/images/estandar.jpg';
import foto1 from '../../static/images/FotoPerfil1.jpg';
import foto2 from '../../static/images/FotoPerfil2.jpg';
import foto3 from '../../static/images/FotoPerfil3.jpg';
import foto4 from '../../static/images/FotoPerfil4.jpg';
import getProfileImage from "../../util/getProfileImage";
import PlayerEditChecks from "./playerEditChecks";
import "../../static/css/login/login.css";



const jwt = tokenService.getLocalAccessToken();

export default function EditPlayerProfileAndPlayers() {
    const id = getIdFromUrl(2);
    const place = getIdFromUrl(1);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [mostrarLista, setMostrarLista] = useState(false);
    const modal = getErrorModal(setVisible, visible, message);

    const emptyItem = {
        id: "",
        firstName: "",
        lastName: "",
        birthdayDate: "",
        email: "",
        user: {
            username: "",
            password: ""
        }
    };

    const [player, setPlayer] = useFetchState(
        emptyItem,
        place === "players" ? `/api/v1/players/${id}` : `/api/v1/users/${tokenService.getUser() ? tokenService.getUser().id : 0}/player`,
        jwt,
        setMessage,
        setVisible,
        id
    );

    const [player1, setPlayer1] = useFetchState(
        emptyItem,
        place === "players" ? `/api/v1/players/${id}` : `/api/v1/users/${tokenService.getUser() ? tokenService.getUser().id : 0}/player`,
        jwt,
        setMessage,
        setVisible,
        id
    );

    const [playerDTO, setPlayerDTO] = useState({
        id: "",
        firstName: "",
        lastName: "",
        birthdayDate: "",
        email: "",
        username: "",
        password: ""
    });

    useEffect(changeToDTO, [player]);
    function changeToDTO() {
        setPlayerDTO({
            ...player,
            username: player.user.username,
            password: player.user.password,
        });
    }

    function handleCancelClick() {
        if (place === "players") {
            window.location.href = "/players";
        } else {
            window.location.href = "/profile"
        }

    }

    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        setPlayerDTO({ ...playerDTO, [name]: value });
    }

    function handleSubmit(event) {
        event.preventDefault();
        let error = PlayerEditChecks(playerDTO, player1);
        if (error) {
            setMessage(error);
            setVisible(true);
            return;
        }

        fetch(`/api/v1/players/${player.id}`, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(playerDTO),
        })
            .then((response) => {
                if (response.status === 200 && place === "players") {
                    window.location.href = "/players";
                    return response.json()
                } else if (response.status === 200) {
                    if (player.user.username === playerDTO.username) {
                        window.location.href = "/profile";
                        return response.json();
                    } else {
                        if (tokenService.getUser()) {
                            fetch(`/api/v1/players/${tokenService.getUser().id}/connection`, {
                                method: "PUT",
                                headers: {
                                    Authorization: `Bearer ${jwt}`,
                                    Accept: "application/json",
                                    "Content-Type": "application/json",
                                }
                            })
                            tokenService.removeUser();
                            window.location.href = "/";
                            return response.json();
                        }
                    }
                } else {
                    return response.json()
                }
            })
            .then((jsonResponse) => {
                setMessage(jsonResponse.message)
                setVisible(true)
                return;
            })
            .catch((message) => alert(message));

    }


    const toggleLista = (selectedImage) => {
        setMostrarLista(!mostrarLista);
    }

    const toggleListaAndUptade = (selectedImage) => {
        setMostrarLista(!mostrarLista);

        if (selectedImage) {
            setPlayerDTO({
                ...playerDTO,
                image: selectedImage,
            });
        }
    };



    return (
        <div className="page-container">


            <div className="auth-form-container">
                {<h2>{id !== "new" ? "Edit Player" : "Add Player"}</h2>}

                {modal}
                <div>
                    <button onClick={toggleLista}><img src={getProfileImage(playerDTO.image)} alt="perfil" style={{ height: 200, width: 200 }} /></button>
                    {mostrarLista && (
                        <div className="lista-imagenes">
                            {playerDTO.image !== 'Estandar' && <button onClick={() => { toggleListaAndUptade('Estandar'); }}><img src={Estandar} alt="Estandar" style={{ height: 200, width: 200 }} /></button>}
                            {playerDTO.image !== 'foto1' && <button onClick={() => { toggleListaAndUptade('foto1'); }}><img src={foto1} alt="Imagen 1" style={{ height: 200, width: 200 }} /></button>}
                            {playerDTO.image !== 'foto2' && <button onClick={() => { toggleListaAndUptade('foto2'); }}><img src={foto2} alt="Imagen 2" style={{ height: 200, width: 200 }} /></button>}
                            {playerDTO.image !== 'foto3' && <button onClick={() => { toggleListaAndUptade('foto3'); }}><img src={foto3} alt="Imagen 3" style={{ height: 200, width: 200 }} /></button>}
                            {playerDTO.image !== 'foto4' && <button onClick={() => { toggleListaAndUptade('foto4'); }}><img src={foto4} alt="Imagen 4" style={{ height: 200, width: 200 }} /></button>}

                        </div>)}
                </div>
                <Form onSubmit={handleSubmit}>
                    <div className="custom-form-input">
                        <Label for="username" className="custom-form-input-label">
                            Username
                        </Label>
                        <Input
                            type="text"
                            required
                            name="username"
                            id="username"
                            value={playerDTO.username || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="password" className="custom-form-input-label">
                            Password
                        </Label>
                        <Input
                            type="password"
                            required
                            name="password"
                            id="password"
                            value={playerDTO.password || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="firstName" className="custom-form-input-label">
                            First Name
                        </Label>
                        <Input
                            type="text"
                            required
                            name="firstName"
                            id="firstName"
                            value={playerDTO.firstName || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="lastName" className="custom-form-input-label">
                            Last Name
                        </Label>
                        <Input
                            type="text"
                            required
                            name="lastName"
                            id="lastName"
                            value={playerDTO.lastName || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="birthdayDate" className="custom-form-input-label">
                            Birthday Date
                        </Label>
                        <Input
                            type="date"
                            required
                            name="birthdayDate"
                            id="birthdayDate"
                            value={playerDTO.birthdayDate || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="email" className="custom-form-input-label">
                            Email
                        </Label>
                        <Input
                            type="text"
                            required
                            name="email"
                            id="email"
                            value={playerDTO.email || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-button-row">
                        <button type="submit" className="auth-button">
                            Save
                        </button>
                        <button type="submit" className="auth-button" onClick={handleCancelClick}>
                            Cancel
                        </button>
                    </div>
                </Form>
            </div>
        </div>
    );
}