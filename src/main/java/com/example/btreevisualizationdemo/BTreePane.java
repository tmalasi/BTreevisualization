package com.example.btreevisualizationdemo;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

class BTreePane extends Pane {
    private BTree<Integer> bTree;
    private double X;//original x value
    private double Y;//original Y value
    private int fSize = 15; //fixed font size
    private int rowSpace = 70;//space between 2 rows


    private void constructNode(String s, double x, double y) {
        Color color = Color.WHITE;//colour of nodes rectagle
        Rectangle rect;
        //size of rectangle based on number of digits in input
        if (s.length() < 3)
            rect = new Rectangle(x, y, 50, 30);
        else
            rect = new Rectangle(x, y, 51, 30);
        //making rectangle for node
        rect.setFill(color);
        rect.setStroke(Color.PURPLE);
        Text txt;
        //to add text to rectangle
        //position based on number of digits.
        if (compareLength(s, 1))
            txt = new Text(x + 10 - s.length(), y + 20, s);
        else if (compareLength(s, 2))
            txt = new Text(x + 11 - s.length(), y + 20, s);
        else
            txt = new Text(x + 10 - s.length(), y + 20, s);
        txt.setFill(Color.BLACK);//nodes ke text ka colour
        txt.setFont(Font.font("Arial", FontWeight.BOLD, fSize));
        this.getChildren().addAll(rect, txt);
    }

    public BTreePane(double x, double y, BTree<Integer> bTree) {
        this.X = x;
        this.Y = y;
        this.bTree = bTree;
    }

    //called as soon as any node is inserted or deleted
    public void updatePaneLayout(BTree<Integer> bTree) {
        this.getChildren().clear();
        this.bTree = bTree;
        DrawBTree(bTree.getRoot(), X, Y);
    }

    //function to draw tree
    private void DrawBTree(BTNode<Integer> root, double x, double y) {

        if (!(root == null)) {
            // Draw the keys of the current node
            for (int i = 0; i < root.getSize(); i++) {
                constructNode(root.getKey(i).toString(), x + i *50, y);
            }
            // Drawing lines
            double startY = y + 2 * fSize;
            if (root.isLeafNode() == false) {
                int i = 0;
                while (i < root.getChildren().size()) {
                    double startLine = x + i * 50;
                    double startChildNode, endLine;

                    //Calculate the start and end positions for the line based on the comparison of child node sizes
                    if (compareRootSize(i, root) == 1) {
                        startChildNode = startLine + (bTree.getOrder() - 1) * (bTree.getHeight(root.getChild(i)) - 1) * 50 / 2;
                        endLine = startChildNode + ((double) root.getChild(i).getSize()) / 2 * 50;
                    } else if (compareRootSize(i, root) == 0) {
                        endLine = startLine - (bTree.getOrder() - 1) * (bTree.getHeight(root.getChild(i)) - 1) * 50 / 2
                                - ((double) root.getChild(i).getSize()) / 2 * 50;
                        startChildNode = endLine - ((double) root.getChild(i).getSize()) / 2 * 50;
                    } else {
                        startChildNode = startLine - ((double) root.getChild(i).getSize()) / 2 * 50;
                        endLine = startLine;
                    }
                    // Adjust startChildNode and endLine for the first and last child nodes
                    if (i == 0) {
                        startChildNode = startChildNode - 50 * 2;
                        endLine = endLine - 50 * 2;
                    } else if (i == root.getSize()) {
                        startChildNode = startChildNode + 50 * 2;
                        endLine = endLine + 50 * 2;
                    }
                    // connecting the current node to its child node
                    if (!root.getChild(i).isNull()) {
                        Line line = new Line(startLine, startY, endLine, y + rowSpace);
                        line.setStrokeWidth(1.5);
                        line.setStroke(Color.PURPLE);

                        this.getChildren().add(line);
                    }
                    DrawBTree(root.getChild(i), startChildNode, y + rowSpace);
                    i++;
                }
            }
        }
    }

    //function to compare lengths
    public boolean compareLength(String s, int x) {
        if (s.length() == x)
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }

    //function to compare root size
    public int compareRootSize(int i, BTNode root) {
        if ((double) i > ((double) root.getSize()) / 2)
            return 1;
        else if ((double) i < ((double) root.getSize()) / 2)
            return 0;
        else return -1;
    }
}
