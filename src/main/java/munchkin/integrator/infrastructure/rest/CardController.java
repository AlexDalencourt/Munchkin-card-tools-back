package munchkin.integrator.infrastructure.rest;

import munchkin.integrator.domain.boards.UploadBoard;
import munchkin.integrator.domain.card.Card;
import munchkin.integrator.domain.card.Type;
import munchkin.integrator.infrastructure.rest.responses.cards.CardResponseWithImage;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("cards")
public class CardController {

    private final UploadBoard boardUploadingService;

    @GetMapping()
    public List<CardResponseWithImage> getAll(){
        return null;
    }

    @GetMapping("types")
    public Type[] getAllCardTypes() {
        return Type.values();
    }

    @PutMapping("/crop/{boardId}")
    public List<CardResponseWithImage> cropBoard(@PathVariable Long boardId, @RequestParam Optional<Boolean> persist, HttpServletResponse httpResponse) {
        if (boardId == null) {
            throw new IllegalArgumentException("boardId null");
        }
        List<Card> cropedCards = boardUploadingService.cropBoard(boardId, persist.orElse(false));
        httpResponse.setStatus(OK.value());
        if (persist.orElse(false)) {
            httpResponse.setStatus(CREATED.value());
        }
        return cropedCards.stream().map(card -> card.cardAsset().image().image()).map(CardResponseWithImage::new).collect(Collectors.toList());
    }

    public CardController(UploadBoard uploadBoard) {
        this.boardUploadingService = requireNonNull(uploadBoard);
    }
}
