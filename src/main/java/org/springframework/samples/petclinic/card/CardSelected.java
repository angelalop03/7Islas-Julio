package org.springframework.samples.petclinic.card;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardSelected {

    @NotNull
    @NotEmpty
    List<Card> cardsSelected;

    public CardSelected(List<Card> cards){
        this.cardsSelected= cards;
    }
    public CardSelected(){
        
    }

}
