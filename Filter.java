/**
 * Names: Reagan Buvens & Alison Teske
 * File name: Filter.java
 * Description: Class Filter holds a variety of static methods which
 * apply modifications to a given 2D int array representation of
 * an image.
 */
public class Filter {
    private static final int STARTIDX = 0;
    private static final int FOURTH = 4;
    private static final int EIGTH = 8;
    private static final int SIXTEENTH = 16;
    /**
     * Takes a given Color[][] and modifies each pixel, such that
     * the image as a whole is returned through a negative filter.
     * @param image Color[][] to be filtered
     */
    public static void negative(Color[][] image) {
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                Color c = image[i][j];
                c.setRed(255 - c.getRed());
                c.setGreen(255 - c.getGreen());
                c.setBlue(255 - c.getBlue());
            }
        }
    }

    /**
     * Takes a given Color[][] and modifies each pixel, such that
     * the image as a whole is returned through a grayscale filter.
     * @param image Color[][] to be filtered
     */
    public static void grayscale(Color[][] image) {
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                Color c = image[i][j];
                int gray = (int) (0.3 * c.getRed() + 0.59 * c.getGreen() + 0.11 * c.getBlue());
                c.setRed(gray);
                c.setGreen(gray);
                c.setBlue(gray);
            }
        }
    }

    /**
     * Takes a given Color[][] and modifies each pixel, such that
     * the image as a whole is returned through a tinted filter.
     * @param image Color[][] to be filtered
     * @param tint Color to be used as the tint
     */
    public static void tint(Color[][] image, Color tint) {
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                Color c = image[i][j];
                c.setRed((int)(c.getRed() / 255.0 * tint.getRed()));
                c.setGreen((int)(c.getGreen() / 255.0 * tint.getGreen()));
                c.setBlue((int)(c.getBlue() / 255.0 * tint.getBlue()));
            }
        }
    }

    /**
     * Takes a given Color[][] and returns a modified Color[][
     * such that the image as a whole is given a blurred effect.
     * @param origArray Color[][] to be filtered
     * @param motionArray Color[][] with filter applied
     */
    public static void motionBlur(Color[][] origArray, Color[][] motionArray) {
        for (int x = STARTIDX; x < origArray.length; x++) {
            for (int y = STARTIDX; y < origArray.length; y++) {
                    int redTotal = 0;
                    int greenTotal = 0;
                    int blueTotal = 0;
                    if ((x - 1) < STARTIDX) {//no x-1
                        if ((y - 1) < STARTIDX) {//no y -1
                            redTotal += origArray[x][y].getRed()/FOURTH;
                            redTotal += origArray[x][y+1].getRed()/EIGTH;
                            redTotal += origArray[x+1][y].getRed()/EIGTH;
                            redTotal += origArray[x+1][y+1].getRed()/SIXTEENTH;

                            blueTotal += origArray[x][y].getBlue()/FOURTH;
                            blueTotal += origArray[x][y+1].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y+1].getBlue()/SIXTEENTH;

                            greenTotal += origArray[x][y].getGreen()/FOURTH;
                            greenTotal += origArray[x][y+1].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y+1].getGreen()/SIXTEENTH;
                        
                        } else if ((y + 2) > origArray[STARTIDX].length) {//no y + 1
                            redTotal += origArray[x][y].getRed()/FOURTH;
                            redTotal += origArray[x][y-1].getRed()/EIGTH;
                            redTotal += origArray[x+1][y].getRed()/EIGTH;
                            redTotal += origArray[x+1][y-1].getRed()/SIXTEENTH;

                            blueTotal += origArray[x][y].getBlue()/FOURTH;
                            blueTotal += origArray[x][y-1].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y-1].getBlue()/SIXTEENTH;

                            greenTotal += origArray[x][y].getGreen()/FOURTH;
                            greenTotal += origArray[x][y-1].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y-1].getGreen()/SIXTEENTH;
                        } else {
                            redTotal += origArray[x][y].getRed()/FOURTH;
                            redTotal += origArray[x][y-1].getRed()/EIGTH;
                            redTotal += origArray[x][y+1].getRed()/EIGTH;
                            redTotal += origArray[x+1][y].getRed()/EIGTH;
                            redTotal += origArray[x+1][y+1].getRed()/SIXTEENTH;
                            redTotal += origArray[x+1][y-1].getRed()/SIXTEENTH;

                            blueTotal += origArray[x][y].getBlue()/FOURTH;
                            blueTotal += origArray[x][y-1].getBlue()/EIGTH;
                            blueTotal += origArray[x][y+1].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y+1].getBlue()/SIXTEENTH;
                            blueTotal += origArray[x+1][y-1].getBlue()/SIXTEENTH;

                            greenTotal += origArray[x][y].getGreen()/FOURTH;
                            greenTotal += origArray[x][y-1].getGreen()/EIGTH;
                            greenTotal += origArray[x][y+1].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y+1].getGreen()/SIXTEENTH;
                            greenTotal += origArray[x+1][y-1].getGreen()/SIXTEENTH;
                        }
                    } else if ((x + 2) > origArray.length) {//no x+1
                        if ((y-1) < STARTIDX) {//no y -1
                            redTotal += origArray[x][y].getRed()/FOURTH;
                            redTotal += origArray[x][y+1].getRed()/EIGTH;
                            redTotal += origArray[x-1][y].getRed()/EIGTH;
                            redTotal += origArray[x-1][y+1].getRed()/SIXTEENTH;

                            blueTotal += origArray[x][y].getBlue()/FOURTH;
                            blueTotal += origArray[x][y+1].getBlue()/EIGTH;
                            blueTotal += origArray[x-1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x-1][y+1].getBlue()/SIXTEENTH;

                            greenTotal += origArray[x][y].getGreen()/FOURTH;
                            greenTotal += origArray[x][y+1].getGreen()/EIGTH;
                            greenTotal += origArray[x-1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x-1][y+1].getGreen()/SIXTEENTH;
                        } else if ((y + 2) > origArray[STARTIDX].length) {//no y +1
                            redTotal += origArray[x][y].getRed()/FOURTH;
                            redTotal += origArray[x][y-1].getRed()/EIGTH;
                            redTotal += origArray[x-1][y].getRed()/EIGTH;
                            redTotal += origArray[x-1][y-1].getRed()/SIXTEENTH;

                            blueTotal += origArray[x][y].getBlue()/FOURTH;
                            blueTotal += origArray[x][y-1].getBlue()/EIGTH;
                            blueTotal += origArray[x-1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x-1][y-1].getBlue()/SIXTEENTH;

                            greenTotal += origArray[x][y].getGreen()/FOURTH;
                            greenTotal += origArray[x][y-1].getGreen()/EIGTH;
                            greenTotal += origArray[x-1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x-1][y-1].getGreen()/SIXTEENTH;
                        } else {
                            redTotal += origArray[x][y].getRed()/FOURTH;
                            redTotal += origArray[x][y-1].getRed()/EIGTH;
                            redTotal += origArray[x][y+1].getRed()/EIGTH;
                            redTotal += origArray[x-1][y].getRed()/EIGTH;
                            redTotal += origArray[x-1][y+1].getRed()/SIXTEENTH;
                            redTotal += origArray[x-1][y-1].getRed()/SIXTEENTH;

                            blueTotal += origArray[x][y].getBlue()/FOURTH;
                            blueTotal += origArray[x][y-1].getBlue()/EIGTH;
                            blueTotal += origArray[x][y+1].getBlue()/EIGTH;
                            blueTotal += origArray[x-1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x-1][y+1].getBlue()/SIXTEENTH;
                            blueTotal += origArray[x-1][y-1].getBlue()/SIXTEENTH;

                            greenTotal += origArray[x][y].getGreen()/FOURTH;
                            greenTotal += origArray[x][y-1].getGreen()/EIGTH;
                            greenTotal += origArray[x][y+1].getGreen()/EIGTH;
                            greenTotal += origArray[x-1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x-1][y+1].getGreen()/SIXTEENTH;
                            greenTotal += origArray[x-1][y-1].getGreen()/SIXTEENTH;
                        }
                    } else if ((y - 1) < STARTIDX) {//no y-1
                            redTotal += origArray[x][y].getRed()/FOURTH;
                            redTotal += origArray[x-1][y].getRed()/EIGTH;
                            redTotal += origArray[x+1][y].getRed()/EIGTH;
                            redTotal += origArray[x][y+1].getRed()/EIGTH;
                            redTotal += origArray[x+1][y+1].getRed()/SIXTEENTH;
                            redTotal += origArray[x-1][y+1].getRed()/SIXTEENTH;

                            blueTotal += origArray[x][y].getBlue()/FOURTH;
                            blueTotal += origArray[x-1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x][y+1].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y+1].getBlue()/SIXTEENTH;
                            blueTotal += origArray[x-1][y+1].getBlue()/SIXTEENTH;

                            greenTotal += origArray[x][y].getGreen()/FOURTH;
                            greenTotal += origArray[x-1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x][y+1].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y+1].getGreen()/SIXTEENTH;
                            greenTotal += origArray[x-1][y+1].getGreen()/SIXTEENTH;
                        
                    } else if ((y + 2) > origArray[STARTIDX].length) {//no y+1
                            redTotal += origArray[x][y].getRed()/FOURTH;
                            redTotal += origArray[x-1][y].getRed()/EIGTH;
                            redTotal += origArray[x+1][y].getRed()/EIGTH;
                            redTotal += origArray[x][y-1].getRed()/EIGTH;
                            redTotal += origArray[x+1][y-1].getRed()/SIXTEENTH;
                            redTotal += origArray[x-1][y-1].getRed()/SIXTEENTH;

                            blueTotal += origArray[x][y].getBlue()/FOURTH;
                            blueTotal += origArray[x-1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y].getBlue()/EIGTH;
                            blueTotal += origArray[x][y-1].getBlue()/EIGTH;
                            blueTotal += origArray[x+1][y-1].getBlue()/SIXTEENTH;
                            blueTotal += origArray[x-1][y-1].getBlue()/SIXTEENTH;

                            greenTotal += origArray[x][y].getGreen()/FOURTH;
                            greenTotal += origArray[x-1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y].getGreen()/EIGTH;
                            greenTotal += origArray[x][y-1].getGreen()/EIGTH;
                            greenTotal += origArray[x+1][y-1].getGreen()/SIXTEENTH;
                            greenTotal += origArray[x-1][y-1].getGreen()/SIXTEENTH;
                    } else {
                        redTotal += origArray[x][y].getRed()/FOURTH;
                        redTotal += origArray[x-1][y].getRed()/EIGTH;
                        redTotal += origArray[x+1][y].getRed()/EIGTH;
                        redTotal += origArray[x][y+1].getRed()/EIGTH;
                        redTotal += origArray[x][y-1].getRed()/EIGTH;
                        redTotal += origArray[x+1][y+1].getRed()/SIXTEENTH;
                        redTotal += origArray[x-1][y+1].getRed()/SIXTEENTH;
                        redTotal += origArray[x+1][y-1].getRed()/SIXTEENTH;
                        redTotal += origArray[x-1][y-1].getRed()/SIXTEENTH;

                        blueTotal += origArray[x][y].getBlue()/FOURTH;
                        blueTotal += origArray[x-1][y].getBlue()/EIGTH;
                        blueTotal += origArray[x+1][y].getBlue()/EIGTH;
                        blueTotal += origArray[x][y+1].getBlue()/EIGTH;
                        blueTotal += origArray[x][y-1].getBlue()/EIGTH;
                        blueTotal += origArray[x+1][y+1].getBlue()/SIXTEENTH;
                        blueTotal += origArray[x-1][y+1].getBlue()/SIXTEENTH;
                        blueTotal += origArray[x+1][y-1].getBlue()/SIXTEENTH;
                        blueTotal += origArray[x-1][y-1].getBlue()/SIXTEENTH;

                        greenTotal += origArray[x][y].getGreen()/FOURTH;
                        greenTotal += origArray[x-1][y].getGreen()/EIGTH;
                        greenTotal += origArray[x+1][y].getGreen()/EIGTH;
                        greenTotal += origArray[x][y+1].getGreen()/EIGTH;
                        greenTotal += origArray[x][y-1].getGreen()/EIGTH;
                        greenTotal += origArray[x+1][y+1].getGreen()/SIXTEENTH;
                        greenTotal += origArray[x-1][y+1].getGreen()/SIXTEENTH;
                        greenTotal += origArray[x+1][y-1].getGreen()/SIXTEENTH;
                        greenTotal += origArray[x-1][y-1].getGreen()/SIXTEENTH;
                    }
                    motionArray[x][y] = new Color(Math.abs(redTotal), Math.abs(greenTotal), Math.abs(blueTotal));
                }
        }
        return;
    }
}
