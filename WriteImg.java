/**
 * Names: Reagan Buvens and Alison Teske
 * File name: WriteImg.java
 * Description: Class WriteImg has methods to write a given Color[][] into
 * an output file, using proper P3 image formatting, and to turn a given
 * P3 ppm into a Color[][].
 */

 import java.util.*;
import java.io.*;
public class WriteImg {
    /**
     * Creates a Color[][] from a given ppm file
     * @param filename ppm file to be turned into Color[][]
     * @return Color[][] representation of the image
     */
    public static Color[][] createTwoD(String filename) throws FileNotFoundException {
        Scanner imageInput = new Scanner(new File(filename));
        while (!imageInput.hasNextInt()) {
            imageInput.nextLine();
        }
            
        int columns = imageInput.nextInt();
        int rows = imageInput.nextInt();
        imageInput.nextInt(); //skip int containing maximum color value
        Color[][] picture = new Color[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int r = imageInput.nextInt();
                int g = imageInput.nextInt();
                int b = imageInput.nextInt();
                picture[i][j] = new Color(r,g,b);
            }
        }
        return picture;
    }

    /**
     * Write the given Color[][] to the given output file filename, using formatting
     * such that the file can be read as a .ppm file
     * @param fileName file to which the Color[][] will be written
     * @param img Color[][] representation of image
     * @throws IOException if an error occurs with the given filename
     */
    public static void writeImg(String fileName, Color[][] img) throws IOException {
        PrintWriter out = new PrintWriter(fileName);
        out.println("P3");
        out.println(img[0].length + " " + img.length+ "\n255\n");
        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[0].length; j++) {
                out.print(img[i][j] + " ");
            }
            out.println();
        }
        out.close();
    } 
}