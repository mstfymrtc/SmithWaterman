/**
 * Author:    Mustafa Yumurtacı
 * Created:   26.05.2020
 **/
package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main extends Application {
    private final int TileSize = 40;
    BorderPane root;
    ListView listView;
    boolean isRunOnce = false;


    @Override
    public void start(Stage primaryStage) throws Exception {
        String verticalSequence;
        String horizontalSequence;

        var seqS = new File("sequences/seqS.txt");
        var seqT = new File("sequences/seqT.txt");
        if (seqS.exists() && seqT.exists()) {
            verticalSequence = Files.readAllLines(Paths.get("sequences/seqS.txt")).get(1);
            horizontalSequence = Files.readAllLines(Paths.get("sequences/seqT.txt")).get(1);
        } else {
            throw new FileNotFoundException("Both seqS.txt and seqT.txt files must be exists!");
        }


        SmithWaterman localAligner = new SmithWaterman(verticalSequence, horizontalSequence);

        root = new BorderPane();

        VBox leftPane = new VBox(5);
        VBox rightPane = new VBox(15);
        listView = new ListView();
        root.setLeft(leftPane);

        root.setRight(rightPane);

        //left pane

        leftPane.setPadding(new Insets(15, 25, 5, 10));
        leftPane.getChildren().add(new Label("Alignments"));
        leftPane.getChildren().add(listView);

        //right pane

        rightPane.setPadding(new Insets(15, 10, 5, 5));
        rightPane.getChildren().add(new Label("Options"));

        TextField matchTextfield = new TextField();
        matchTextfield.setPromptText("Match Reward");
        matchTextfield.setFocusTraversable(false);

        TextField mismatchTextfield = new TextField();
        mismatchTextfield.setPromptText("Mismatch Penalty");
        mismatchTextfield.setFocusTraversable(false);

        TextField gapTextfield = new TextField();
        gapTextfield.setPromptText("Gap Penalty");
        gapTextfield.setFocusTraversable(false);

        Button submitButton = new Button("Run");

        rightPane.getChildren().addAll(matchTextfield, mismatchTextfield, gapTextfield, submitButton);

        Scene scene = new Scene(root, TileSize * localAligner.Matrix[0].length + 500 + TileSize, TileSize * localAligner.Matrix.length + TileSize);
        primaryStage.setTitle("Smith Waterman Algorithm - Mustafa Yumurtacı");
        primaryStage.setScene(scene);
        primaryStage.show();
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (matchTextfield.getText().isEmpty()
                        || mismatchTextfield.getText().isEmpty() || gapTextfield.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Match, mismatch and gap values are required!");
                    alert.showAndWait();

                } else {
                    if (isRunOnce) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Running algorithm multiple times not supported yet. Please close and run program again!");
                        alert.showAndWait();
                    } else {

                        isRunOnce = true;
                        localAligner.SetValues(
                                Integer.parseInt(matchTextfield.getText()),
                                Integer.parseInt(mismatchTextfield.getText()),
                                Integer.parseInt(gapTextfield.getText()));

                        long executionTime = localAligner.Run();

                        listView.getItems().clear();

                        for (AlignedSequencePair pair : localAligner.AlignedSequencePairs
                        ) {
                            listView.getItems().add(pair.VerticalAlignedSequence + "\n" + pair.HorizontalAlignedSequence);
                        }
                        leftPane.getChildren().add(new Label("Score: " + localAligner.HighestScore));
                        leftPane.getChildren().add(new Label("Execution time: " + executionTime / 1000000 + " ms"));
                        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent event) {
                                DrawGrid(localAligner.Matrix, localAligner.VerticalSequence, localAligner.HorizontalSequence, localAligner.BackTraces, localAligner.AlignedSequencePairs, true, listView.getSelectionModel().getSelectedIndex());

                            }
                        });

                        DrawGrid(localAligner.Matrix, localAligner.VerticalSequence, localAligner.HorizontalSequence, localAligner.BackTraces, localAligner.AlignedSequencePairs, false, -1);

                    }
                }

            }
        });

        DrawGrid(localAligner.Matrix, localAligner.VerticalSequence, localAligner.HorizontalSequence, localAligner.BackTraces, localAligner.AlignedSequencePairs, false, -1);

    }

    public void DrawGrid(int[][] matrix, String verticalSequence, String horizontalSequence, ArrayList<ArrayList<Cell>> backtraces, ArrayList alignedSequencePairs, boolean isSelected, int selectedIndex) {
        String newVerticalSequence = " " + "γ" + verticalSequence;
        String newHorizontalSequence = " " + "γ" + horizontalSequence;
        GridPane grid = new GridPane();

        root.setCenter(grid);
        for (int y = 0; y < matrix.length + 1; y++) {

            TextField text = new TextField(String.valueOf(newVerticalSequence.charAt(y)));
            text.setEditable(false);
            text.setPrefHeight(TileSize);
            text.setPrefWidth(TileSize);
            text.setAlignment(Pos.CENTER);

            grid.setRowIndex(text, y);
            grid.setColumnIndex(text, 0);
            text.setStyle("-fx-background-color: yellow;-fx-border-width:1;-fx-border-color:black;");
            grid.getChildren().add(text);

        }

        for (int x = 0; x < matrix[0].length + 1; x++) {

            TextField text = new TextField(String.valueOf(newHorizontalSequence.charAt(x)));
            text.setEditable(false);
            text.setPrefHeight(TileSize);
            text.setPrefWidth(TileSize);
            text.setAlignment(Pos.CENTER);

            grid.setRowIndex(text, 0);
            grid.setColumnIndex(text, x);
            text.setStyle("-fx-background-color: yellow;-fx-border-width:1;-fx-border-color:black;");
            grid.getChildren().add(text);

        }

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                TextField text = new TextField(String.valueOf(matrix[y][x]));
                text.setEditable(false);
                text.setPrefHeight(TileSize);
                text.setPrefWidth(TileSize);
                text.setAlignment(Pos.CENTER);

                grid.setRowIndex(text, y + 1);
                grid.setColumnIndex(text, x + 1);
                text.setStyle("-fx-background-color: white;-fx-border-width:1;-fx-border-color:black;");
                if (isSelected) {
                    for (int z = 0; z < backtraces.get(selectedIndex).size(); z++) {
                        if (y == backtraces.get(selectedIndex).get(z).RowIndex && x == backtraces.get(selectedIndex).get(z).ColIndex) {
                            text.setStyle("-fx-background-color: orange;");

                        }

                        System.out.println("clicked on " + listView.getSelectionModel().getSelectedIndex());

                    }
                }
                grid.getChildren().add(text);

            }
        }


    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
