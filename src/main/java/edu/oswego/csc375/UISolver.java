package edu.oswego.csc375;

import edu.oswego.csc375.Puzzle.GameState;
import edu.oswego.csc375.Puzzle.GameStateFactory;
import edu.oswego.csc375.Puzzle.Puzzle;
import edu.oswego.csc375.Puzzle.Solution;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UISolver extends Application {

    private static final double boxOuterSize = 50;
    private static final double boxInnerSize = 48;

    private static GridPane controlsContainer = new GridPane();
    private static HBox root = new HBox();
    private static Group tilesGroup = new Group();
    private static final int GAMESIZE = 4;

    @Override
    public void start(Stage primaryStage) throws Exception {

        controlsContainer.setHgap(5);
        controlsContainer.setVgap(5);
        root.setSpacing(0);
        controlsContainer.setPadding(new Insets(10, 10, 10, 10));
        double sceneWidth = 640;
        double sceneHeight = 198;
        Scene applicationScene = new Scene(root, sceneWidth, sceneHeight, Color.WHITE);

        GameStateFactory gsf = new GameStateFactory(GAMESIZE);
        final GameState[] state = {gsf.getRandomState()};

        primaryStage.setScene(applicationScene);
        int[] tiles = state[0].getTiles();
        for (int i = 0; i < GAMESIZE; i++) {
            for (int j = 0; j < GAMESIZE; ++j) {
                Rectangle r = new Rectangle();
                Label lbl = new Label();
                if (tiles[(i * GAMESIZE) + j] == GAMESIZE * GAMESIZE) {
                    r.setFill(Color.WHITE);
                    lbl.setText("");
                } else {
                    r.setFill(Color.GRAY);
                    lbl.setText(tiles[(i * GAMESIZE) + j] + "");
                }
                r.setX(j * boxOuterSize);
                lbl.setLayoutX(r.getX() + lbl.getWidth() / 2);
                r.setY(i * boxOuterSize);
                lbl.setLayoutY(r.getY() + lbl.getHeight() / 2);
                r.setWidth(boxInnerSize);
                r.setHeight(boxInnerSize);
                tilesGroup.getChildren().add(r);
                tilesGroup.getChildren().add(lbl);

            }
        }


        Label lbl_parallelism = new Label("Parallelism: ");
        TextField tf_parallelism = new TextField();
        Label lbl_depth = new Label("Maximum depth: ");
        Label lbl_depth_val = new Label("8");
        Slider slider_depth = new Slider(1, 32, 16);
        Button button_solve = new Button("Solve");
        Button button_generate = new Button("Make New Board");
        Text text = new Text();
        slider_depth.setShowTickLabels(true);
        slider_depth.setShowTickMarks(true);
        slider_depth.setMajorTickUnit(8);
        slider_depth.setMinorTickCount(7);
        slider_depth.setSnapToTicks(true);
        slider_depth.setBlockIncrement(4);
        slider_depth.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                lbl_depth_val.setText(String.format("%.0f", newValue));
            }
        });
        controlsContainer.add(lbl_parallelism, 0, 0);
        controlsContainer.add(tf_parallelism, 1, 0);
        controlsContainer.add(lbl_depth, 0, 1);
        controlsContainer.add(slider_depth, 1, 2, 3, 1);
        controlsContainer.add(lbl_depth_val, 1, 1);
        controlsContainer.add(button_solve, 0, 3);
        controlsContainer.add(button_generate, 1, 3);
        controlsContainer.add(text, 0, 4, 3, 3);
        root.getChildren().add(tilesGroup);
        root.getChildren().add(controlsContainer);

        Thread[] th = new Thread[1];

        button_solve.setOnAction(event -> {
            th[0] = new Thread(() -> {
                Puzzle puzzle16 = new Puzzle(Integer.parseInt(lbl_depth_val.getText()),
                        Integer.parseInt(tf_parallelism.getCharacters().toString()), GAMESIZE, state[0]);

                ExecutorService exec = Executors.newSingleThreadExecutor();
                Future<Solution> futureSolution = exec.submit(puzzle16::solve);
                int i = 0;
                while (!futureSolution.isDone()) {

                    String bar = "";
                    switch (i) {
                        case 0:
                            bar = "-";
                            ++i;
                            break;
                        case 1:
                            bar = "\\";
                            ++i;
                            break;
                        case 2:
                            bar = "|";
                            ++i;
                            break;
                        case 3:
                            bar = "/";
                            i = 0;
                            break;
                    }
                    String finalBar = bar;
                    Platform.runLater(() -> {
                        text.setText("Solving..." + finalBar);
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                exec.shutdown();
                Solution actual = null;
                try {
                    actual = futureSolution.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                int moves = actual.getMoves();
                state[0] = actual.getLastState();
                Platform.runLater(() -> {
                    text.setText("Solved!\n" + "Total of " + moves + " moves");
                });

                for (GameState s : actual.getStates()) {
                    int[] t2 = s.getTiles();
                    if (th[0].isInterrupted()){
                        break;
                    }
                    tilesGroup.getChildren().removeAll();
                    for (int k = 0; k < GAMESIZE; k++) {
                        for (int j = 0; j < GAMESIZE; ++j) {
                            Rectangle r = new Rectangle();
                            Label lbl = new Label();
                            if (t2[(k * GAMESIZE) + j] == GAMESIZE * GAMESIZE) {
                                r.setFill(Color.WHITE);
                                lbl.setText("");
                            } else {
                                r.setFill(Color.GRAY);
                                lbl.setText(t2[(k * GAMESIZE) + j] + "");
                            }
                            r.setX(j * boxOuterSize);
                            lbl.setLayoutX(r.getX() + lbl.getWidth() / 2);
                            r.setY(k * boxOuterSize);
                            lbl.setLayoutY(r.getY() + lbl.getHeight() / 2);
                            r.setWidth(boxInnerSize);
                            r.setHeight(boxInnerSize);
                            Platform.runLater(() -> {
                                tilesGroup.getChildren().add(r);
                                tilesGroup.getChildren().add(lbl);
                            });
                        }
                    }


                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            });
            th[0].setDaemon(true);
            th[0].start();
        });

        button_generate.setOnAction(event -> {
            state[0] = gsf.getRandomState();
            if (th[0] != null) {
                th[0].interrupt();

            }
            Platform.runLater(() -> {
                text.setText("");
                int[] t = state[0].getTiles();
                tilesGroup.getChildren().removeAll();
                for (int i = 0; i < GAMESIZE; i++) {
                    for (int j = 0; j < GAMESIZE; ++j) {
                        Rectangle r = new Rectangle();
                        Label lbl = new Label();
                        if (t[(i * GAMESIZE) + j] == GAMESIZE * GAMESIZE) {
                            r.setFill(Color.WHITE);
                            lbl.setText("");
                        } else {
                            r.setFill(Color.GRAY);
                            lbl.setText(t[(i * GAMESIZE) + j] + "");
                        }
                        r.setX(j * boxOuterSize);
                        lbl.setLayoutX(r.getX() + lbl.getWidth() / 2);
                        r.setY(i * boxOuterSize);
                        lbl.setLayoutY(r.getY() + lbl.getHeight() / 2);
                        r.setWidth(boxInnerSize);
                        r.setHeight(boxInnerSize);
                        tilesGroup.getChildren().add(r);
                        tilesGroup.getChildren().add(lbl);

                    }
                }
            });
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
