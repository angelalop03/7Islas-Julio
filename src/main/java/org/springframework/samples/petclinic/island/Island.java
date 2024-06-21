package org.springframework.samples.petclinic.island;

import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Setter
@Getter
@Table(name = "islands")
public class Island extends BaseEntity{

    @NotNull
    Integer num;

    @ManyToOne
    Game game;

}
