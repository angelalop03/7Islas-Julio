import changeTurn from "./changeTurn";


class cardSelected {
    constructor(cardsSelected) {
        this.cardsSelected = cardsSelected;
    }
}

export default function move(game, card, resultadoTirada, mano, player, jwt, setMessage, setVisible, setDiceIsThrown, diceIsThrown) {
    let cartasSeleccionadas = mano.filter(carta => carta.isSelected === true);
    
    if (!diceIsThrown || resultadoTirada === 0) {
        window.alert("Para jugar, tira el dado.");
    } else if (resultadoTirada === card.island.num && cartasSeleccionadas.length === 0) {
        fetch(`/api/v1/cards/${card.id}/player/${player.id}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
            },
        })
            .then((response) => response.json())
            .then((json) => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else {
                    setDiceIsThrown(false);
                    changeTurn(game, jwt, setMessage, setVisible);
                }
            })
            .catch((message) => alert(message));
    } else if (Math.abs(card.island.num - resultadoTirada) === cartasSeleccionadas.length) {
        let cartasManoSeleccionadas = new cardSelected(cartasSeleccionadas);
        fetch(`/api/v1/cards/${card.id}/player/${player.id}/selectedCards`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(cartasManoSeleccionadas),
        })
            .then((response) => response.json())
            .then((json) => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else {
                    setDiceIsThrown(false);
                    changeTurn(game, jwt, setMessage, setVisible);
                }
            })
            .catch((message) => alert(message));
        console.log('cartasManoSeleccionadas:', cartasManoSeleccionadas);  // Agregado para verificar los datos

    } else {
        window.alert("Cartas seleccionadas: " + cartasSeleccionadas.length + ". Tirada: " + resultadoTirada + 
            ". No puedes viajar a esta isla.");
    }
}


