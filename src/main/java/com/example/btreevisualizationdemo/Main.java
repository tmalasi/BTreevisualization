package com.example.btreevisualizationdemo;

import java.util.LinkedList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.*;
import javafx.stage.Stage;



public class Main extends Application {
    private int key;//entered value stored here
    private BTreePane btPane;
    private TextField keyText = new TextField();//input text box

    Button clearScreenButton = new Button("Clear");
    Button insertButton = new Button("Insert");
    Button deleteButton = new Button("Delete");

    private int index = 0;
    private LinkedList<BTree<Integer>> bTreeLinkedList = new LinkedList<BTree<Integer>>();
    private BTree<Integer> bTree = new BTree<Integer>(3);//change order of tree
    Slider slider = new Slider(3, 7, 3);
    public void setOrder(int x){
        bTree = new BTree<Integer>(x);//change order of tree
        reset();
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        BackgroundFill backgroundFill = new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.LIGHTBLUE),
                        new Stop(1, Color.LIGHTPINK)
                ),
                CornerRadii.EMPTY, Insets.EMPTY);
// Create BackgroundFill with LinearGradient

// Create Background with the BackgroundFill
        Background background = new Background(backgroundFill);

// Set the background to the BorderPane
        borderPane.setBackground(background);

        // Create button HBox for bottom bar
        HBox hBox = new HBox(20);//15 pixel distance between elements
        VBox vBox = new VBox(20);//15 pixel distance between elements

        Text heading = new Text("B-Tree Visualization");
        //heading font
        Font f1 = Font.font("SansSerif", FontWeight.BOLD,FontPosture.ITALIC, 25);
        heading.setFont(f1);//setting heading font
        heading.setFill(Color.PURPLE);
        BorderPane.setMargin(heading, new Insets(20, 0, 0, 20));
        borderPane.setTop(heading);//heading on top of border pane
        borderPane.setAlignment(heading, Pos.TOP_LEFT);
        //paddings for hbox and vbox
        BorderPane.setMargin(hBox, new Insets(10));
        BorderPane.setMargin(vBox, new Insets(10));
        //font for bottom box
        Font f3 = Font.font("SansSerif", FontWeight.BOLD, FontPosture.REGULAR, 17);
        Label order =new Label("Set Order:");
        order.setFont(f3);
        order.getStyleClass().add("orderText");
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setBlockIncrement(1);
        //add the slider at the same vbox with the height
        vBox.getChildren().addAll(order,slider);
        borderPane.setLeft(vBox);

        // TextField for inputs
        keyText.setPrefWidth(100);
        keyText.setAlignment(Pos.BASELINE_RIGHT);
        // Button

        insertButton.getStyleClass().add("insertButton");

        deleteButton.getStyleClass().add("deleteButton");

        clearScreenButton.getStyleClass().add("clearScreenButton");

        //height text box cotents
        StackPane heightPane= new StackPane();
        Label enterANumber = new Label("Enter an integer: ");
        enterANumber.setFont(f3);
        enterANumber.getStyleClass().add("numberText");

        HBox heightBox = new HBox();
        heightBox.setAlignment(Pos.CENTER_LEFT);

        //adding all elements to bottom bar
        hBox.getChildren().addAll( enterANumber, keyText, insertButton, deleteButton, clearScreenButton);
        hBox.setAlignment(Pos.CENTER);//bottom bar in centre always
        VBox bottomVBox = new VBox();//vbox for buttons
        bottomVBox.getChildren().addAll(heightBox,hBox);

        borderPane.setBottom(bottomVBox);
        // Create TreePane in center, size of window
        Scene scene = new Scene(borderPane, 1300, 550);
        //we link the css
        String css = Main.class.getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("B-Tree Visualization");
        primaryStage.setScene(scene);

        btPane = new BTreePane( scene.getWidth()/2, 100, bTree);
        borderPane.setCenter(btPane);
        BorderPane.setMargin(bottomVBox, new Insets(15));

        insertButton.setOnMouseClicked(e -> insertValue());
        deleteButton.setOnMouseClicked(e -> deleteValue());
        clearScreenButton.setOnMouseClicked(e -> reset());
        //we set the order based on the value that is passed on the slider
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int newOrder = newValue.intValue();
            setOrder(newOrder);
        });
        //to set size of startup window
        primaryStage.show();

    }
    private void checkNext() {
        if (checkNextCondition()==1) {
            //this method is called to update the visualization
            next();
        } else if (checkNextCondition()==2) {
            return;
        } else if (checkNextCondition()==3)
            next();
        else {
            return;
        }
    }

    //conditions for next function
    private int checkNextCondition(){
        //index is pointing in the tree si we have steps to make
        if(index > 0 && index < bTreeLinkedList.size() - 1)
            return 1;
        //index is pointing at the last element of the tree so there ar no more steps to take
        else if (index > 0 && index == bTreeLinkedList.size() - 1) {
            return 2;
        }
        //index is pointing at the root node there ar steps to take
        else if (index == 0 && index < bTreeLinkedList.size() - 1) {
            return 3;
        }
        else {
            return 4;
        }
    }

    //function to insert value to the tree
    //calls maketree
    private void insertValue() {
        try {
            //we get the key from the keytext
            key = Integer.parseInt(keyText.getText());
            bTree.setStepTrees(new LinkedList<BTree<Integer>>());

            bTree.insert(key);

            index = 0;
            bTreeLinkedList = bTree.getStepTrees();
            btPane.updatePaneLayout(bTreeLinkedList.get(0));
            checkNext();

        } catch (Exception e) {//all other exceptions
            System.out.println("Warning!"+e);
        } finally {//finally clear the text box
            keyText.setText("");
        }
    }


    private void deleteValue() {
        try {

            // Parse the key from the keyText TextField
            key = Integer.parseInt(keyText.getText());
            // Check if the key is present in the B-tree
            if (bTree.getNode(key) == bTree.nullBTNode) {
                throw new Exception("Not in the tree!");
            }
            bTree.setStepTrees(new LinkedList<BTree<Integer>>());
            // Delete the key from the B-tree
            bTree.delete(key);

            index = 0;
            bTreeLinkedList = bTree.getStepTrees();
            // Update the layout of the btPane with the first B-tree in the stepTrees list
            btPane.updatePaneLayout(bTreeLinkedList.get(0));
            checkNext();

        } catch (Exception e) {//all other exceptions
            System.out.println("Warning!"+e);
        } finally {//finally clear the text box
            keyText.setText("");
        }
    }


    //called if tree is not make in one step
    private void next() {
        if (index < bTreeLinkedList.size() - 1) {
            index++;
            btPane.updatePaneLayout(bTreeLinkedList.get(index));
            checkNext();
        }
    }

    //to delete everything from the tree
    private void reset() {
        keyText.setText("");
        bTree.setRoot(null);
        index = 0;
        bTreeLinkedList.clear();
        btPane.updatePaneLayout(bTree);

        checkNext();


    }
}
