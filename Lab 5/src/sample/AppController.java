package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

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

    @FXML
    TextField input;

    @FXML
    Button nextButton;
    private Stage stage;
    private GraphicsContext context;
    private double diameter = 6;

    private Point firstRectanglePoint;
    private ArrayList<Point> points = new ArrayList<>();
    private Rectangle rectangle;
    private boolean rectangleInput = false;
    private boolean rectangleInputEnded;


    private List<Point> quickHull(List<Point> a, Point l, Point r) {
        if (a.get(0).equals(l) && a.get(1).equals(r)
        || a.get(1).equals(l) && a.get(0).equals(r)) {
            List<Point> list = new LinkedList<>();
            list.add(l);
            list.add(r);
            return list;
        }
        Point maxPoint = a.get(0);
        double maxArea = -1;
        for (Point point: a) {
            double curArea = abs(Point.area(point, l, r));
            if (curArea > maxArea) {
                maxPoint = point;
                maxArea = curArea;
            } else if (Point.equal(curArea, maxArea) && maxPoint.x > point.x) {
                maxPoint = point;
            }
        }

        List<Point> list1 = new LinkedList<>();
        List<Point> list2 = new LinkedList<>();
        for (Point point: a) {
            double sum1 = Point.area(l, maxPoint, point);
            double sum2 = Point.area(maxPoint, r, point);
            if (sum1 > 0 || Point.equal(sum1, 0)) {
                list1.add(point);
            }
            if (sum2 > 0 || Point.equal(sum2, 0)) {
                list2.add(point);
            }
        }
        List<Point> res1 = quickHull(list1, l, maxPoint);
        List<Point> res2 = quickHull(list2, maxPoint, r);
        if (res1 == null || res2 == null || res2.isEmpty()) {
            System.out.println("Error!");
            return null;
        }
        res2.remove(0);
        res1.addAll(res2);

        return res1;
    }

    public AppController(Stage stage) {
        this.stage = stage;
    }

    int firstPointChosen = -1;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        context = canvas.getGraphicsContext2D();
       onReset(null);
    }


    @FXML
    public void onReset(ActionEvent e) {
        System.out.println("Action");
        context.setFill(Color.BLACK );
        rectangleInput = false;
        label.setText("Введіть точки (натисність Готово, щоб продовжити)");
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    public void onOpenFile(ActionEvent e) {

        System.out.println("Load");

    }


    @FXML
    public void canvasClick(MouseEvent event) {
        double x = event.getX(), y = event.getY();
        System.out.println("Click: " + x +" " + y);
        Point point = new Point(x, y);
        if (rectangleInput) {
            if (firstRectanglePoint == null) {
                firstRectanglePoint = point;
            } else {
                rectangle = new Rectangle(firstRectanglePoint, point);
                firstRectanglePoint = null;
                rectangleInputEnded = true;
                redraw();
            }
            return;
        }
        points.add(point);
        drawPoint(point);
    }

    private void redraw() {
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.setStroke(Color.BLACK);
        for (Point p: points) {
            drawPoint(p);
        }
        context.setStroke(Color.ROYALBLUE);
        if (rectangle != null)
            rectangle.draw(context);
    }

    @FXML
    public void onTextEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String text = input.getText();
            String[] args = text.split(",");
            Point a = new Point(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
            Point b = new Point(Double.parseDouble(args[2]), Double.parseDouble(args[3]));
            rectangle = new Rectangle(a, b);
            rectangleInputEnded = true;
            nextButtonClick(null);
        }
    }
    @FXML
    public void nextButtonClick(MouseEvent event) {
        System.out.println("Next button click");
        redraw();
        Point minX = points.get(0);
        for (Point point: points) {
            if (point.getX() < minX.getX())
                minX = point;
        }
        Point point = new Point(minX.x, minX.y -0.01);
        points.add(point);
        List<Point> a = quickHull(points, minX, point);
        a.remove(point);

        points.remove(point);
        context.setStroke(Color.RED);
        Iterator<Point> iter = a.iterator();
        Point firstP = iter.next();
        Point secondP = firstP;
        Point p1 = null;
        while (iter.hasNext()) {
             p1 = iter.next();
            context.strokeLine(secondP.x, secondP.y, p1.x, p1.y);
            secondP = p1;
        }
        context.strokeLine(firstP.x, firstP.y, secondP.x, secondP.y);
    }

    private void drawPoint(Point point) {
        double x = point.x;
        double y = point.y;
        context.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
        context.strokeText("("+point.x+","+point.y+")", point.x + 5, point.y+5);

    }

    private void drawPoint(Point point, int numb) {
       drawPoint(point);
       context.setStroke(Color.WHITE);
       context.strokeText(Integer.toString(numb), point.x -5, point.y+5);
       context.setStroke(Color.GREEN);
       context.strokeText("("+point.x+","+point.y+")", point.x + 5, point.y+5);
    }
}
