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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.Tree.TreeNode;


import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

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
    private Graph graph = new Graph();
    private boolean pointChosed;

    public AppController(Stage stage) {
        this.stage = stage;
    }
    double diameter = 20;
    Stage stage;
    private GraphicsContext context;
    private boolean graphInputEnded = false;
    private double minX = Integer.MAX_VALUE;
    ArrayList<Point> points = new ArrayList<>();
    Point point;

    int firstPointChosen = -1;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        context = canvas.getGraphicsContext2D();
        context.setFill(Color.BLACK );
        label.setText("Введіть багатокутник (натисніть на першу вершину, щоб закінчити вершину)");
    }


    @FXML
    public void onReset(ActionEvent e) {
        System.out.println("Action");
        points.clear();
        point = null;
        minX = Integer.MAX_VALUE;
        label.setText("Введіть багатокутник (натисніть на першу вершину, щоб закінчити вершину)");
        //context.setFill(Color.GREEN );
        graphInputEnded = false;
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    public void onOpenFile(ActionEvent e) {

        System.out.println("Load");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null)
            return;
        try {
            onReset(e);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String s = "";
            while ((s = reader.readLine()) != null) {
                Scanner scan = new Scanner(s);
                int x = scan.nextInt(), y;
                if (x > 0) {
                    y = scan.nextInt();
                    points.add(new Point(x, y));
                } else {
                    x = scan.nextInt();
                    y = scan.nextInt();
                    point = new Point(x, y);
                }

            }
            reader.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
        }
        drawPolygon();
        drawPoint(point);
        graphInputEnded = true;
        if (Point.isPointInside(points, point, minX))
            label.setText("Точка всередині");
        else
            label.setText("Точка ззовні");
    }

    private int findPoint(double x, double y, double r) {
        for (int i = 0; i < graph.points.size(); i++) {
            if (abs(graph.points.get(i).position.x - x) <= r &&
                    abs(graph.points.get(i).position.y - y) <= r) {
                return i;
            }
        }
        return -1;
    }
    @FXML
    public void labelOnClick() {
        System.out.println("Label on click");
        graphInputEnded = true;
        if (pointChosed) {
            context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            drawGraph();
            TreeNode gSplit = graph.split();

        }
    }

    void drawGraph() {
        context.setStroke(Color.BLACK);
        for (GraphNode point:graph.points) {
            for (Edge edge: point.outEdges) {
                context.strokeLine(edge.from.position.x, edge.from.position.y, edge.to.position.x, edge.to.position.y);
            }
        }
        int i = 0;
        for (GraphNode point:graph.points) {
            drawPoint(point.position, i + 1);
            i++;
        }
        context.setFill(Color.RED);
        drawPoint(point);
        context.setFill(Color.BLACK);
    }
    @FXML
    public void canvasClick(MouseEvent event) {
        double x = event.getX(), y = event.getY();
        System.out.println("Click: " + x +" " + y);
        if (graphInputEnded) {
            pointChosed = true;
            point = new Point(x, y);
            context.setFill(Color.RED);
            drawPoint(point);
            context.setFill(Color.BLACK);
            graphInputEnded = false;
            return;
        }
        if (firstPointChosen != -1) {
            int secondPointIndex = findPoint(x, y, diameter / 2);
            if (secondPointIndex == -1 || secondPointIndex == firstPointChosen) {
                firstPointChosen = -1;
                return;
            }

            graph.addEdge(firstPointChosen, secondPointIndex);
            Point firstPoint = graph.points.get(firstPointChosen).position;
            Point secondPoint = graph.points.get(secondPointIndex).position;

            context.setStroke(Color.BLACK);
            context.strokeLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
            drawPoint(firstPoint, firstPointChosen + 1);
            drawPoint(secondPoint, secondPointIndex + 1);
            drawPoint(secondPoint, secondPointIndex + 1);

            firstPointChosen = -1;
            return;
        }
        firstPointChosen = findPoint(x, y, diameter / 2);
        if (firstPointChosen != -1) {
            Point firstPoint = graph.points.get(firstPointChosen).position;
            //drawPoint(firstPoint);
            return;
        }
        int p = findPoint(x, y, diameter);
        if (p != -1)
            return;
        Point point = new Point(x, y);
        graph.points.add(new GraphNode(point));
        drawPoint(point, graph.points.size());
    }
    void drawPolygon() {
        double[] x_ = new double[points.size()];
        double[] y_ = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            x_[i] = points.get(i).x;
            y_[i] = points.get(i).y;
        }
        context.strokePolygon(x_, y_, x_.length);
    }

    void drawPoint(Point point) {
        double x = point.x;
        double y = point.y;
        context.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
    }

    void drawPoint(Point point, int numb) {
       drawPoint(point);
       context.setStroke(Color.WHITE);
       context.strokeText(Integer   .toString(numb), point.x -5, point.y+5);
    }
}
