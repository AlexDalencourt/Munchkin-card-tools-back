package munchkin.integrator.domain.boards;

import munchkin.integrator.domain.asset.Image;
import munchkin.integrator.domain.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Board {
    private final Long boardId;
    private final Sizing sizing;
    private final Image boardImage;

    private final List<Card> cards = new ArrayList<>();

    public Board(Long boardId, Sizing sizing, Image boardImage, List<Card> cards) {
        this.boardId = boardId;
        this.sizing = requireNonNull(sizing);
        this.boardImage = boardImage;
        this.cards.addAll(cards);
    }

    public Board(Board baseBoard, Image boardImage) {
        this.boardId = baseBoard.boardId();
        this.sizing = baseBoard.sizing();
        this.boardImage = boardImage;
    }

    public Image boardImage() {
        return boardImage;
    }

    public Sizing sizing() {
        return new Sizing(sizing);
    }

    public Long boardId() {
        return boardId;
    }

    public List<Card> cards() {
        return Collections.unmodifiableList(this.cards);
    }

    public void addCard(Card card) {
        this.cards.add(new Card(card));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(boardId, board.boardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId);
    }
}
