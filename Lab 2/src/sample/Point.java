package sample;



import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.signum;


public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    static boolean intersect_1(double a, double b, double c, double d) {
        if (a > b) {
            double temp = a;
            a = b;
            b = temp;
        }
        if (c > d)  {
            double temp = c;
            c = d;
            d = temp;
        }
        return max(a,c) <= min(b,d);
    }
    static public double area(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    static public boolean intersection(Point a, Point b, Point c, Point d) {
        return intersect_1 (a.x, b.x, c.x, d.x)
                && intersect_1 (a.y, b.y, c.y, d.y) && signum(area(a, b, c)) * signum(area(a, b, d)) < 0 &&
                signum(area(c, d, a)) * signum(area(c, d, b)) < 0;
    }
    static public boolean checkPointInsideSegment(Point s1, Point s2, Point point) {
        Point v1 = new Point(point.x - s1.x, point.y - s1.y);
        Point v2 = new Point(s2.x - point.x, s2.y - point.y);
        return Double.compare(v1.x*v2.y - v2.x*v1.y, 0) == 0;

    }
    static public boolean isPointInside(ArrayList<Point> points, Point point, double minX) {
        int intersections = 0;
        Point lineStart = new Point(minX, point.y+0.5);
        for (int i = 0; i < points.size(); i++) {
            Point a = points.get(i);
            Point b;
            if (Double.compare(point.x, a.x) == 0 && Double.compare(point.y, a.y) == 0)
                return true;
            if (i == points.size() - 1) {
                b = points.get(0);
            } else
                b = points.get(i + 1);
            if (checkPointInsideSegment(a, b, point))
                return true;

            if (intersection(a, b, lineStart, point))
                intersections++;
        }
        return intersections % 2 == 1;

    }
}
