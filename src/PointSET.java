import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class PointSET {
    private final SET<Point2D> points;

    // construct an empty set of points
    public PointSET() {
        points = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (contains(p)) return;
        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D point : points) {
            point.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        SET<Point2D> insidePoints = new SET<>();

        for (Point2D point : points) {
            if (rect.contains(point)) insidePoints.add(point);
        }

        return insidePoints;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        if (isEmpty()) return null;
        Point2D nearest = null;
        for (Point2D point : points) {
            if (nearest == null) {
                nearest = point;
                continue;
            }
            double pointDistance = p.distanceSquaredTo(point);
            if (pointDistance == 0) {
                nearest = point;
                break;
            }
            double nearestDistance = p.distanceSquaredTo(nearest);
            if (pointDistance < nearestDistance) nearest = point;
        }
        return new Point2D(nearest.x(), nearest.y());
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // initialize the data structures from file
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }

        Point2D p = new Point2D(0.0, 0.1);
        StdOut.println(brute.nearest(p));
    }
}
