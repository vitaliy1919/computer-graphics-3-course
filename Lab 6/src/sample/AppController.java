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
import sample.CircularLinkedList.CircularList;
import sample.CircularLinkedList.CircularListNode;

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

    private Point firstRectanglePoint;
    private ArrayList<Point> points = new ArrayList<>();
    private Rectangle rectangle;
    private boolean rectangleInput = false;
    private boolean rectangleInputEnded;


    public CircularList<Point> jarvisMatch(ArrayList<Point> points, int l, int r) {
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
        CircularList<Point> hull  = new CircularList<>();
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
            hull.insertToEnd(nextPoint);
            firstPoint = secondPoint;
            secondPoint = nextPoint;
        } while (secondPoint != minYPoint);
        return hull;

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
        CircularList<Point> result = divideAndConquer(points, 0, points.size());
        context.setStroke(Color.RED);
        CircularListNode<Point> iter = result.getRoot();
        Point firstP = iter.next.data;
        iter = iter.next;
        Point secondP = firstP;
        Point p1 = null;
       do {
            p1 = iter.next.data;
            iter = iter.next;
            context.strokeLine(secondP.x, secondP.y, p1.x, p1.y);
            secondP = p1;
        } while (iter != result.getRoot());
        context.strokeLine(firstP.x, firstP.y, secondP.x, secondP.y);
        context.setStroke(Color.BLACK);


    }

    private void drawPoint(Point point) {
        double x = point.x;
        double y = point.y;
        context.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
        context.strokeText("("+point.x+","+point.y+")", point.x + 5, point.y+5);

    }
    private CircularList<Point> divideAndConquer(ArrayList<Point> points, int l, int r) {
        if (r - l <= 7) {
            return jarvisMatch(points, l, r);
        }
        int m = l + (r - l) / 2;
        CircularList<Point> leftHull = divideAndConquer(points, l, m);
        CircularList<Point> rightHull = divideAndConquer(points,m, r);
        CircularList<Point> result = mergeHulls(leftHull, rightHull);
        return result;
    }

    private CircularList<Point> mergeHulls(CircularList<Point> leftHull, CircularList<Point> rightHull) {
        if (leftHull.size() < 3) {
            throw new RuntimeException("leftHull size have to be >= 3");
        }
        double innerX = (leftHull.getRoot().data.x + leftHull.getRoot().next.data.x + leftHull.getRoot().next.next.data.x) / 3;
        double innerY = (leftHull.getRoot().data.y + leftHull.getRoot().next.data.y + leftHull.getRoot().next.next.data.y) / 3;
        Point inner = new Point(innerX, innerY);
        //drawPoint(inner);
        CircularListNode<Point> iterLeft = leftHull.getRoot(), iterRight = rightHull.getRoot();
        boolean leftMoved = false, rightMoved = false;
        boolean isInside = true;
        do {
            double area = Point.area(inner, iterRight.data, iterRight.next.data);
            if (area < 0)
                isInside = false;
            iterRight = iterRight.next;
        } while (isInside && iterRight != rightHull.getRoot());
        iterRight = rightHull.getRoot();
        if (!isInside)  {
            double area;
            do {
                area = Point.area(inner, iterRight.data, iterRight.next.data);
                iterRight = iterRight.next;
            }  while (area < 0);
            do {
                area = Point.area(inner, iterRight.data, iterRight.next.data);
                if (area > 0) {
                    CircularListNode<Point> next = iterRight.next;
                    rightHull.remove(iterRight);
                    iterRight = next;
                } else {
                    //iterRight = iterRight.next;
                }
            } while (area > 0);
        }

        if (isInside) {
            CircularListNode<Point> minAngleIter = null;
            double minArea = 0;
            do {
                double area = Point.area(inner, iterLeft.data, iterRight.data);
                if (minAngleIter == null || (area < 0 && minArea < area) ) {
                    minAngleIter = iterRight;
                    minArea = area;
                }
                iterRight = iterRight.next;
            } while (iterRight != rightHull.getRoot());
            iterRight = minAngleIter;
        } else {
            CircularListNode<Point> minAngleIter = null;
            double minArea = 0;
            do {
                double area = Point.area(inner, iterLeft.data, iterRight.data);
                if (area < 0 && (minAngleIter == null || minArea < area)) {
                    minAngleIter = iterLeft;
                    minArea = area;
                }
                iterLeft = iterLeft.next;
            } while (iterLeft != leftHull.getRoot());
            iterLeft = minAngleIter;
        }
        CircularList<Point> resultHull = new CircularList<>();
        CircularListNode<Point> startLeft = iterLeft, startRight = iterRight;
        while (!(leftMoved && iterLeft == startLeft) &&
               !(rightMoved && iterRight == startRight)) {
            double area = Point.area(inner, iterLeft.data, iterRight.data);
            if (area > 0) {
                rightMoved = true;
                resultHull.insertToEnd(iterRight.data);
                iterRight = iterRight.next;
            } else {
                leftMoved = true;
                resultHull.insertToEnd(iterLeft.data);
                iterLeft = iterLeft.next;
            }
        }
        while (!(leftMoved && iterLeft == startLeft)) {
            leftMoved =true;
            resultHull.insertToEnd(iterLeft.data);
            iterLeft = iterLeft.next;
        }


        while (!(rightMoved && iterRight == startRight)) {
            rightMoved = true;
            resultHull.insertToEnd(iterRight.data);
            iterRight = iterRight.next;
        }
//        if (5==5)
//            return resultHull;
        CircularListNode<Point> iter = resultHull.getRoot();
        CircularListNode<Point> minYIter = iter;
        do {
            if (iter.data.y < minYIter.data.y)
                minYIter = iter;
            iter = iter.next;
        } while (iter != resultHull.getRoot());
        iter = minYIter;
        boolean flag = true;
        do {
            double area = Point.area(iter.data, iter.next.data, iter.next.next.data);
            if (iter.next == minYIter.prev || iter.next == minYIter)
                flag = false;
            if (area > 0) {
                resultHull.remove(iter.next);
                iter = iter.prev;
            } else
                iter = iter.next;
        } while (flag || iter.next != minYIter);
        return resultHull;
    }

    private void drawPoint(Point point, int numb) {
       drawPoint(point);
       context.setStroke(Color.WHITE);
       context.strokeText(Integer.toString(numb), point.x -5, point.y+5);
       context.setStroke(Color.GREEN);
       context.strokeText("("+point.x+","+point.y+")", point.x + 5, point.y+5);
    }
}
