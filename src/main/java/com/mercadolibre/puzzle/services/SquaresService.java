package com.mercadolibre.puzzle.services;

import com.mercadolibre.puzzle.entities.Square;
import com.mercadolibre.puzzle.utils.QRCode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Felipe on 13/09/2018.
 */
public class SquaresService {

    public static int SQUARE_SIZE = 49;
    public static int TOTAL_ROWS = 20;
    public static int TOTAL_COLUMNS = TOTAL_ROWS;


    public void execute() throws Exception {

        List<Square> squares = loadSquares();

        squares.stream().filter(Square::isEdgeTopLeft).findFirst().map(s -> {
            s.correctColumn = 1;
            s.correctRow = 1;
            return s;
        });
        squares.stream().filter(Square::isEdgeTopRight).findFirst().map(s -> {
            s.correctColumn = TOTAL_COLUMNS;
            s.correctRow = 1;
            return s;
        });
        squares.stream().filter(Square::isEdgeBottomLeft).findFirst().map(s -> {
            s.correctColumn = 1;
            s.correctRow = TOTAL_ROWS;
            return s;
        });
        squares.stream().filter(Square::isEdgeBottomRight).findFirst().map(s -> {
            s.correctColumn = TOTAL_COLUMNS;
            s.correctRow = TOTAL_ROWS;
            return s;
        });

        squares.forEach(square -> {
            if (square.needsToFindParentRight()) {
                List<Square> allRights = squares.stream().filter(square::matchesEdgeRightWithEdgeLeftOf).collect(Collectors.toList());
                if (allRights.size() == 1) {
                    linkLeftAndRight(square, allRights.get(0));
                }
            }
            if (square.needsToFindParentLeft()) {
                List<Square> allLefts = squares.stream().filter(square::matchesEdgeLeftWithEdgeRightOf).collect(Collectors.toList());
                if (allLefts.size() == 1) {
                    linkLeftAndRight(allLefts.get(0), square);
                }
            }
            if (square.needsToFindParentTop()) {
                List<Square> allTops = squares.stream().filter(square::matchesEdgeTopWithEdgeBottomOf).collect(Collectors.toList());
                if (allTops.size() == 1) {
                    linkTopAndBottom(allTops.get(0), square);
                }
            }
            if (square.needsToFindParentBottom()) {
                List<Square> allBottom = squares.stream().filter(square::matchesEdgeBottomWithEdgeTopOf).collect(Collectors.toList());
                if (allBottom.size() == 1) {
                    linkTopAndBottom(square, allBottom.get(0));
                }
            }
        });

        squares.forEach(Square::updateCorrectPositionOfSiblings);

        for (int row = 1; row <= TOTAL_ROWS; row++) {
            for (int column = 1; column <= TOTAL_COLUMNS; column++) {
                boolean squareFounded = findByCorrectRowAndColumn(squares, row, column).isPresent();
                if (squareFounded == false) {
                    Optional<Square> top    = findByCorrectRowAndColumn(squares, row-1, column);
                    Optional<Square> bottom = findByCorrectRowAndColumn(squares, row+1, column);
                    Optional<Square> left   = findByCorrectRowAndColumn(squares, row, column-1);
                    Optional<Square> right  = findByCorrectRowAndColumn(squares, row, column+1);

                    List<Square> squaresMatch = findSquareThatMatchesWith(squares, top, bottom, left, right);
                    if (squaresMatch.size() == 1) {

                        Square squareFinded = squaresMatch.get(0);
                        squareFinded.correctColumn = column;
                        squareFinded.correctRow = row;

                        top.ifPresent(squareTop -> linkTopAndBottom(squareTop, squareFinded));
                        bottom.ifPresent(squareBottom -> linkTopAndBottom(squareFinded, squareBottom));
                        right.ifPresent(squareRight -> linkLeftAndRight(squareFinded, squareRight));
                        left.ifPresent(squareLeft -> linkLeftAndRight(squareLeft, squareFinded));

                        squareFinded.updateCorrectPositionOfSiblings();
                    }
                }
            }
        }

        BufferedImage correctedImage = generateCorrectImage(squares);
        String qrCodeText = new QRCode().readQRCode(correctedImage);

        ImageIO.write(correctedImage, "PNG", new java.io.File("./out/correctedImage.png"));

        System.out.println("QRCode: " + qrCodeText);

        Image correctedImageResized = correctedImage.getScaledInstance(400, 400, Image.SCALE_DEFAULT);
        JOptionPane.showMessageDialog(null, null, qrCodeText, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(correctedImageResized));
    }

    private List<Square> findSquareThatMatchesWith(List<Square> squares, Optional<Square> top, Optional<Square> bottom, Optional<Square> left, Optional<Square> right) {
        return squares.stream()
            .filter(Square::isNotCorrectPosition)
            .filter(square -> {
                if (top.isPresent()) {
                    if (square.matchesEdgeTopWithEdgeBottomOf(top.get()) == false) {
                        return false;
                    }
                }
                if (bottom.isPresent()) {
                    if (square.matchesEdgeBottomWithEdgeTopOf(bottom.get()) == false) {
                        return false;
                    }
                }
                if (left.isPresent()) {
                    if (square.matchesEdgeLeftWithEdgeRightOf(left.get()) == false) {
                        return false;
                    }
                }
                if (right.isPresent()) {
                    if (square.matchesEdgeRightWithEdgeLeftOf(right.get()) == false) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
    }

    private Optional<Square> findByCorrectRowAndColumn(List<Square> squares, int row, int column) {
        return squares.stream().filter(square -> square.correctRow == row && square.correctColumn == column).findFirst();
    }

    private void linkLeftAndRight(Square squareLeft, Square squareRight) {
        squareRight.left = squareLeft;
        squareLeft.right = squareRight;
    }
    private void linkTopAndBottom(Square squareTop, Square squareBottom) {
        squareTop.bottom = squareBottom;
        squareBottom.top = squareTop;
    }

    public BufferedImage generateCorrectImage(List<Square> squares) throws IOException {
        int canvasSize = TOTAL_ROWS * SQUARE_SIZE;
        BufferedImage canvas = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB);

        squares.forEach(square -> {
            canvas.getGraphics().drawImage(square.getImageCleared(), square.getCorrectX(), square.getCorrectY(), square.size, square.size, null);
        });

        return canvas;
    }

    public List<Square> loadSquares() throws IOException {

        InputStream resourceAsStream = SquaresService.class.getResourceAsStream("/challenge.png");
        BufferedImage image = ImageIO.read(resourceAsStream);

        int spaceBeteweenSquares = 1;

        List<Square> squares = new ArrayList<Square>();
        int yPosition = spaceBeteweenSquares;
        for (int row = 1; row <= TOTAL_ROWS; row++) {
            int xPosition = spaceBeteweenSquares;
            for (int column = 1; column <= TOTAL_COLUMNS; column++) {

                Square square = new Square();
                square.originalColumn = column;
                square.originalRow = row;
                square.size = SQUARE_SIZE;
                square.image = image.getSubimage(xPosition, yPosition, SQUARE_SIZE, SQUARE_SIZE);;

                squares.add(square);

                xPosition += SQUARE_SIZE + spaceBeteweenSquares;
            }
            yPosition += SQUARE_SIZE + spaceBeteweenSquares;
        }

        return squares;
    }

}
