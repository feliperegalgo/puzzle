package com.mercadolibre.puzzle.entities;

/**
 * Created by Felipe on 13/09/2018.
 */
public class Color {
    public int red;
    public int green;
    public int blue;

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public boolean isBlack() {
        return red == 0 && green == 0 && blue == 0;
    }

    public boolean isWhite() {
        return red == 255 && green == 255 && blue == 255;
    }

    public boolean isEmpty() {
        return isBlack() || isWhite();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        if (red != color.red) return false;
        if (green != color.green) return false;
        return blue == color.blue;
    }

    @Override
    public int hashCode() {
        int result = red;
        result = 31 * result + green;
        result = 31 * result + blue;
        return result;
    }

    @Override
    public String toString() {
        return "Color{" + "red=" + red + ", green=" + green + ", blue=" + blue + '}';
    }

    public boolean matchesWith(Color color) {
        return (this.isEmpty() && color.isEmpty()) || this.equals(color);
    }
}
