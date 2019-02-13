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


import java.io.*;
import java.net.URL;
import java.util.ArrayList;
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

    public AppController(Stage stage) {
        this.stage = stage;
    }
    double diameter = 4;
    Stage stage;
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
        drawPoint();
        poligonInputEnded = true;
        if (Point.isPointInside(points, point, minX))
            label.setText("Точка всередині");
        else
            label.setText("Точка ззовні");
    }

    @FXML
    public void canvasClick(MouseEvent event) {
        double x = event.getX(), y = event.getY();
        System.out.println("Click: " + x +" " + y);


        if (poligonInputEnded) {

            point = new Point(x, y);
            drawPoint();
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

            drawPolygon();

            label.setText("Оберіть точку");

            return;
        }

        context.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
        points.add(new Point(x, y));
        if (minX > x)
            minX = x;
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

    void drawPoint() {
        context.setFill(Color.RED);
        double x = point.x;
        double y = point.y;
        context.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
    }
}
