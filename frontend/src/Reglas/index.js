import React from 'react';
import '../App.css';
import '../static/css/reglas/reglas.css'; 
import reglasImage from'../static/images/Reglas.png';

export default function Rules(){
    return(
        <div className="rules-container">
            <h1 className="rules-title">Reglas del Juego</h1>
            <div className="image-container">
                <img src={reglasImage} alt="Reglas del Juego" className="reglas-image" />
            </div>
        </div>
    );
}