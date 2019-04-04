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

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import static java.lang.Math.*;

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
    private CircularList<Point> hull = new CircularList<>();

    private ArrayList<Point> points = new ArrayList<>();


    CircularListNode<Point> leftNode;
    CircularListNode<Point> rightNode;


    void updateHull(CircularList<Point> hull, Point point) {
        CircularListNode<Point> curPoint = null;
        if (hull.size() >= 3) {
            double distLeft = abs(point.x - leftNode.data.x);
            double distRight = abs(point.x - rightNode.data.x);

            CircularListNode<Point> startPoint;
            if (distLeft > distRight)
                startPoint = rightNode;
            else
                startPoint = leftNode;

            CircularListNode<Point> topPoint = startPoint, bottomPoint = startPoint;
            boolean topFound = false, bottomFound = false;
            boolean inside = false;
            double rightTopSign = distLeft > distRight ? 1 : -1;
            double rightBottomSign = distLeft > distRight ? -1 : 1;
            boolean firstBottomTime = true;
            boolean firstTopTime = true;
            int steps = 0;
            do {
                steps ++;
                double a = signum(Point.area(point, topPoint.data, topPoint.next.data));
                double b = signum(Point.area(point, topPoint.data, topPoint.prev.data));

                if ((!firstTopTime && topPoint == startPoint) || (!firstBottomTime && bottomPoint == startPoint))
                    inside = true;
                if (!topFound && ( a != b || b <= 0)) {
                    topPoint = topPoint.next;
                    firstTopTime = false;
                } else
                    topFound = true;
                a = signum(Point.area(point, bottomPoint.data, bottomPoint.next.data));
                b = signum(Point.area(point, bottomPoint.data, bottomPoint.prev.data));
                if (!bottomFound && (a != b || a >= 0)) {
                    firstBottomTime = false;
                    bottomPoint = bottomPoint.prev;
                } else
                    bottomFound = true;
            } while (steps < 5000 && !inside && (!topFound || !bottomFound));
            if (inside)
                return;
            if (steps >= 5000) {
                System.out.println("Something went wrong, reverting");
                return;
            }
            hull.splitNext(bottomPoint, topPoint);
            curPoint = hull.insertAfter(bottomPoint, point);

            context.setStroke(Color.RED);
            drawPoint(topPoint.data);
            drawPoint(bottomPoint.data);
            context.setStroke(Color.BLACK);

        } else if (hull.size() < 3)
            curPoint = hull.insertToEnd(point);
        if (leftNode == null || leftNode.data.x > point.x)
            leftNode = curPoint;
        if (rightNode == null || rightNode.data.x < point.x)
            rightNode = curPoint;

    }

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
        label.setText("Введіть точки (натисність Готово, щоб продовжити)");
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    public void onOpenFile(ActionEvent e) {

        System.out.println("Load");
        points.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("int.txt"));
            String s;
            hull = new CircularList<>();
            while ((s = reader.readLine()) != null) {
                Scanner scan = new Scanner(s).useLocale(Locale.US);;
                double x = scan.nextDouble();
                double y = scan.nextDouble();
                points.add(new Point(x, y));
                updateHull(hull, new Point(x, y));
            }
            reader.close();
            redraw();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @FXML
    public void onSaveAction(ActionEvent e) {

        System.out.println("Save");
        try {
            FileWriter writer = new FileWriter("int.txt");
            BufferedWriter fileWriter = new BufferedWriter(writer);
            for (Point point: points) {
                fileWriter.write(Double.toString(point.x));
                fileWriter.write(" ");
                fileWriter.write(Double.toString(point.y));
                fileWriter.newLine();
            }
            fileWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }


    @FXML
    public void canvasClick(MouseEvent event) {
        redraw();
        double x = event.getX(), y = event.getY();
        System.out.println("Click: " + x +" " + y);
        Point point = new Point(x, y);

        points.add(point);
      //  CircularList<Point> hull = new CircularList<>();
        updateHull(hull, point);
        redraw();
        drawPoint(point);
    }

    private void redraw() {
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.setStroke(Color.BLACK);
        for (Point p: points) {
            drawPoint(p);
        }
        context.setStroke(Color.RED);

        CircularListNode<Point> iter = hull.getRoot();
        if (iter == null)
            return;
        if (iter.next == null)
            return;
        Point firstP = iter.next.data;
        iter = iter.next;
        Point secondP = firstP;
        Point p1 = null;
        do {
            p1 = iter.next.data;
            iter = iter.next;
            context.strokeLine(secondP.x, secondP.y, p1.x, p1.y);
            secondP = p1;
        } while (iter != hull.getRoot());
        context.strokeLine(firstP.x, firstP.y, secondP.x, secondP.y);
        context.setStroke(Color.BLACK);
    }

    @FXML
    public void onTextEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String text = input.getText();
            nextButtonClick(null);
        }
    }
    @FXML
    public void nextButtonClick(MouseEvent event) {
        System.out.println("Next button click");
        redraw();

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
