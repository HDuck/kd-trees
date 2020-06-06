import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

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
        SET<Point2D> insidePoints = new SET<>();

        for (Point2D point : points) {
            if (rect.contains(point)) insidePoints.add(point);
        }

        return insidePoints;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (isEmpty()) return null;
        Point2D nearest = null;
        for (Point2D point : points) {
            if (nearest == null) {
                nearest = point;
                continue;
            }
            if (point.equals(p)) continue;
            double nearestDistance = p.distanceTo(nearest);
            double pointDistance = p.distanceTo(point);
            if (pointDistance < nearestDistance) nearest = point;
        }
        return new Point2D(nearest.x(), nearest.y());
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }
}
