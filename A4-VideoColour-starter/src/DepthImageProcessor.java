import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;

/**
 * Complete this class as part of the assignment
 */
public class DepthImageProcessor implements ImageProcessor{
    private double threshold;
    private double minDis;
    private double maxDis;
    private double [][]frame;
    private int [][] colorFrame;
    private int labelCount = -1;
    private ArrayList<Blob> blobs;
    private ArrayList<Blob> previousBlobs;
    /**
     * Default constructor
     */
    DepthImageProcessor(){
        blobs = new ArrayList<>();
        previousBlobs = new ArrayList<>();
    }

    /**
     * Process the given image file, distinguish blob from one another and store them im the blob arraylist
     * this method also compare the blob from the previous frame and if they satisfy the condition that
     * they are the same blob then we gave the new blob the same label from previous frame
     * @param fileName the name of the file to process
     */
    @Override
    public void processFile(String fileName) { //do not throw anything here{
        try {
            File file = new File(fileName);
            Scanner in = new Scanner(file);
            int colSize = in.nextInt();
            int rowSize = in.nextInt();
            blobs = new ArrayList<>();
            frame = new double[rowSize][colSize];
            colorFrame = new int[rowSize][colSize];
            for(int i=0; i<rowSize; i++){
                for(int j=0; j<colSize; j++){
                    frame[i][j]=in.nextDouble();
                    colorFrame[i][j]=-1;
                }
            }
            int colorIndex=-1;
            for(int i=0;i<rowSize;i++) {
                for (int j = 0; j < colSize; j++) {
                    if (colorFrame[i][j]==-1 && frame[i][j] < maxDis && frame[i][j] > minDis) {
                        colorIndex++;
                        
                        Blob currentBlob = bfs(i, j, colorIndex);
                        Blob matchedBlob = new Components();
                        boolean sameBlob = false;
                        for (Blob prevBlob : previousBlobs) {
                            matchedBlob = prevBlob;
                            int distance = (int) Math.abs(currentBlob.getCentroid().getX() - prevBlob.getCentroid().getX());
                            distance += Math.abs(currentBlob.getCentroid().getY() - prevBlob.getCentroid().getY());
                            if (distance <= 10 ){
                                currentBlob.setLabel(prevBlob.getLabel());
                                sameBlob = true;
                                break;
                            }
                        }
                        if(sameBlob) {
                           previousBlobs.remove(matchedBlob);
                        }
                        blobs.add(currentBlob);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File is missing..!!");
            e.printStackTrace();
        }
    }

    /**
     * this method does the breadth first search on the frame array list to find all the point that correspond to same blob.
      * @param row the row of the point
     * @param col the column of the point
     * @param color the initial color of the point
     * @return Component(an object that implements the blob interface)/blob
     */
    public Components bfs(int row,int col,int color){
        labelCount++;
        Components blob = new Components();
        blob.setLabel(labelCount);

        colorFrame[row][col] = color;

        Queue<Point>list = new LinkedList<>();
        list.add(new Point(row,col));

        while(!list.isEmpty()){
            Point next = list.remove();
            int r = (int)next.getX();
            int c = (int)next.getY();
            blob.addPoint(next);
            //up down
            if(check(r-1,c,next,color)){
                list.add(new Point(r-1,c));
            }
            if(check(r+1,c,next,color)){
                list.add(new Point(r+1,c));
            }

            //left right
            if(check(r,c-1,next,color)){
                list.add(new Point(r,c-1));
            }
            if(check(r,c+1,next,color)){
                list.add(new Point(r,c+1));
            }

            //diagonal
            if(check(r+1,c+1,next,color)){
                list.add(new Point(r+1,c+1));
            }
            if(check(r-1,c-1,next,color)){
                list.add(new Point(r-1,c-1));
            }

            if(check(r-1,c+1,next,color)){
                list.add(new Point(r-1,c+1));
            }if(check(r+1,c-1,next,color)){
                list.add(new Point(r+1,c-1));
            }
        }
        return blob;
    }

    /**
     * this method checks if the given two point are connected or not by comparing their x,y position on the frame.
     * We are using the colorFrame array to determine if the point is already been visited (-1 not visited, otherwise they are visited).
     * @param row row of one point
     * @param col column of one point
     * @param p another point object
     * @param color the color of the point
     * @return true if they are valid neighbor otherwise false.
     */
    public boolean check(int row, int col,Point p,int color){
        if(row>=0 && row<frame.length && col>=0 && col<frame[0].length && colorFrame[row][col]==-1 && frame[row][col]<maxDis && frame[row][col]>minDis){

            if((Math.abs(frame[(int)p.getX()][(int)p.getY()]- frame[row][col]))<=threshold){
                colorFrame[row][col]=color;
                return true;
            }
        }
        else if(row>=0 && row<frame.length && col>=0 && col<frame[0].length && colorFrame[row][col]==-1){
            colorFrame[row][col]=-2;
        }
        return false;
    }

    /**
     * Retrieve and return the raw gray image for the previously specified frame
     * @return frame - the raw gray image that corresponds to the frame that was already set
     */
    @Override
    public double[][] getRawImg() {
        return frame;
    }
    /**
     * returns a 2D color array which determines the color of each pixel
     * @return image - 2D array that holds color for each pixel of the frame
     */
    @Override
    public int[][] getColorImg() {
         int [][] image=new int [frame.length][frame[0].length];
         for(int i=0; i<frame.length; i++){
             for(int j=0; j<frame[0].length; j++){
                 if(colorFrame[i][j]!=-1 && colorFrame[i][j]!=-2){
                     int color = (colorFrame[i][j]);
                     color=color%COLORS.length;
                     image[i][j] = COLORS[color];
                 }
             }
         }
        return image;
    }

    /**
     * this method returns the blob arraylist also set the previousBlob arraylist to cuurentBlob list.
     * @return blobs
     */
    @Override
    public ArrayList<Blob> getBlobs() {
        previousBlobs= new ArrayList<>(blobs);
        return blobs;
    }

    @Override
    public void setThreshold(double threshold) {
        this.threshold = threshold;

    }

    @Override
    public int getWidth() {
        return frame[0].length;
    }

    @Override
    public int getHeight() {
        return frame.length;
    }

    @Override
    public void setMinDist(double minDist) {
        minDis=minDist;
    }

    @Override
    public void setMaxDist(double maxDist) {
        maxDis=maxDist;
    }
}