import java.awt.*;

public class Components implements Blob {
    private int numberOfElements;
    private int name;
    private double x;
    private double y;

    /**
     * default constructor
     */
    Components(){
        numberOfElements=0;
        x=0;
        y=0;
    }
    /**
     * adds the given point to the blob so that we can calculate the centroid
     * @param p the point to add
     */
    public void addPoint(Point p){
        x+=p.getX();
        y+=p.getY();
        numberOfElements++;
    }
    @Override
    public Point getCentroid() {
        int r=(int)(x/numberOfElements);
        int c=(int)(y/numberOfElements);
        return new Point(c,r);
    }

    @Override
    public int getLabel() {
        return name;
    }

    @Override
    public void setLabel(int label) {
        name=label;
    }
}
