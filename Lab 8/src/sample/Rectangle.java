package sample;

import javafx.scene.canvas.GraphicsContext;

import static java.lang.Math.*;

public class Rectangle {
    public Point p1, p2;

    public Rectangle(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
    public Rectangle(double x1, double y1, double x2, double y2) {
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
    }
    public void normalize() {
        double xMin = min(p1.x, p2.x), xMax = max(p1.x, p2.x);
        double yMin = min(p1.y, p2.y), yMax = max(p1.y, p2.y);
        p1.x = xMin; p1.y = yMin;
        p2.x = xMax; p2.y = yMax;
    }
    public void draw(GraphicsContext context) {
        double x1 = Math.min(p1.x, p2.x);
        double y1 = Math.min(p1.y, p2.y);
        double w = abs(p1.x - p2.x);
        double h = abs(p1.y - p2.y);

        context.strokeRect(x1, y1, w, h);
    }

    static boolean equal(double a, double b) {
        return Double.compare(a, b) == 0;
    }
    public boolean isPointInside(Point point) {
        double xMin = min(p1.x, p2.x), xMax = max(p1.x, p2.x);
        double yMin = min(p1.y, p2.y), yMax = max(p1.y, p2.y);
        if ((point.x > xMin || equal(point.x, xMin)) && (point.x < xMax || equal(point.x, xMax))
                && (point.y > yMin || equal(point.y, yMin)) && (point.y < yMax || equal(point.y, yMax)))
            return true;
        return false;
    }
}
