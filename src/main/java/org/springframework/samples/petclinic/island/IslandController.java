package org.springframework.samples.petclinic.island;

import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.util.RestPreconditions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/islands")
@Tag(name = "Islands", description = "API for the management of islands")
@SecurityRequirement(name = "bearerAuth")
public class IslandController {

    private final IslandService islandService;

    @Autowired
    public IslandController(IslandService islandService){
        this.islandService= islandService;
    }

    @GetMapping
    public ResponseEntity<List<Island>> findAll() {
        return new ResponseEntity<>((List<Island>) islandService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{islandId}")
    public ResponseEntity<Island> findById(@PathVariable("islandId") int id) {
        return new ResponseEntity<>(islandService.findIslandById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/{islandId}/cards")
    public ResponseEntity<List<Card>> findCardsByIslandId(@PathVariable("islandId") int islandId) {
        return new ResponseEntity<>(this.islandService.findCardsByIslandId(islandId), HttpStatus.OK);
    }

    @GetMapping(value = "/game/{gameId}")
    public ResponseEntity<List<Island>> findIslandsByGameId(@PathVariable("gameId") Integer gameId) {
        return new ResponseEntity<>(islandService.findIslandsByGameId(gameId), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Island> create(@RequestBody @Valid Island island) throws URISyntaxException {
        Island newIsland = new Island();
        BeanUtils.copyProperties(island, newIsland, "id");
        Island savedIsland = this.islandService.saveIsland(newIsland);

        return new ResponseEntity<>(savedIsland, HttpStatus.CREATED);
    }

    @DeleteMapping("/{islandId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> delete(@PathVariable("islandId") int id) {
        RestPreconditions.checkNotNull(islandService.findIslandById(id), "Island", "ID", id);
        islandService.deleteIsland(id);
        return new ResponseEntity<>(new MessageResponse("Island deleted!"), HttpStatus.OK);
    }
   

}
