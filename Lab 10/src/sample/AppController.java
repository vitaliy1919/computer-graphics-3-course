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
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;

import static java.lang.Math.min;
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

    boolean debug = false;
    public AppController(Stage stage) {
        this.stage = stage;
    }

    int firstPointChosen = -1;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        context = canvas.getGraphicsContext2D();
        context.setFont(new Font(null,10));
       onReset(null);
    }


    public Edge hullEdge(ArrayList<Point> points, int l, int r) {
        if (l >= r) {
            return null;
        }
        Point minXPoint = points.get(l);
        for (int i = l; i < r; i++) {
            Point point = points.get(i);
            if (point.x < minXPoint.x)
                minXPoint = point;
        }

        Point firstPoint = new Point(minXPoint.x + 0.1,minXPoint.y ), secondPoint = minXPoint ;
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
        Edge edge = new Edge(minXPoint, nextPoint);
        return edge;

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
        points.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("int.txt"));
            String s;
            while ((s = reader.readLine()) != null) {
                Scanner scan = new Scanner(s).useLocale(Locale.US);;
                double x = scan.nextDouble();
                double y = scan.nextDouble();
                points.add(new Point(x, y));
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

    public ArrayList<Edge> deloneTriangulation(ArrayList<Point> points) {
        Edge startEdge = hullEdge(points, 0, points.size());
        if (startEdge.start.y < startEdge.end.y)
            startEdge = new Edge(startEdge.end, startEdge.start);
        ArrayList<Edge> result = new ArrayList<>();
        TreeSet<Edge> liveEdges = new TreeSet<>((a, b)->{
            int comp = a.start.compareTo(b.start);
            if (comp != 0)
                return comp;
            return a.end.compareTo(b.end);
        });
        liveEdges.add(startEdge);
        NormalLine line = NormalLine.bisector(startEdge);
        result.add(startEdge);

        //line.drawLine(context);
        int steps = 0;
        while (!liveEdges.isEmpty() && steps < 5000) {
            Edge edge = liveEdges.first();
            liveEdges.remove(edge);
            Point point = findPoint(points, edge);
            debug = false;
            if (point != null) {
                addLiveEdge(liveEdges,  point, edge.start);
                addLiveEdge(liveEdges, edge.end,point);
                result.add(new Edge(edge.start, point));
                result.add(new Edge(edge.end, point));

            }
            steps++;
        }
        System.out.println("steps: " + steps);
        return result;
    }

    private void addLiveEdge(TreeSet<Edge> liveEdges, Point a, Point b) {
        Edge edge = new Edge(a, b);
        if (liveEdges.contains(edge))
            liveEdges.remove(edge);
        else
            liveEdges.add(new Edge(b, a));
    }

    private Point findPoint(ArrayList<Point> points, Edge edge) {
        Point curBestPoint = null;
        double curMax = -1;
        NormalLine edgeBisector = NormalLine.bisector(edge);
        for (Point point: points) {
            if (Point.area(edge.start, edge.end, point) > 0) {
                Edge edge1 = new Edge(edge.end, point);
                NormalLine secondBisector = NormalLine.bisector(edge1);

                Point intersection = edgeBisector.intersect(secondBisector);
                Point vector = new Point (intersection.x - edge.start.x, intersection.y - edge.start.y);
                double curRadius = vector.length();
                if (Point.area(edge.start, edge.end, intersection) < 0)
                    curRadius = -curRadius;
                if (curBestPoint == null || curMax > curRadius) {
                    curBestPoint = point;
                    curMax = curRadius;
                }
            }
        }
        return curBestPoint;
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
        ArrayList<Edge> edges = deloneTriangulation(points);
        for (Edge edge: edges) {
            context.strokeLine(edge.start.x, edge.start.y, edge.end.x, edge.end.y);

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
//        context.strokeText("("+point.x+","+point.y+")", point.x + 5, point.y+5);

    }



    private void drawPoint(Point point, int numb) {
       drawPoint(point);
//       context.setStroke(Color.WHITE);
//       context.strokeText(Integer.toString(numb), point.x -5, point.y+5);
//       context.setStroke(Color.GREEN);
//       context.strokeText("("+point.x+","+point.y+")", point.x + 5, point.y+5);
    }
}
