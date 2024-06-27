import { useState } from "react";
import getErrorModal from "../../../util/getErrorModal";
import tokenService from "../../../services/token.service";
import { Form, Input, Label } from "reactstrap";
import getIdFromUrl from "../../../util/getIdFromUrl";
import adminChecks from "./adminRegisterCheckers"; // Asegúrate de crear un archivo de comprobaciones para los administradores
import "../../../static/css/login/register.css";

const jwt = tokenService.getLocalAccessToken();

export default function CreateRegisterAdmin() {
    const place = getIdFromUrl(1);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const modal = getErrorModal(setVisible, visible, message);

    const [adminDTO, setAdminDTO] = useState({
        id: "",
        username: "",
        password: ""
    });

    function handleCancelClick() {
        window.location.href = place === "register" ? "/" : "/";
    }

    function handleChange(event) {
        const { name, value } = event.target;
        setAdminDTO({ ...adminDTO, [name]: value });
    }

    function signIn(loginRequest) {
        let state = "0";
        fetch("/api/v1/auth/signin", {
            headers: { "Content-Type": "application/json" },
            method: "POST",
            body: JSON.stringify(loginRequest),
        })
            .then((response) => {
                if (response.status === 200) {
                    state = "200";
                } else {
                    state = "";
                }
                return response.json();
            })
            .then((jsonResponse) => {
                if (state === "200") {
                    tokenService.setUser(jsonResponse);
                    tokenService.updateLocalAccessToken(jsonResponse.token);
                    fetch(`/api/v1/users/${tokenService.getUser().id}/connection`, {
                        method: "PUT",
                        headers: {
                            Authorization: `Bearer ${tokenService.getLocalAccessToken()}`,
                            Accept: "application/json",
                            "Content-Type": "application/json",
                        }
                    });
                    window.location.href = "/";
                } else {
                    setMessage(jsonResponse.message);
                    setVisible(true);
                }
            });
    }

    function handleSubmit(event) {
        event.preventDefault();
        let error = adminChecks(adminDTO);
        if (error) {
            setMessage(error);
            setVisible(true);
            return;
        }

        fetch("/api/v1/auth//signupAdmin", {
            method: "POST",
            headers: {
                Authorization: place === "register" ? "" : `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(adminDTO),
        })
            .then((response) => {
                if (response.status === 200 && place === "register") {
                    const loginRequest = {
                        username: adminDTO.username,
                        password: adminDTO.password,
                    };
                    signIn(loginRequest);
                    return response.json();
                } else if (response.status === 200) {
                    window.location.href = "/";
                    return response.json();
                } else {
                    return response.json();
                }
            })
            .then((jsonResponse) => {
                setMessage(jsonResponse.message);
                setVisible(true);
                return;
            })
            .catch((error) => alert(error));
    }

    return (
        <div className="page-container">
            <div className="auth-form-container">
                {<h2>{place === "register" ? "Register Admin" : "Create Admin"}</h2>}
                {modal}
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
                            value={adminDTO.username || ""}
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
                            value={adminDTO.password || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-button-row">
                        <button type="submit" className="auth-button">
                            Save
                        </button>
                        <button type="button" className="auth-button" onClick={handleCancelClick}>
                            Cancel
                        </button>
                    </div>
                </Form>
            </div>
        </div>
    );
}
