import { useState } from "react";
import { Form, Input, Label } from "reactstrap";
import { Link } from "react-router-dom";
import tokenService from "../services/token.service";
import getErrorModal from "../util/getErrorModal";
import '../static/css/game/gameCodeRoom.css';

const jwt = tokenService.getLocalAccessToken();

export default function GameCodeRoom() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [code, setCode] = useState("");

    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            const response = await fetch(`/api/v1/games/code/${code}`, {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ code }),
            });

            const data = await response.json();

            if (data.message) {
                setMessage(data.message);
                setVisible(true);
            } else {
                window.location.href = `/game/${data.id}/waitingRoom`;
            }
        } catch (error) {
            alert(error.message);
        }
    };

    const handleChange = (event) => {
        setCode(event.target.value);
    };

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="game-code-home-background">
            <div className="game-code-page-container-head">
                <h1>Introduce the code </h1>
                <h2>And have a fun time!</h2>
                {modal}
            </div>
            <div className="game-code-input">
                <div className="auth-form-container">
                    <Form onSubmit={handleSubmit}>
                        <div className="custom-form-input">
                            <Label size="lg" for="code" className="custom-form-input-label">
                                Code
                            </Label>
                            <Input
                                type="text"
                                required
                                name="code"
                                id="code"
                                value={code}
                                onChange={handleChange}
                                className="custom-input"
                            />
                        </div>
                        <div className="custom-button-row">
                            <button type="submit" className="game-code-button">Confirm</button>
                            <Link to="/homePlayer" className="game-code-button2">
                                Cancel
                            </Link>
                        </div>
                    </Form>
                </div>
            </div>
        </div>
    );
}
