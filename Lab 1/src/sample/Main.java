package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.Vector;

import static java.lang.Math.abs;

public class Main extends Application {
    ArrayList<Point> points = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("sample.fxml"));
        loader.setController(new AppController());
        BorderPane pane = (BorderPane)loader.load();

        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
//        FXMLLoader loader1 = new FXMLLoader();
//
//        loader1.setLocation(Main.class.getResource("sample.fxml"));
//        pane.setCenter((AnchorPane)loader1.load());

//        Parent root1 = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        Group root = new Group();
//        Canvas canvas = new Canvas(600, 600);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
//                new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent t) {
//                        double x = t.getSceneX(), y = t.getSceneY();
//                        gc.setFill(Color.GREEN);
//                        gc.setStroke(Color.GREEN);
//
//                        if (points.size() > 0 && abs(points.get(0).x - x) < 5 && abs(points.get(0).y - y) < 5 ) {
//                            gc.setFill(Color.TRANSPARENT);
//                            gc.setStroke(Color.GREEN);
//
//                            double[] x_ = new double[points.size()];
//                            double[] y_ = new double[points.size()];
//                            int i = 0;
//                            for (Point p: points) {
//                                x_[i] = p.x;
//                                y_[i] = p.y;
//                                i++;
//                            }
//                            gc.strokePolygon(x_, y_, points.size());
//                            return;
//                        }
//                        System.out.println("Click: " + t.getSceneX() + " " + t.getSceneY());
//                        gc.fillOval( x - 5 /2, y - 5/2,5,5);
//                        points.add(new Point(t.getSceneX(), t.getSceneY()));
//                       // t.getSceneX()
//                        if (t.getClickCount() >1) {
//
//                        }
//                    }
//                });
//        //drawShapes(gc);
//        root.getChildren().add(canvas);
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
