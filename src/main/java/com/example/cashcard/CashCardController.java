package com.example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestId, Principal principal) {
        CashCard cashCard = findCashCard(requestId, principal.getName());
        if(cashCard != null) {
            return ResponseEntity.ok(cashCard);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping
    public ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardToSave = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardToSave);
        URI locationOfNewCashCard = ucb.path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<Void> updateCashCard(@PathVariable Long requestId, @RequestBody CashCard cashCardToUpdate, Principal principal) {
        CashCard cashCard = findCashCard(requestId, principal.getName());
        if(cashCard != null){
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardToUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteCashCard(@PathVariable Long requestId, Principal principal) {
        if(cashCardRepository.existsByIdAndOwner(requestId, principal.getName())){
            cashCardRepository.deleteById(requestId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private CashCard findCashCard(Long id, String ownerName) {
        return cashCardRepository.findByIdAndOwner(id, ownerName);
    }
}
