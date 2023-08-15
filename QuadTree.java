import java.io.*;
import java.util.*;
/**
 * Names: Reagan Buvens & Alison Teske
 * File name: QuadTree.java
 * Description: Class QuadTree creates a tree structure in which each node either has 0
 * children or 4 children. Provides additional methods used to populate QuadTree
 * objects with a representation of a .ppm image, to compress images, or to detect 
 * edges in images.
 */

public class QuadTree {
    private Node root; //reference to root Node of tree
    private int size; //size of the tree

    private static final int STARTIDX = 0; //start index of rows/cols
    
    //divison value for motion blur pixel
    private static final int FOURTH = 4;
    //divison value for adjacent pixel values
    private static final int EIGTH = 8;
    //division value for corner pixel values
    private static final int SIXTEENTH = 16;

    //weight given to center pixel in edge detection if in a corner of the image
    private static final int CORNERPIXELWEIGHT = 3; 
    //weight given to center pixel in edge detection if on the side of the image
    private static final int SIDEPIXELWEIGHT = 5;
    //weight given to center pixel in edge detection if not an edge case
    private static final int MIDDLEPIXELWEIGHT = 8;

    private class Node {
        private Color data; //holds data for this Node
        private int depth; //holds depth of the Node relative to the root
        private int startRow; //stores index of this Node's starting row in the Color[][]
        private int endRow; //stores index of this Node's ending row in the Color[][]
        private int startCol; //stores index of this Node's starting column in the Color[][]
        private int endCol; //stores index of this Node's ending column in the Color[][]
        private Node northwest; //reference to northwest child of this Node
        private Node northeast; //reference to northeast child of this Node
        private Node southwest; //reference to southwest child of this Node
        private Node southeast; //reference to southeast child of this Node

        public Node(Color data, int depth, int startRow, int startCol, int endRow, int endCol) {
            this.data = data;
            this.depth = depth;
            this.startRow = startRow;
            this.startCol = startCol;
            this.endRow = endRow;
            this.endCol = endCol;
            northwest = null;
            northeast = null;
            southwest = null;
            southeast = null;
        }

        public Color get() { return data; }

        public void set(Color data) { this.data = data; }

        public int depth() { return depth; }

        public void setDepth() { this.depth = depth; }

        public Node northwest() { return northwest; }

        public void setNorthwest(Node northwest) { this.northwest = northwest; }

        public Node northeast() { return northeast; }

        public void setNortheast(Node northeast) { this.northeast = northeast; }

        public Node southwest() { return southwest; }

        public void setSouthwest(Node southwest) { this.southwest = southwest; }

        public Node southeast() { return southeast; }

        public void setSoutheast(Node southeast) { this.southeast = southeast; }

        public int getStartRow() { return startRow; }

        public int getStartCol() { return startCol; }

        public int getEndRow() { return endRow; }

        public int getEndCol() { return endCol; }

        /**
         * Determines whether a given Node is a leaf, i.e. has no children
         * @return true if Node is a leaf, false otherwise
         */
        public boolean isLeaf() {
            if (northwest() == null && northeast() == null && southeast() == null && southwest() == null ) {
                return true;
            } 
            return false;
        }
    }

    public QuadTree(Color[][] image) {
        root = new Node(findAverageColor(image, STARTIDX, STARTIDX, image.length, image[STARTIDX].length), 0, STARTIDX, STARTIDX, image.length, image[STARTIDX].length);
        size = 1;
    }

    public Color getRootValue() { return root.get(); }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    /**
     * Publically accessible method to divide a QuadTree representation
     * of an image down to the level of the pixel.
     * @param image to be stored in the tree
     */
    public void divide(Color[][] image) {
        divide(root, image);
    }

    /**
     * Private recursive method to divide QuadTree representation
     * of an image down to the level of the pixel. Stores the
     * average color of the section of the image, as well as
     * indices for the section of the image, in the Node.
     * @param n Node to be divided
     * @param image to be stored in the tree
     */
    private void divide(Node n, Color[][] image) {
        //if node is the size of a pixel or null, return
        if (n == null || (n.endCol - n.startCol) * (n.endRow - n.startRow) < 2) {
            return;
        }

        int NWStartRow = n.startRow;
        int NWEndRow = (n.endRow - n.startRow) / 2 + n.startRow;
        int NWStartCol = n.startCol;
        int NWEndCol = (n.endCol - n.startCol) / 2 + n.startCol;
        n.setNorthwest(new Node(findAverageColor(image, NWStartRow, NWStartCol, NWEndRow, NWEndCol), n.depth() + 1, NWStartRow, NWStartCol, NWEndRow, NWEndCol));
        divide(n.northwest, image);

        int NEStartRow = n.startRow;
        int NEEndRow = (n.endRow - n.startRow) / 2 + n.startRow;
        int NEStartCol = (n.endCol - n.startCol) / 2 + n.startCol;
        int NEEndCol = n.endCol;
        n.setNortheast(new Node(findAverageColor(image, NEStartRow, NEStartCol, NEEndRow, NEEndCol), n.depth() + 1, NEStartRow, NEStartCol, NEEndRow, NEEndCol));
        divide(n.northeast, image);

        int SWStartRow = (n.endRow - n.startRow) / 2 + n.startRow;
        int SWEndRow = n.endRow;
        int SWStartCol = n.startCol;
        int SWEndCol = (n.endCol - n.startCol) / 2 + n.startCol;
        n.setSouthwest(new Node(findAverageColor(image, SWStartRow, SWStartCol, SWEndRow, SWEndCol), n.depth() + 1, SWStartRow, SWStartCol, SWEndRow, SWEndCol));
        divide(n.southwest, image);

        int SEStartRow = (n.endRow - n.startRow) / 2 + n.startRow;
        int SEEndRow = n.endRow;
        int SEStartCol = (n.endCol - n.startCol) / 2 + n.startCol;
        int SEEndCol = n.endCol;
        n.setSoutheast(new Node(findAverageColor(image, SEStartRow, SEStartCol, SEEndRow, SEEndCol), n.depth() + 1, SEStartRow, SEStartCol, SEEndRow, SEEndCol));
        divide(n.southeast, image);
        size += 4;
    }


    /**
     * Public method to populate a quadTree with nodes down to 
     * a certain compression or under a certain error threshold
     * @param image array of pixel color data to be compressed
     * @param compressionLevel maxium allowed number of nodes per thousand pixels
     * @param maxAcceptableError error threshold to hit or fall under for color difference
     */
    public void divideRegulated(Color[][] image, double compressionLevel, double maxAcceptableError) {
        divideRegulated(root, image, compressionLevel, maxAcceptableError);
    }

    /**
     * Public method to populate a quadTree with nodes down to 
     * a certain compression or under a certain error threshold
     * @param  n node to be populated with color data 
     * @param image array of pixel color data to be compressed
     * @param compressionLevel maxium allowed number of nodes per thousand pixels
     * @param maxAcceptableError error threshold to hit or fall under for color difference
     */
    private void divideRegulated(Node n, Color[][] image, double compressionLevel, double maxAcceptableError) {

        if (n == null || (n.getEndCol() - n.getStartCol()) * (n.getEndRow() - n.getStartRow()) < 2) {
            return;
        }

        Color average = findAverageColor(image, n.getStartRow(), n.getStartCol(), n.getEndRow(), n.getEndCol());
        double colorDistance = 0;
        for (int i = n.getStartRow(); i < n.getEndRow(); i++) {
            for (int j = n.getStartCol(); j < n.getEndCol(); j++) {
                colorDistance += (Math.pow((image[i][j].getRed() - average.getRed()), 2) + Math.pow((image[i][j].getGreen() - average.getGreen()), 2) + Math.pow((image[i][j].getBlue() - average.getBlue()), 2));
            }
        }
        double error = Math.abs(colorDistance/((n.getEndRow() - n.getStartCol())*(n.getEndRow()- n.getStartRow())));

        int maxLeaves = (int) (compressionLevel * image.length * image[0].length);
        
        if (Math.pow(4, n.depth()) > maxLeaves) {
            return;
        }

        if ( error < maxAcceptableError) {
            return;
        }

        int NWStartRow = n.startRow;
        int NWEndRow = (n.endRow - n.startRow) / 2 + n.startRow;
        int NWStartCol = n.startCol;
        int NWEndCol = (n.endCol - n.startCol) / 2 + n.startCol;
        n.setNorthwest(new Node(findAverageColor(image, NWStartRow, NWStartCol, NWEndRow, NWEndCol), n.depth() + 1, NWStartRow, NWStartCol, NWEndRow, NWEndCol));
        divideRegulated(n.northwest, image, compressionLevel, maxAcceptableError);

        int NEStartRow = n.startRow;
        int NEEndRow = (n.endRow - n.startRow) / 2 + n.startRow;
        int NEStartCol = (n.endCol - n.startCol) / 2 + n.startCol;
        int NEEndCol = n.endCol;
        n.setNortheast(new Node(findAverageColor(image, NEStartRow, NEStartCol, NEEndRow, NEEndCol), n.depth() + 1, NEStartRow, NEStartCol, NEEndRow, NEEndCol));
        divideRegulated(n.northeast, image, compressionLevel, maxAcceptableError);

        int SWStartRow = (n.endRow - n.startRow) / 2 + n.startRow;
        int SWEndRow = n.endRow;
        int SWStartCol = n.startCol;
        int SWEndCol = (n.endCol - n.startCol) / 2 + n.startCol;
        n.setSouthwest(new Node(findAverageColor(image, SWStartRow, SWStartCol, SWEndRow, SWEndCol), n.depth() + 1, SWStartRow, SWStartCol, SWEndRow, SWEndCol));
        divideRegulated(n.southwest, image, compressionLevel, maxAcceptableError);
        

        int SEStartRow = (n.endRow - n.startRow) / 2 + n.startRow;
        int SEEndRow = n.endRow;
        int SEStartCol = (n.endCol - n.startCol) / 2 + n.startCol;
        int SEEndCol = n.endCol;
        n.setSoutheast(new Node(findAverageColor(image, SEStartRow, SEStartCol, SEEndRow, SEEndCol), n.depth() + 1, SEStartRow, SEStartCol, SEEndRow, SEEndCol));
        divideRegulated(n.southeast, image, compressionLevel, maxAcceptableError);
        size += 4;
        
    }

    /**
     * Method to find the average rgb color values for a range of pixels
     * @param image pixel color array which the pixel quadrent is drawn
     * @param startRow start row of pixel quadrant
     * @param startCol start column of pixel quadrant
     * @param endRow end row of pixel quadrant
     * @param endCol end column of pixel quadrant
     * @return Color average color of pixel range
     */
    private Color findAverageColor(Color[][] image, int startRow, int startCol, int endRow, int endCol) {
        double redAverage = 0;
        double greenAverage = 0;
        double blueAverage = 0;
        for (int i = startRow; i < endRow; i++) {
            for (int j = startCol; j < endCol; j++) {
                Color c = image[i][j];
                redAverage += c.getRed();
                greenAverage += c.getGreen();
                blueAverage += c.getBlue();
            }
        }
        redAverage /= (endRow - startRow) * (endCol - startCol);
        greenAverage /= (endRow - startRow) * (endCol - startCol);
        blueAverage /= (endRow - startRow) * (endCol - startCol);
        return new Color((int)redAverage, (int)greenAverage, (int)blueAverage);
    }

    /**
     * Public accessor method to trace edges on an image
     * @param origArray image to be traced
     * @param edgeArray array of image with edge filter applied
     */
    public void edgeDetector(Color[][] origArray, Color[][] edgeArray) {
        edgeDetector(root, origArray, edgeArray);
    }

    /**
     * private method to trace edges on  on nodes small enough
     * @param n node whose size is to be analyzed to decide whether 
     * to calculate edges
     * @param origArray image to be traced
     * @param edgeArray array of image with edge filter applied
     */
    private static void edgeDetector(Node n, Color[][] origArray, Color[][] edgeArray) {
        if (n == null) {
            return;
        }

        Color black = new Color(0,0,0);

        if (n.depth < 4 && n.isLeaf()) {
            for (int i = n.getStartRow(); i < n.getEndRow(); i++) {
                for (int j = n.getStartCol(); j < n.getEndCol(); j++) {
                    edgeArray[i][j] = black;
                }
            }
        } else if (n.depth < 4) {
            edgeDetector(n.northwest, origArray, edgeArray);
            edgeDetector(n.northeast, origArray, edgeArray);
            edgeDetector(n.southwest, origArray, edgeArray);
            edgeDetector(n.southeast, origArray, edgeArray);
        } else {
            for (int x = n.getStartRow(); x < n.getEndRow(); x++) {
                for (int y = n.getStartCol(); y < n.getEndCol(); y++) {
                    int redTotal = 0;
                    int greenTotal = 0;
                    int blueTotal = 0;
                    if ((x - 1) < STARTIDX) {//at first row
                        if ((y - 1) < STARTIDX) {//at first coloumn
                            for (int i = x; i < x + 2; i++) {
                                for (int j = y; j < y + 2; j++) {
                                    if (i == x && j == y) {
                                        redTotal += CORNERPIXELWEIGHT * (origArray[i][j].getRed());
                                        greenTotal += CORNERPIXELWEIGHT * (origArray[i][j].getGreen());
                                        blueTotal += CORNERPIXELWEIGHT * (origArray[i][j].getBlue());
                                    } else {
                                        redTotal -= origArray[i][j].getRed();
                                        greenTotal -= origArray[i][j].getGreen();
                                        blueTotal -= origArray[i][j].getBlue();
                                    }
                                }
                            }
                        } else if ((y + 2) > origArray[STARTIDX].length) {//at last column
                            for (int i = x; i < x + 2; i++) {
                                for (int j = y - 1; j < y + 1; j++) {
                                    if (i == x && j == y) {
                                        redTotal += CORNERPIXELWEIGHT * (origArray[i][j].getRed());
                                        greenTotal += CORNERPIXELWEIGHT * (origArray[i][j].getGreen());
                                        blueTotal += CORNERPIXELWEIGHT * (origArray[i][j].getBlue());
                                    } else {
                                        redTotal -= origArray[i][j].getRed();
                                        greenTotal -= origArray[i][j].getGreen();
                                        blueTotal -= origArray[i][j].getBlue();
                                    }
                                }
                            }
                        } else {//at middle column
                            for (int i = x; i < x + 2; i++) {
                                for (int j = y - 1; j < y + 2; j++) {
                                    if (i == x && j == y) {
                                        redTotal += SIDEPIXELWEIGHT * (origArray[i][j].getRed());
                                        greenTotal += SIDEPIXELWEIGHT * (origArray[i][j].getGreen());
                                        blueTotal += SIDEPIXELWEIGHT * (origArray[i][j].getBlue());
                                    } else {
                                        redTotal -= origArray[i][j].getRed();
                                        greenTotal -= origArray[i][j].getGreen();
                                        blueTotal -= origArray[i][j].getBlue();
                                    }
                                }
                            }
                        }
                    } else if ((x + 2) > origArray.length) {//at last row
                        if ((y-1) < STARTIDX) {
                            for (int i = x - 1; i < x + 1; i++) {
                                for (int j = y; j < y + 2; j++) {
                                    if (i == x && j == y) {
                                        redTotal += CORNERPIXELWEIGHT * (origArray[i][j].getRed());
                                        greenTotal += CORNERPIXELWEIGHT * (origArray[i][j].getGreen());
                                        blueTotal += CORNERPIXELWEIGHT * (origArray[i][j].getBlue());
                                    } else {
                                        redTotal -= origArray[i][j].getRed();
                                        greenTotal -= origArray[i][j].getGreen();
                                        blueTotal -= origArray[i][j].getBlue();
                                    }
                                }
                            }
                        } else if ((y + 2) > origArray[STARTIDX].length) {
                            for (int i = x - 1; i < x + 1; i++) {
                                for (int j = y - 1; j < y + 1; j++) {
                                    if (i == x && j == y) {
                                        redTotal += CORNERPIXELWEIGHT * (origArray[i][j].getRed());
                                        greenTotal += CORNERPIXELWEIGHT * (origArray[i][j].getGreen());
                                        blueTotal += CORNERPIXELWEIGHT * (origArray[i][j].getBlue());
                                    } else {
                                        redTotal -= origArray[i][j].getRed();
                                        greenTotal -= origArray[i][j].getGreen();
                                        blueTotal -= origArray[i][j].getBlue();
                                    }
                                }
                            }
                        } else {
                            for (int i = x - 1; i < x + 1; i++) {
                                for (int j = y - 1; j < y + 2; j++) {
                                    if (i == x && j == y) {
                                        redTotal += SIDEPIXELWEIGHT * (origArray[i][j].getRed());
                                        greenTotal += SIDEPIXELWEIGHT * (origArray[i][j].getGreen());
                                        blueTotal += SIDEPIXELWEIGHT * (origArray[i][j].getBlue());
                                    } else {
                                        redTotal -= origArray[i][j].getRed();
                                        greenTotal -= origArray[i][j].getGreen();
                                        blueTotal -= origArray[i][j].getBlue();
                                    }
                                }
                            }
                        }
                    } else if ((y - 1) < STARTIDX) {
                        for (int i = x - 1; i < x + 2; i++) {
                            for (int j = y; j < y + 2; j++) {
                                if (i == x && j == y) {
                                    redTotal += SIDEPIXELWEIGHT * (origArray[i][j].getRed());
                                    greenTotal += SIDEPIXELWEIGHT * (origArray[i][j].getGreen());
                                    blueTotal += SIDEPIXELWEIGHT * (origArray[i][j].getBlue());
                                } else {
                                    redTotal -= origArray[i][j].getRed();
                                    greenTotal -= origArray[i][j].getGreen();
                                    blueTotal -= origArray[i][j].getBlue();
                                }
                            }
                        }
                    } else if ((y + 2) > origArray[STARTIDX].length) {
                        for (int i = x - 1; i < x + 2; i++) {
                            for (int j = y - 1; j < y + 1; j++) {
                                if (i == x && j == y) {
                                    redTotal += SIDEPIXELWEIGHT * (origArray[i][j].getRed());
                                    greenTotal += SIDEPIXELWEIGHT * (origArray[i][j].getGreen());
                                    blueTotal += SIDEPIXELWEIGHT * (origArray[i][j].getBlue());
                                } else {
                                    redTotal -= origArray[i][j].getRed();
                                    greenTotal -= origArray[i][j].getGreen();
                                    blueTotal -= origArray[i][j].getBlue();
                                }
                            }
                        }
                    } else {
                        for (int i = x - 1; i < x + 2; i++) {
                            for (int j = y - 1; j < y + 2; j++) {
                                if (i == x && j == y) {
                                    redTotal += MIDDLEPIXELWEIGHT * (origArray[i][j].getRed());
                                    greenTotal += MIDDLEPIXELWEIGHT * (origArray[i][j].getGreen());
                                    blueTotal += MIDDLEPIXELWEIGHT * (origArray[i][j].getBlue());
                                } else {
                                    redTotal -= origArray[i][j].getRed();
                                    greenTotal -= origArray[i][j].getGreen();
                                    blueTotal -= origArray[i][j].getBlue();
                                }
                            }

                        }
                        
                    }
                    edgeArray[x][y] = new Color(Math.abs(redTotal), Math.abs(greenTotal), Math.abs(blueTotal));
                }
            }
            
        }
        return;
    }

    /**
     * Public method to populate a 2D array of color data from a QuadTree
     * @param newPicture array of color data to be populated
     */
    public void quadCompression(Color[][] newPicture) {
        quadCompression(root, newPicture);
    }

   
    /**
     * Private method populate a 2D array of color data 
     * @param n node of color data used to populate 2D array
     * @param newPicture array of color data to be populated
     */
    private static void quadCompression(Node n, Color[][] newPicture) {
        if (n == null) {
            return;
        }

        for (int i = n.getStartRow(); i < n.getEndRow(); i++) {
            for (int j = n.getStartCol(); j < n.getEndCol(); j++) {
                newPicture[i][j] = new Color(n.get().getRed(), n.get().getGreen(), n.get().getBlue());
            }
        }
        quadCompression(n.northwest(), newPicture);
        quadCompression(n.northeast(), newPicture);
        quadCompression(n.southeast(), newPicture);
        quadCompression(n.southwest(), newPicture);

    }

    /**
     * Public method to create an array of color data from a QuadTree 
     * and outline the QuadTree nodes
     * @param newPicture array of color data to be populated
     */
    public void quadCompressionOutlined(Color[][] newPicture) {
        quadCompressionOutlined(root, newPicture);
    }

    /**
     * Private method to create an array of color data from a QuadTree 
     * and outline the QuadTree nodes
     * @param n node of data to populate the array and to be outlined
     * @param newPicture array of color data to be populated
     */
    private static void quadCompressionOutlined(Node n, Color[][] newPicture) {
        if (n == null) {
            return;
        }
        Color outlineRed = new Color(255,0,0);

        for (int i = n.getStartRow(); i < n.getEndRow(); i++) {
            for (int j = n.getStartCol(); j < n.getEndCol(); j++) {
                if (i == n.getStartRow() || i == n.getEndRow()-1) {
                    newPicture[i][j] = outlineRed;
                } else if (j == n.getStartCol() || j == n.getEndCol()-1) {
                    newPicture[i][j] = outlineRed;
                } else {
                    newPicture[i][j] = new Color(n.get().getRed(), n.get().getGreen(), n.get().getBlue());
                }
            }
        }
        quadCompressionOutlined(n.northwest(), newPicture);
        quadCompressionOutlined(n.northeast(), newPicture);
        quadCompressionOutlined(n.southeast(), newPicture);
        quadCompressionOutlined(n.southwest(), newPicture);
    }

    /**
     * Public method to outline nodes on an outlined image
     * @param edgePicture array of outlined pixels
     */
    public void quadEdgeOutlined(Color[][] edgePicture) {
        quadEdgeOutlined(root, edgePicture);
    }

    /**
     * Private method to outline nodes on an outlined image 
     * @param n node to be outlined
     * @param edgePicture array of outlined pixels
     */
    private static void quadEdgeOutlined(Node n, Color[][] edgePicture) {
        if (n == null) {
            return;
        }
        Color outlineRed = new Color(255,0,0);

        for (int i = n.getStartRow(); i < n.getEndRow(); i++) {
            for (int j = n.getStartCol(); j < n.getEndCol(); j++) {
                if (i == n.getStartRow() || i == n.getEndRow()-1) {
                    edgePicture[i][j] = outlineRed;
                } else if (j == n.getStartCol() || j == n.getEndCol()-1) {
                    edgePicture[i][j] = outlineRed;
                } 
            }
        }
        quadEdgeOutlined(n.northwest(), edgePicture);
        quadEdgeOutlined(n.northeast(), edgePicture);
        quadEdgeOutlined(n.southeast(), edgePicture);
        quadEdgeOutlined(n.southwest(), edgePicture);
    }

    /**
     * Public method to create a blurred effect on an image
     * @param origArray Color[][] to be filtered
     * @param motionArray Color[][] with filter applied
     */
    public void motionBlur(Color[][] origArray, Color[][] motionArray) {
        motionBlur(root, origArray, motionArray);
    }

    /**
     * Takes a given Color[][] and modifies so that the image as a whole 
     * is given a blurred effect on detailed nodes
     * @param n node which is used to figure out whether array 
     * segment is worth filtering.
     * @param origArray Color[][] to be filtered
     * @param motionArray Color[][] with filter applied
     */
    private void motionBlur(Node n, Color[][] origArray, Color[][] motionArray) {
        if (n == null) {
            return;
        } 

        if (n.depth < 3 && n.isLeaf()) {
            Color ave = findAverageColor(origArray, n.getStartRow(), n.getStartCol(), n.getEndRow(), n.getEndCol());
            for (int a = n.getStartRow(); a < (n.getEndRow()); a++) {
                for (int b = n.getStartCol(); b < (n.getEndCol()); b++) {
                    motionArray[a][b] = ave;
                }
            }
            return;
        } else if (n.depth < 3) {
            motionBlur(n.northwest, origArray, motionArray);
            motionBlur(n.northeast, origArray, motionArray);
            motionBlur(n.southwest, origArray, motionArray);
            motionBlur(n.southeast, origArray, motionArray);
        } else {
            //System.out.println("start " + n.getStartRow() + "end " +  (n.getEndRow() - n.getStartRow()));
            for (int x = n.getStartRow(); x < (n.getEndRow()); x++) {
                for (int y = n.getStartCol(); y < (n.getEndCol()); y++) {
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
        }
    }
}