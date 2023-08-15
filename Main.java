/**
 * Names: Reagan Buvens
 * File name: Main.java
 * Description: Driver class for A9.
 */

import java.io.*;

public class Main {
    public static final double[] COMPRESSIONLEVELS = {.002, .004, .01, .033, .077, .2, .5, .75}; //values for the compression levels
    public static final int NUMCOMPRESSIONLEVELS = 8; //number of levels of compression, or number of QuadTrees needed
    public static final double ERRORTOLERANCE = 5; //maximum tolerance for mean squared error in each node (overridden by compression level)
    public static final double DEFAULTCOMPRESSION = .01; 

    public static void main(String[] args) {
        try {
            String infile = "";
            String outfileStub = "";
            boolean compress = false;
            boolean edgeDetect = false;
            boolean blur = false;
            boolean outlined = false;

            
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-i")) {
                    if (i + 1 < args.length) { infile = args[++i]; }
                    else { throw new IllegalArgumentException(); }
                }
                else if (args[i].equals("-o")) {
                    if (i + 1 < args.length) { outfileStub = args[++i]; }
                    else { throw new IllegalArgumentException(); }
                }
                else if (args[i].equals("-c")) {
                    compress = true;
                }
                else if (args[i].equals("-e")) {
                    edgeDetect = true;
                }
                else if (args[i].equals("-x")) {
                    blur = true;
                }
                else if (args[i].equals("-t")) {
                    outlined = true;
                }
                else { throw new IllegalArgumentException(); }
            }

            Color[][] originalImage = WriteImg.createTwoD(infile);
            Color[][] newImage = new Color[originalImage.length][originalImage[0].length];

            if (compress) {
                QuadTree[] compressionTrees = new QuadTree[NUMCOMPRESSIONLEVELS];
                for (int i = 0; i < compressionTrees.length; i++) {
                    compressionTrees[i] = new QuadTree(originalImage);
                    compressionTrees[i].divideRegulated(originalImage, COMPRESSIONLEVELS[i], ERRORTOLERANCE);
                }
                if (outlined) {
                    for (int i = 0; i < compressionTrees.length; i++) {
                        compressionTrees[i].quadCompressionOutlined(newImage);
                        String outFileName = outfileStub + "-" + (i+1) + ".ppm";
                        WriteImg.writeImg(outFileName, newImage);
                    }
                }
                else {
                    for (int i = 0; i < compressionTrees.length; i++) {
                        compressionTrees[i].quadCompression(newImage);
                        String outFileName = outfileStub + "-" + (i+1) + ".ppm";
                        WriteImg.writeImg(outFileName, newImage);
                    }
                }
            }
            else if (edgeDetect) {
                QuadTree edgeTree = new QuadTree(originalImage);
                edgeTree.divideRegulated(originalImage, DEFAULTCOMPRESSION, ERRORTOLERANCE);
                edgeTree.edgeDetector(originalImage, newImage);
                if (outlined) {
                    edgeTree.quadEdgeOutlined(newImage);
                }
                WriteImg.writeImg(outfileStub + ".ppm", newImage);
            }
            else if (blur) {
                WriteImg.writeImg("testing.ppm", originalImage);
                QuadTree blurTree = new QuadTree(originalImage);
                blurTree.divideRegulated(originalImage, DEFAULTCOMPRESSION, ERRORTOLERANCE);
                blurTree.motionBlur(originalImage, newImage);
                if (outlined) {
                    blurTree.quadEdgeOutlined(newImage);
                }
                WriteImg.writeImg(outfileStub + ".ppm", newImage);
            }
            else {
                throw new IllegalArgumentException();
            }
        } 
        catch (FileNotFoundException e) {
            System.out.println("File not found");
        } 
        catch (IllegalArgumentException e) {
            System.out.println("Illegal or missing argument");
        } 
        catch (IOException e) {
            System.out.println("Error with output file name");
        }
    }
}