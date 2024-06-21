import "../../static/css/auth/authButton.css";
import "../../static/css/login/register.css";
import React from 'react';

export default function Register() {

  function handleButtonClick(event) {
    const target = event.target;
    const value = target.value;
    if (value === "Player") {
      window.location.href = "/createRegisterPlayer";
    } else {
      window.location.href = "/createRegisterAdmin";
    }
  }

  return (
    <div className="page-container">
    <div className="auth-page-container">
      <div className="auth-form-container">
        <h1>Register</h1>
        <h2 className="text-center text-md">
          What type of user will you be?
        </h2>
        <div className="options-row">
          <button
            className="auth-button"
            value="Admin"
            onClick={handleButtonClick}
          >
            Admin
          </button>
          <button
            className="auth-button"
            value="Player"
            onClick={handleButtonClick}
          >
            Player
          </button>
        </div>
      </div>
    </div>
    </div>
  );
}
