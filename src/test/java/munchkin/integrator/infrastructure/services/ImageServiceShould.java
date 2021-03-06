package munchkin.integrator.infrastructure.services;

import munchkin.integrator.domain.asset.Image;
import munchkin.integrator.domain.boards.Board;
import munchkin.integrator.domain.boards.Sizing;
import munchkin.integrator.domain.card.Card;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(PER_CLASS)
class ImageServiceShould {

    private final ImageService imageService;

    private final Resource card;
    private final Resource cardPng;
    private final Resource card25Percent;
    private final Resource card5Percent;

    public ImageServiceShould(
            @Value("classpath:card.jpg") Resource card,
            @Value("classpath:card-25%.jpg") Resource card25Percent,
            @Value("classpath:card-5%.jpg") Resource card5Percent,
            @Value("classpath:card.png") Resource cardPng
    ) {
        MockitoAnnotations.openMocks(this);
        this.imageService = new ImageService();
        this.card = card;
        this.card25Percent = card25Percent;
        this.card5Percent = card5Percent;
        this.cardPng = cardPng;
    }

    @Test
    public void resize_boards_iterate_on_all_images() throws IOException {
        Board mockImage = mock(Board.class);
        List<Board> images = Arrays.asList(mockImage, mockImage, mockImage);
        doReturn(new Image(card.getInputStream().readAllBytes())).when(mockImage).boardImage();

        imageService.reziseBoards(images, 25);

        verify(mockImage, times(3)).boardImage();
    }

    @Test
    public void resize_boards_return_new_byte_image_from_original_image() throws IOException {
        Board mockImage = mock(Board.class);
        byte[] image = card.getInputStream().readAllBytes();
        doReturn(new Image(image)).when(mockImage).boardImage();
        List<Board> images = Arrays.asList(mockImage, mockImage, mockImage);

        List<Board> resultBoards = imageService.reziseBoards(images, 25);

        assertThat(resultBoards).hasSameSizeAs(images);
        resultBoards.forEach(board -> assertThat(board.boardImage().image()).isNotEqualTo(image));
    }

    @Test
    public void resize_boards_should_return_same_image_at_25_percent_size() throws IOException {
        List<Board> images = Collections.singletonList(new Board(0L, new Sizing(1, 1), new Image(card.getInputStream().readAllBytes()), new ArrayList<Card>()));
        BufferedImage expectedImage = ImageIO.read(card25Percent.getInputStream());

        List<Board> resultBoards = imageService.reziseBoards(images, 4);

        BufferedImage generatedImage = ImageIO.read(new ByteArrayInputStream(resultBoards.get(0).boardImage().image()));
        assertThat(generatedImage.getWidth()).isEqualTo(expectedImage.getWidth());
        assertThat(generatedImage.getHeight()).isEqualTo(expectedImage.getHeight());
    }

    @ParameterizedTest
    @MethodSource("parameterizedReductionSource")
    public void resize_boards_should_be_reduction_parameterized(int reduction, Resource resizedCard) throws IOException {
        List<Board> images = Collections.singletonList(new Board(0L, new Sizing(1, 1), new Image(card.getInputStream().readAllBytes()), new ArrayList<Card>()));
        BufferedImage expectedImage = ImageIO.read(resizedCard.getInputStream());

        List<Board> resultBoards = imageService.reziseBoards(images, reduction);

        BufferedImage generatedImage = ImageIO.read(new ByteArrayInputStream(resultBoards.get(0).boardImage().image()));
        assertThat(generatedImage.getWidth()).isEqualTo(expectedImage.getWidth());
        assertThat(generatedImage.getHeight()).isEqualTo(expectedImage.getHeight());
    }

    @SuppressWarnings("unused")
    private Stream<Arguments> parameterizedReductionSource() {
        return Stream.of(
                Arguments.of(4, card25Percent),
                Arguments.of(10, card5Percent)
        );
    }

    @ParameterizedTest
    @MethodSource("parameterizedFileTypeMatch")
    public void reziseBoards_should_conserve_original_file_extension(Resource inputImage) throws IOException {
        List<Board> images = Collections.singletonList(new Board(0L, new Sizing(1, 1), new Image(inputImage.getInputStream().readAllBytes()), new ArrayList<Card>()));
        Iterator<ImageReader> expectedImageReader = ImageIO.getImageReaders(ImageIO.createImageInputStream(inputImage.getInputStream()));

        List<Board> resultBoards = imageService.reziseBoards(images, 25);

        Iterator<ImageReader> generatedImageReader = ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(resultBoards.get(0).boardImage().image())));
        assertThat(generatedImageReader.next().getFormatName()).isEqualTo(expectedImageReader.next().getFormatName());
    }

    @SuppressWarnings("unused")
    private Stream<Resource> parameterizedFileTypeMatch() {
        return Stream.of(
                card,
                cardPng
        );
    }

    @Test
    public void crop_image_for_different_positions(Resource inputImage, Resource outputImage, int column, int line) throws IOException {
        fail();
    }
}