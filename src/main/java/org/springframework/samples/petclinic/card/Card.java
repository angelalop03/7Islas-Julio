package org.springframework.samples.petclinic.card;

import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.island.Island;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cards")
public class Card extends BaseEntity{

    @NotNull
    Type type;

    @NotNull
    Boolean isSelected;

    @NotNull
    Boolean reversed;

    @ManyToOne
    Island island;

    @ManyToOne
    Game game;

    @ManyToOne
    Player player;

    

}
