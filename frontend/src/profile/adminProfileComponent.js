import { useState } from "react";
import "../static/css/auth/authButton.css";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import getErrorModal from "../util/getErrorModal";


const jwt = tokenService.getLocalAccessToken();

export default function AdminProfileComponent() {
    const userId = tokenService.getUser()?.id || 0; // ID del usuario actual
    
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [user, setUser] = useFetchState(
        {}, //Objeto vacío para evitar problemas con desestructuración
        `/api/v1/users/${userId}`,
        jwt,
        setMessage,
        setVisible
    );

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div>
            {modal}
            <h3>Username: {user.username}</h3>
        </div>
    );
}