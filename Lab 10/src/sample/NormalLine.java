package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class NormalLine {
    public double k, b;

    public NormalLine(double k, double b) {
        this.k = k;
        this.b = b;
    }

    public NormalLine() {
    }

    public static NormalLine bisector(Edge a) {
        double k = (a.end.y - a.start.y) / (a.end.x - a.start.x);
        NormalLine line = new NormalLine();
        line.k = -1/k;
        double midX = (a.end.x + a.start.x) / 2, midY = (a.end.y + a.start.y) / 2;
        line.b = midY - line.k * midX;
        return line;
    }

    public double getX(double y) {
        return (y - b) / k;
    }

    public double getY(double x) {
        return x*k+b;
    }

    public void drawLine(GraphicsContext context) {
        Paint paint = context.getStroke();
        context.setStroke(Color.RED);
        context.strokeLine(0, getY(0), 1000, getY(1000));
        context.setStroke(paint);
    }

    public Point intersect(NormalLine line) {
        double x = (line.b - b) / (k - line.k);
        double y = k * x + b;
        return new Point(x, y);
    }
}
