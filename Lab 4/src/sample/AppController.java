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
import sample.Tree2D.Tree2D;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
    private Tree2D tree;
    private boolean rectangleInputEnded;

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
        if (tree == null) {
            tree = new Tree2D();
            tree.buildTree(points);
            System.out.println(tree);
        }
        if (rectangleInputEnded)  {
            ArrayList<Point> points = tree.findPointsInRectangle(rectangle);
            context.setStroke(Color.RED);
            context.setFill(Color.RED);

            for (Point point: points) {
                drawPoint(point);
            }
        }
        rectangleInput = true;
        label.setText("Оберіть 2 точки для прямокутника (натисніть Визначити, коли закінчите)");
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
