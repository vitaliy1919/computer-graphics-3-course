package sample;



import java.util.ArrayList;

import static java.lang.Math.signum;

public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    static public double area(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    static public boolean intersection(Point a, Point b, Point c, Point d) {
        return signum(area(a, b, c)) * signum(area(a, b, d)) < 0 &&
                signum(area(c, d, a)) * signum(area(c, d, b)) < 0;
    }

    static public boolean isPointInside(ArrayList<Point> points, Point point, double minX) {
        int intersections = 0;
        Point lineStart = new Point(minX, point.y+0.5);
        for (int i = 0; i < points.size(); i++) {
            Point a = points.get(i);
            Point b;
            if (i == points.size() - 1) {
                b = points.get(0);
            } else
                b = points.get(i + 1);

            if (intersection(a, b, lineStart, point))
                intersections++;
        }
        return intersections % 2 == 1;

    }
}
