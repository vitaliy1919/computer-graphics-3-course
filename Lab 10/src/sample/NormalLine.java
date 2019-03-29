package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.omg.PortableServer.POA;

public class NormalLine {
    public double a, b, c;

    public NormalLine(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public NormalLine() {
    }

    public static NormalLine bisector(Edge a) {
        double midX = (a.end.x + a.start.x) / 2, midY = (a.end.y + a.start.y) / 2;
        boolean verticalLine = false;
        if (Point.equal(a.end.x - a.start.x, 0))
            verticalLine = true;
        double k = (a.end.y - a.start.y) / (a.end.x - a.start.x);
        NormalLine line = new NormalLine();
        if (Point.equal(k, 0)) {
            line.a = 1;
            line.b = 0;
            line.c = -midX;
        } else {
            double newK;
            if (verticalLine)
                newK = 0;
            else
                newK = -1 / k;
            line.a = -newK;
            line.b = 1;
            line.c = -(midY - newK * midX);
        }
        return line;
    }

    public double getX(double y) {
        if (Point.equal(a, 0))
            return 0;
//            throw new RuntimeException("Line.getX: a is 0");
        return -(c+b*y)/ a;
    }

    public double getY(double x) {
        if (Point.equal(b, 0))
            return 0;
//            throw new RuntimeException("Line.getX: b is 0");
        return - (a*x+c)/ b;
    }

    public void drawLine(GraphicsContext context) {
        Paint paint = context.getStroke();
        context.setStroke(Color.RED);
        context.strokeLine(0, getY(0), 1000, getY(1000));
        context.setStroke(paint);
    }

    public Point intersect(NormalLine line) {
        if (Point.equal(b, 0) && Point.equal(line.b, 0))
            throw new RuntimeException("intersect: both b are 0");
        double x, y;
        if (Point.equal(b, 0)) {
            x = -c / a;
            y = line.getY(x);
        } else if (Point.equal(line.b, 0)) {
            x = - line.c / line.a;
            y = getY(x);
        } else {
            if (Point.equal(line.b*a - line.a*b, 0)) {
                throw new RuntimeException("intersect: Cant find intersect: dividing by 0");
            }
            x = (line.c*b - c*line.b) / (line.b*a - line.a*b);
            y = getY(x);
        }

        return new Point(x, y);
    }
}
