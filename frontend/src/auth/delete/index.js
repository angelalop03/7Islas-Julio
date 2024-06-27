import { useState } from "react";
import { Link } from "react-router-dom";
import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPage.css";
import tokenService from "../../services/token.service";
import deleteFromList from "../../util/deleteFromList";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";

const jwt = tokenService.getLocalAccessToken();

export default function Login() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [users, setUsers] = useFetchState(
    [],
    `/api/v1/users`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);
  const [password, setPassword] = useState("");

  async function sendDeleteAccountRequest() {
    const user = tokenService.getUser();
    
    // Verifica la contraseña antes de proceder con la eliminación
    const response = await fetch(`/api/v1/users/verify-password`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwt}`,
      },
      body: JSON.stringify({ userId: user.id, password }),
    });

    if (!response.ok) {
      const error = await response.json();
      setMessage(error.message || "Incorrect password");
      setVisible(true);
      return;
    }

    // Procede con la eliminación si la contraseña es correcta
    deleteFromList(
      `/api/v1/users/${user.id}`,
      user.id,
      [users, setUsers],
      [alerts, setAlerts],
      setMessage,
      setVisible
    );
    tokenService.removeUser();
    window.location.href = "/";
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="auth-page-container">
      <div className="auth-form-container">
        <h2 className="text-center text-md">
          {modal}
          Are you sure you want to delete your account?
        </h2>
        <input
          type="password"
          placeholder="Enter your password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="auth-input"
        />
        <div className="options-row">
          <Link className="auth-button" to="/profile" style={{ textDecoration: "none" }}>
            No
          </Link>
          <button className="auth-button" onClick={() => sendDeleteAccountRequest()}>
            Yes
          </button>
        </div>
      </div>
    </div>
  );
}
