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
import sample.Voronoi.Point;
import sample.Voronoi.Voronoi;
import sample.Voronoi.VoronoiEdge;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

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



    @FXML
    public void canvasClick(MouseEvent event) {
        double x = event.getX(), y = event.getY();
        System.out.println("Click: " + x +" " + y);
        Point point = new Point(x, y);

        points.add(point);
        drawPoint(point);
    }

    private void redraw() {
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.setStroke(Color.BLACK);
        for (Point p: points) {
            drawPoint(p);
        }
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
        Voronoi voronoi = new Voronoi(points);

        for (VoronoiEdge e : voronoi.getEdgeList()) {
            if (e.p1 != null && e.p2 != null) {
                double topY = (e.p1.y == Double.POSITIVE_INFINITY) ? -600  : e.p1.y; // HACK to draw from infinity
                context.strokeLine(e.p1.x, topY, e.p2.x, e.p2.y);
            }
        }
//        edges.edgeStream().forEach(edge -> {
//
////            context.strokeLine(edge.getA().getLocation().x, edge.getA().getLocation().y, edge.getB().getLocation().x, edge.getB().getLocation().y);
//            System.out.println(edge);
//        });

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
