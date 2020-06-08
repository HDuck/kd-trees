import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private static final boolean ORDINATE_AXIS = true;
    private static final boolean ABSISS_AXIS = false;
    private Node root = null;

    // construct an empty set of points
    public KdTree() {
    }

    private class Node {
        public boolean type = ORDINATE_AXIS;
        public Point2D point;
        public int count = 1;
        public Node left = null;
        public Node right = null;

        public Node(Point2D p) {
            point = p;
        }

        public int compareTo(Node that) {
            Point2D thisPoint = this.point;
            Point2D thatPoint = that.point;
            if (type == ORDINATE_AXIS) {
                if (thisPoint.x() > thatPoint.x()) return 1;
                if (thisPoint.x() < thatPoint.x()) return -1;
            }
            if (type == ABSISS_AXIS) {
                if (thisPoint.y() > thatPoint.y()) return 1;
                if (thisPoint.y() < thatPoint.y()) return -1;
            }
            return 0;
        }

        public int compareToPoint(Point2D thatPoint) {
            Point2D thisPoint = this.point;
            if (type == ORDINATE_AXIS) {
                if (thisPoint.x() < thatPoint.x()) return 1;
                if (thisPoint.x() > thatPoint.x()) return -1;
            }
            if (type == ABSISS_AXIS) {
                if (thisPoint.y() < thatPoint.y()) return 1;
                if (thisPoint.y() > thatPoint.y()) return -1;
            }
            return 0;
        }

        public boolean equals(Object that) {
            if (that == this) return true;
            if (that == null) return false;
            if (that instanceof Node) {
                Node thatNode = (Node) that;
                Point2D thatPoint = thatNode.point;
                Point2D thisPoint = this.point;
                return thisPoint.equals(thatPoint);
            }
            return false;
        }
    }

    // is the set empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the set
    public int size() {
        if (isEmpty()) return 0;
        return root.count;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        root = insertPoint(p, root);
    }

    private Node insertPoint(Point2D point, Node node) {
        if (node == null) return new Node(point);
        if (node.point.equals(point)) return node;
        int comparison = node.compareToPoint(point);
        if (comparison > 0) {
            node.right = insertPoint(point, node.right);
            node.right.type = !node.type;
        }
        if (comparison <= 0) {
            node.left = insertPoint(point, node.left);
            node.left.type = !node.type;
        }
        int leftNodeCount = node.left != null ? node.left.count : 0;
        int rightNodeCount = node.right != null ? node.right.count : 0;
        node.count = 1 + leftNodeCount + rightNodeCount;
        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return false;
        return containsPoint(p, root);
    }

    private boolean containsPoint(Point2D point, Node node) {
        if (node == null) return false;
        if (node.point.equals(point)) return true;
        int comparison = node.compareToPoint(point);
        if (comparison > 0) {
            return containsPoint(point, node.right);
        }
        if (comparison < 0) {
            return containsPoint(point, node.left);
        }
        if (node.left != null) return containsPoint(point, node.left);
        if (node.right != null) return containsPoint(point, node.right);
        return false;
    }

    // draw all points to standard draw
    public void draw() {
        if (isEmpty()) return;
        drawRootLine();
        drawPoints(root);
    }

    private void drawPoints(Node currentNode) {
        currentNode.point.draw();

        if (currentNode.left != null) {
            drawPoints(currentNode.left);
            drawChildNodeLine(currentNode.left, currentNode);
        }
        if (currentNode.right != null) {
            drawPoints(currentNode.right);
            drawChildNodeLine(currentNode.right, currentNode);
        }
    }

    private void drawRootLine() {
        Line rootLine = new Line(root.point.x(), 0, root.point.x(), 1);
        drawLine(rootLine, root.type);
    }

    private void drawChildNodeLine(Node child, Node parent) {
        double x0 = 0, y0 = 0, x1 = 0, y1 = 0;
        boolean isLeft = child.equals(parent.left);

        if (child.type == ORDINATE_AXIS) {
            x0 = child.point.x();
            x1 = child.point.x();
            y0 = isLeft ? 0 : parent.point.y();
            y1 = isLeft ? parent.point.y() : 1;
        }
        if (child.type == ABSISS_AXIS) {
            y0 = child.point.y();
            y1 = child.point.y();
            x0 = isLeft ? 0 : parent.point.x();
            x1 = isLeft ? parent.point.x() : 1;
        }

        Line line = new Line(x0, y0, x1, y1);
        drawLine(line, child.type);
    }

    private void drawLine(Line line, boolean axisType) {
        StdDraw.setPenRadius(0.001);
        StdDraw.setPenColor(axisType == ORDINATE_AXIS ? StdDraw.RED : StdDraw.BLUE);
        StdDraw.line(line.x0, line.y0, line.x1, line.y1);
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
    }

    private static class Line {
        public double x0;
        public double y0;
        public double x1;
        public double y1;

        public Line(double x0, double y0, double x1, double y1) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        SET<Point2D> insidePoints = new SET<>();

        if (isEmpty()) {
            return insidePoints;
        }

        findInsidePoints(insidePoints, rect, root);
        return insidePoints;
    }

    private void findInsidePoints(SET<Point2D> insidePoints, RectHV rect, Node node) {
        if (node == null) return;

        Point2D nodePoint = node.point;

        if (rect.contains(nodePoint)) insidePoints.add(nodePoint);

        boolean isRectLeftOnly = false;
        boolean isRectRightOnly = false;

        if (node.type == ORDINATE_AXIS) {
            isRectLeftOnly = nodePoint.x() > rect.xmax();
            isRectRightOnly = nodePoint.x() < rect.xmin();
        }

        if (node.type == ABSISS_AXIS) {
            isRectLeftOnly = nodePoint.y() > rect.ymax();
            isRectRightOnly = nodePoint.y() < rect.ymin();
        }

        if (isRectLeftOnly) {
            findInsidePoints(insidePoints, rect, node.left);
        }
        else if (isRectRightOnly) {
            findInsidePoints(insidePoints, rect, node.right);
        }
        else {
            findInsidePoints(insidePoints, rect, node.left);
            findInsidePoints(insidePoints, rect, node.right);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        if (isEmpty()) return null;
        double minDistance = p.distanceSquaredTo(root.point);
        return findNearestPoint(p, root, minDistance);
    }

    private Point2D findNearestPoint(Point2D p, Node node, double minDistance) {
        if (node == null) return null;

        Point2D nodePoint = node.point;
        if (minDistance == 0) return nodePoint;
        Point2D nearestPoint = nodePoint;

        double currentDistance = p.distanceSquaredTo(nodePoint);
        if (currentDistance < minDistance) {
            nearestPoint = nodePoint;
            minDistance = currentDistance;
        }

        Point2D rightNearestPoint = null;
        Point2D leftNearestPoint = null;
        Point2D minDistanceNodeLinePoint = node.type == ORDINATE_AXIS
            ? new Point2D(node.point.x(), p.y())
            : new Point2D(p.x(), node.point.y());
        double minLinePointDistance = p.distanceSquaredTo(minDistanceNodeLinePoint);

        int pointPos = node.compareToPoint(p);
        if (pointPos > 0) {
            rightNearestPoint = findNearestPoint(p, node.right, minDistance);

            if (rightNearestPoint != null) {
                double rightMinDistance = p.distanceSquaredTo(rightNearestPoint);
                if (rightMinDistance < minDistance) {
                    nearestPoint = rightNearestPoint;
                    minDistance = rightMinDistance;
                }
            }

            if (minLinePointDistance >= minDistance) {
                return nearestPoint;
            }

            leftNearestPoint = findNearestPoint(p, node.left, minDistance);

            if (leftNearestPoint != null) {
                double leftMinDistance = p.distanceSquaredTo(leftNearestPoint);
                if (leftMinDistance < minDistance) {
                    nearestPoint = leftNearestPoint;
                    minDistance = leftMinDistance;
                }
            }
        }
        if (pointPos <= 0) {
            leftNearestPoint = findNearestPoint(p, node.left, minDistance);

            if (leftNearestPoint != null) {
                double leftMinDistance = p.distanceSquaredTo(leftNearestPoint);
                if (leftMinDistance < minDistance) {
                    nearestPoint = leftNearestPoint;
                    minDistance = leftMinDistance;
                }
            }

            if (minLinePointDistance >= minDistance) {
                return nearestPoint;
            }

            rightNearestPoint = findNearestPoint(p, node.right, minDistance);

            if (rightNearestPoint != null) {
                double rightMinDistance = p.distanceSquaredTo(rightNearestPoint);
                if (rightMinDistance < minDistance) {
                    nearestPoint = rightNearestPoint;
                    minDistance = rightMinDistance;
                }
            }
        }

        return nearestPoint;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // initialize the data structures from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            StdOut.println(kdtree.size());
            StdOut.println(kdtree.isEmpty());
        }

        StdOut.println("### TREE ###");
        kdtree.drawTree();
        StdOut.println();

        StdOut.println("### INSIDE POINTS ###");
        RectHV[] checkingRects = new RectHV[5];
        checkingRects[0] = new RectHV(0, 0.7, 0.15, 0.9);

        int checkingIndex = 0;
        while (checkingIndex < 5) {
            RectHV rect = checkingRects[checkingIndex++];
            if (rect == null) break;

            StdOut.println("Rect: " + rect);
            for (Point2D insidePoint : kdtree.range(rect)) {
                StdOut.println("Point: " + insidePoint);
            }
            StdOut.println("---#---#---");
        }

        StdOut.println("### CONTAINS ###");
        Point2D p = new Point2D(0.7, 0.65);
        StdOut.println("point: " + p + " -> " + kdtree.contains(p));
    }

    public void drawTree() {
        Point2D parent = root.point;
        Node leftNode = root.left;
        Point2D left = leftNode != null ? leftNode.point : null;
        Node rightNode = root.right;
        Point2D right = rightNode != null ? rightNode.point : null;
        StdOut.println(String.format("Root: %s [%s <-> %s]", parent, left, right));

        drawSubTree(leftNode);
        drawSubTree(rightNode);
    }

    private void drawSubTree(Node node) {
        if (node == null) return;

        Point2D parent = node.point;
        Node leftNode = node.left;
        Point2D left = leftNode != null ? leftNode.point : null;
        Node rightNode = node.right;
        Point2D right = rightNode != null ? rightNode.point : null;
        StdOut.println(String.format("Sub: %s [%s <-> %s]", parent, left, right));

        drawSubTree(leftNode);
        drawSubTree(rightNode);
    }
}
