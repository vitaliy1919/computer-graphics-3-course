package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

public class AppController implements Initializable {
    @FXML
    Canvas canvas;

    @FXML
    MenuItem saveAction;

    @FXML
    MenuItem loadAction;

    @FXML
    Label label;

    private GraphicsContext context;
    private boolean poligonInputEnded = false;
    private double minX = Integer.MAX_VALUE;
    ArrayList<Point> points = new ArrayList<>();
    Point point;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        context = canvas.getGraphicsContext2D();
        context.setFill(Color.GREEN );
        label.setText("Введіть багатокутник (натисніть на першу вершину, щоб закінчити вершину)");
    }


    @FXML
    public void onReset(ActionEvent e) {
        System.out.println("Action");
        points.clear();
        point = null;
        minX = Integer.MAX_VALUE;
        label.setText("Введіть багатокутник (натисніть на першу вершину, щоб закінчити вершину)");
        context.setFill(Color.GREEN );
        poligonInputEnded = false;
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    public void canvasClick(MouseEvent event) {
        double x = event.getX(), y = event.getY();
        System.out.println("Click: " + x +" " + y);

        double diameter = 4;
        if (poligonInputEnded) {
            context.setFill(Color.RED);
            point = new Point(x, y);
            context.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
            if (Point.isPointInside(points, point, minX))
                label.setText("Точка всередині");
            else
                label.setText("Точка ззовні");
            return;
        }

        if (points.size() > 0 &&
            abs(points.get(0).x - x) < diameter &&
            abs(points.get(0).y - y) < diameter) {
            poligonInputEnded = true;
            double[] x_ = new double[points.size()];
            double[] y_ = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                x_[i] = points.get(i).x;
                y_[i] = points.get(i).y;
            }
            context.strokePolygon(x_, y_, x_.length);
            label.setText("Обреріть точку");

            return;
        }

        context.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
        points.add(new Point(x, y));
        if (minX > x)
            minX = x;
    }


}
