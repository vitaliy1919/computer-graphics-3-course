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
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Math.sqrt;

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
    private int kApprox = 5;
    private Point firstRectanglePoint;
    private ArrayList<Point> points = new ArrayList<>();
    private Rectangle rectangle;
    private boolean rectangleInput = false;
    private boolean rectangleInputEnded;


    public ArrayList<Point> jarvisMatch(ArrayList<Point> points, int l, int r) {
//        if (points.size() <= 3) {
//            CircularList<Point> result = new CircularList<>();
//            for (Point point:points)
//                result.insertToEnd(point);
//            return result;
//        }
        if (l >= r) {
            return null;
        }
        Point minYPoint = points.get(l);
        for (int i = l; i < r; i++) {
            Point point = points.get(i);
            if (point.y < minYPoint.y)
                minYPoint = point;
        }

        //ArrayList<Point> hull = new ArrayList<>();
        //hull.add(minYPoint);
        ArrayList<Point> hull  = new ArrayList<>();
        Point firstPoint = new Point(minYPoint.x + 0.1,minYPoint.y ), secondPoint = minYPoint ;
        do {
            Point curVector = new Point(secondPoint.x - firstPoint.x, secondPoint.y - firstPoint.y);
            double vectorLength = sqrt(curVector.x*curVector.x + curVector.y*curVector.y);
            Point nextPoint = null;
            double maxCos = -2;
            for (int i = l; i < r; i++) {
                Point point = points.get(i);
                if (point == firstPoint || point == secondPoint)
                    continue;
                double diffY = secondPoint.y - point.y, diffX = secondPoint.x - point.x;
                double length = sqrt(diffX*diffX + diffY*diffY);
                double curCos = ((-diffX) * curVector.x + (-diffY)*curVector.y) / (vectorLength*length);
                if (curCos > maxCos) {
                    nextPoint = point;
                    maxCos = curCos;
                }
            }
            hull.add(nextPoint);
            firstPoint = secondPoint;
            secondPoint = nextPoint;
        } while (secondPoint != minYPoint);
        return hull;

    }


    ArrayList<Point> hullApprox(ArrayList<Point> points,int k) {
        double minX = stage.getWidth() + 1, maxX = -1;
        Point minPoints[] = new Point[k];
        Point maxPoints[] = new Point[k];

        for (Point point: points) {
            if (point.x > maxX)
                maxX = point.x;
            if (point.x < minX)
                minX = point.x;
        }
        maxX += 5;
        double step = (maxX - minX) / k;
        for (int i = 0; i <= k; i++) {
            context.strokeLine(minX + i*step, 0, minX + i*step, stage.getHeight());
        }
        for (Point point: points) {
            for (int i = 0; i < k; i++) {
                if ((point.x > minX + i*step || Point.equal(point.x, minX + i*step)) &&
                    point.x < minX + (i+1)*step) {
                    if (maxPoints[i] == null || maxPoints[i].y < point.y)
                        maxPoints[i] = point;

                    if (minPoints[i] == null || minPoints[i].y > point.y)
                        minPoints[i] = point;
                }
            }
        }
        ArrayList<Point> approx = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            if (minPoints[i] != null)
                approx.add(minPoints[i]);
            if (maxPoints[i] != null && minPoints[i] != maxPoints[i])
                approx.add(maxPoints[i]);
        }

        return jarvisMatch(approx, 0, approx.size());
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
            kApprox = Integer.parseInt(text);
//            String[] args = text.split(",");
//            Point a = new Point(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
//            Point b = new Point(Double.parseDouble(args[2]), Double.parseDouble(args[3]));
//            rectangle = new Rectangle(a, b);
//            rectangleInputEnded = true;
            nextButtonClick(null);
        }
    }
    @FXML
    public void nextButtonClick(MouseEvent event) {
        System.out.println("Next button click");
        redraw();
        ArrayList<Point> result = hullApprox(points, kApprox);
        context.setStroke(Color.RED);
        for (int i = 0; i < result.size(); i++) {
            if ( i == result.size() - 1) {
                context.strokeLine(result.get(i).x, result.get(i).y, result.get(0).x, result.get(0).y);
            } else
                context.strokeLine(result.get(i).x, result.get(i).y, result.get(i+1).x, result.get(i+1).y);


        }
//        CircularListNode<Point> iter = result.getRoot();
//        Point firstP = iter.next.data;
//        iter = iter.next;
//        Point secondP = firstP;
//        Point p1 = null;
//       do {
//            p1 = iter.next.data;
//            iter = iter.next;
//            context.strokeLine(secondP.x, secondP.y, p1.x, p1.y);
//            secondP = p1;
//        } while (iter != result.getRoot());
      //  context.strokeLine(firstP.x, firstP.y, secondP.x, secondP.y);
        context.setStroke(Color.BLACK);


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
