package com.mercadolibre.puzzle.entities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Felipe on 13/09/2018.
 */
public class Square {

    public int originalColumn;
    public int originalRow;
    public int correctColumn = -1000;
    public int correctRow = -1000;

    public Square top;
    public Square left;
    public Square right;
    public Square bottom;

    public BufferedImage image;
    public int size;

    private Square correctPositionOnRightOf;

    public Color getColorTopLeft() {
        return getColor(0, 0);
    }
    public Color getColorTopRight() {
        return getColor(size-1, 0);
    }
    public Color getColorBottomLeft() {
        return getColor(0, size-1);
    }
    public Color getColorBottomRight() {
        return getColor(size-1, size-1);
    }

    public boolean isEdgeLeft() {
        return getColorTopLeft().isEmpty()
                && getColorBottomLeft().isEmpty()
                && getColorTopRight().isNotEmpty()
                && getColorBottomRight().isNotEmpty();
    }

    public boolean isEdgeTop() {
        return getColorTopLeft().isEmpty()
                && getColorTopRight().isEmpty()
                && getColorBottomLeft().isNotEmpty()
                && getColorBottomRight().isNotEmpty();
    }

    public boolean isEdgeRight() {
        return getColorTopRight().isEmpty()
                && getColorBottomRight().isEmpty()
                && getColorBottomLeft().isNotEmpty()
                && getColorTopLeft().isNotEmpty();
    }

    public boolean isEdgeBottom() {
        return getColorBottomRight().isEmpty()
                && getColorBottomLeft().isEmpty()
                && getColorTopLeft().isNotEmpty()
                && getColorTopRight().isNotEmpty();
    }

    public boolean isEdgeTopLeft() {
        return getColorBottomRight().isNotEmpty()
                && getColorTopLeft().isEmpty()
                && getColorBottomLeft().isEmpty()
                && getColorTopRight().isEmpty();
    }

    public boolean isEdgeTopRight() {
        return getColorBottomLeft().isNotEmpty()
                && getColorTopLeft().isEmpty()
                && getColorBottomRight().isEmpty()
                && getColorTopRight().isEmpty();
    }

    public boolean isEdgeBottomRight() {
        return getColorTopLeft().isNotEmpty()
                && getColorBottomLeft().isEmpty()
                && getColorBottomRight().isEmpty()
                && getColorTopRight().isEmpty();
    }

    public boolean isEdgeBottomLeft() {
        return getColorTopRight().isNotEmpty()
                && getColorBottomLeft().isEmpty()
                && getColorBottomRight().isEmpty()
                && getColorTopLeft().isEmpty();
    }

    public Color getColor(int x, int y) {
        int clr =  image.getRGB(x, y);
        int  red   = (clr & 0x00ff0000) >> 16;
        int  green = (clr & 0x0000ff00) >> 8;
        int  blue  =  clr & 0x000000ff;
        return new Color(red, green, blue);
    }

    public void write() {
        try {
            File out = new File("C:\\temp\\challenge\\originalRow["+ originalRow +"] col["+ originalColumn +"].png");
            ImageIO.write(getImageCleared(), "PNG", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getOriginalX() {
        return (originalColumn - 1) * size;
    }

    public int getOriginalY() {
        return (originalRow - 1) * size;
    }

    public int getCorrectX() {
        return (correctColumn - 1) * size;
    }

    public int getCorrectY() {
        return (correctRow - 1) * size;
    }

    public boolean matchesEdgeRightWithEdgeLeftOf(Square square) {
        return this.equals(square) == false
                && this.getColorTopRight().matchesWith(square.getColorTopLeft())
                && this.getColorBottomRight().matchesWith(square.getColorBottomLeft());
    }
    public boolean matchesEdgeLeftWithEdgeRightOf(Square square) {
        return this.equals(square) == false
                && this.getColorTopLeft().matchesWith(square.getColorTopRight())
                && this.getColorBottomLeft().matchesWith(square.getColorBottomRight());
    }
    public boolean matchesEdgeTopWithEdgeBottomOf(Square square) {
        return this.equals(square) == false
                && this.getColorTopLeft().matchesWith(square.getColorBottomLeft())
                && this.getColorTopRight().matchesWith(square.getColorBottomRight());
    }
    public boolean matchesEdgeBottomWithEdgeTopOf(Square square) {
        return this.equals(square) == false
                && this.getColorBottomLeft().matchesWith(square.getColorTopLeft())
                && this.getColorBottomRight().matchesWith(square.getColorTopRight());
    }
    public boolean needsToFindParentRight() {
        return getColorTopRight().isNotEmpty() || getColorBottomRight().isNotEmpty() && right == null;
    }
    public boolean needsToFindParentLeft() {
        return (getColorTopLeft().isNotEmpty() || getColorBottomLeft().isNotEmpty()) && left == null;
    }
    public boolean needsToFindParentTop() {
        return (getColorTopLeft().isNotEmpty() || getColorTopRight().isNotEmpty()) && top == null;
    }
    public boolean needsToFindParentBottom() {
        return (getColorBottomLeft().isNotEmpty() || getColorBottomRight().isNotEmpty()) && bottom == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Square square = (Square) o;

        if (originalColumn != square.originalColumn) return false;
        return originalRow == square.originalRow;
    }

    @Override
    public int hashCode() {
        int result = originalColumn;
        result = 31 * result + originalRow;
        return result;
    }

    public void updateCorrectPositionOnRightOf(Square square) {
        this.correctRow = square.correctRow;
        this.correctColumn = square.correctColumn + 1;
    }

    public boolean isCorrectPosition() {
        return correctColumn >= 1;
    }
    public boolean isNotCorrectPosition() {
        return isCorrectPosition() == false;
    }

    public void updateCorrectPositionOfSiblings() {
        if (isCorrectPosition()) {
            if (top != null && top.isCorrectPosition() == false) {
                top.correctRow = this.correctRow - 1;
                top.correctColumn = this.correctColumn;
                top.updateCorrectPositionOfSiblings();
            }
            if (bottom != null && bottom.isCorrectPosition() == false) {
                bottom.correctRow = this.correctRow + 1;
                bottom.correctColumn = this.correctColumn;
                bottom.updateCorrectPositionOfSiblings();
            }
            if (left != null && left.isCorrectPosition() == false) {
                left.correctRow = this.correctRow;
                left.correctColumn = this.correctColumn - 1;
                left.updateCorrectPositionOfSiblings();
            }
            if (right != null && right.isCorrectPosition() == false) {
                right.correctRow = this.correctRow;
                right.correctColumn = this.correctColumn + 1;
                right.updateCorrectPositionOfSiblings();
            }
        }
    }

    public BufferedImage getImageCleared() {
        java.awt.Color black = new java.awt.Color(0,0,0);
        java.awt.Color white = new java.awt.Color(255,255,255);
        int innerSquareSize = 10;

        boolean topLeft_BottomColorIsBlack = getColor(0, innerSquareSize).isBlack();
        boolean topLeft_RightColorIsBlack = getColor(innerSquareSize, 0).isBlack();
        java.awt.Color topLeftColor;
        if (topLeft_BottomColorIsBlack == topLeft_RightColorIsBlack) {
            topLeftColor = topLeft_BottomColorIsBlack ? black : white;
        } else {
            boolean topLeft_DiagonalColorIsBlack = getColor(innerSquareSize, innerSquareSize).isBlack();
            topLeftColor = topLeft_DiagonalColorIsBlack ? white : black;
        }

        boolean topRight_BottomColorIsBlack = getColor(size-1, innerSquareSize).isBlack();
        boolean topRight_LeftColorIsBlack = getColor(size-innerSquareSize-1, 0).isBlack();
        java.awt.Color topRightColor;
        if (topRight_BottomColorIsBlack == topRight_LeftColorIsBlack) {
            topRightColor = topRight_BottomColorIsBlack ? black : white;
        } else {
            boolean topRigh_DiagonalColorIsBlack = getColor(size-innerSquareSize-1, innerSquareSize).isBlack();
            topRightColor = topRigh_DiagonalColorIsBlack ? white : black;
        }

        boolean bottomLeft_TopColorIsBlack = getColor(0, size - innerSquareSize - 1).isBlack();
        boolean bottomLeft_RightColorIsBlack = getColor(innerSquareSize+1, size-1).isBlack();
        java.awt.Color bottomLeftColor;
        if (bottomLeft_TopColorIsBlack == bottomLeft_RightColorIsBlack) {
            bottomLeftColor = bottomLeft_TopColorIsBlack ? black : white;
        } else {
            boolean bottomLeft_DiagonalColorIsBlack = getColor(innerSquareSize+1, size - innerSquareSize - 1).isBlack();
            bottomLeftColor = bottomLeft_DiagonalColorIsBlack ? white : black;
        }

        boolean bottomRight_TopColorIsBlack = getColor(size-1, size - innerSquareSize - 1).isBlack();
        boolean bottomRight_LeftColorIsBlack = getColor(size-innerSquareSize-1, size-1).isBlack();
        java.awt.Color bottomRightColor;
        if (bottomRight_TopColorIsBlack == bottomRight_LeftColorIsBlack) {
            bottomRightColor = bottomRight_TopColorIsBlack ? black : white;
        } else {
            boolean bottomRight_DiagonalColorIsBlack = getColor(size-innerSquareSize-1, size - innerSquareSize - 1).isBlack();
            bottomRightColor = bottomRight_DiagonalColorIsBlack ? white : black;
        }

        Graphics2D g2d = (Graphics2D) image.getGraphics();

        g2d.setColor(topLeftColor);
        g2d.fillRect(0,0,innerSquareSize, innerSquareSize);

        g2d.setColor(topRightColor);
        g2d.fillRect(size - innerSquareSize,0,innerSquareSize, innerSquareSize);

        g2d.setColor(bottomLeftColor);
        g2d.fillRect(0,size - innerSquareSize,innerSquareSize, innerSquareSize);

        g2d.setColor(bottomRightColor);
        g2d.fillRect(size - innerSquareSize,size - innerSquareSize,innerSquareSize, innerSquareSize);

        g2d.finalize();
        return image;
    }
}
