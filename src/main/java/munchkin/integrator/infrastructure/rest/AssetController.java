package munchkin.integrator.infrastructure.rest;

import munchkin.integrator.domain.boards.Board;
import munchkin.integrator.domain.boards.Sizing;
import munchkin.integrator.domain.boards.UploadBoard;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("asset")
public class AssetController {

    private final UploadBoard boardUploadingService;

    @PostMapping("board")
    @ResponseStatus(CREATED)
    public void newBoad(@RequestParam MultipartFile file, @RequestParam int numberOfColumns, @RequestParam int numberOfLines) throws IOException {
        requireNonNull(file);
        if (ImageIO.read(file.getInputStream()) == null) {
            throw new InvalidMediaTypeException("Image file", file.getOriginalFilename());
        }
        if (numberOfColumns <= 0 || numberOfLines <= 0) {
            throw new IllegalArgumentException("Number of columns and lines should be positive and superior to 0");
        }
        if (!boardUploadingService.uploadNewBoard(new Board(null, new Sizing(numberOfColumns, numberOfLines), file.getBytes()))) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
        }
    }

    public AssetController(UploadBoard boardUploadingService) {
        this.boardUploadingService = requireNonNull(boardUploadingService);
    }
}
