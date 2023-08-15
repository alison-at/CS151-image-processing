/**
 * Names: Reagan Buvens & Alison Teske
 * File name: Color.java
 * Description: Class Color holds three ints (RGB) to describe the color of a single pixel.
 */
public class Color {
    private int red; //value 0-255 for red
    private int green; //value 0-255 for green
    private int blue; //value 0-255 for blue

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void setRed(int red) { this.red = red; }

    public int getRed() { return red; }

    public void setGreen(int green) { this.green = green; }

    public int getGreen() { return green; }

    public void setBlue(int blue) { this.blue = blue; }

    public int getBlue() { return blue; }

    @Override
    public String toString() {
        return red + " " + green + " " + blue;
    }
}