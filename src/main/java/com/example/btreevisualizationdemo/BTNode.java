package com.example.btreevisualizationdemo;

import java.io.Serializable;
import java.util.ArrayList;

public class BTNode<E extends Comparable<E>> implements Serializable {
    private int keysNumber;
    private BTNode<E> parent;
    private ArrayList<BTNode<E>> children = new ArrayList<BTNode<E>>();
    private ArrayList<E> keys = new ArrayList<>();

    public BTNode() {
    }


    public BTNode(int order) {

        keysNumber = order - 1;
    }

    public boolean isLeafNode() {
        //  this method is used to see if the current node is the last internal node meaning that if its children are empty
        if (keys.size() == 0)
            return false;
        for (BTNode<E> node : children)
            if (node.keys.size() != 0)
                return false;
        return true;
    }

    public BTNode<E> getParent() {
        return parent;
    }

    public void setParent(BTNode<E> parent) {
        this.parent = parent;
    }

    public ArrayList<BTNode<E>> getChildren() {
        return children;
    }

    public BTNode<E> getChild(int index) {
        return children.get(index);
    }

    public void addChild(int index, BTNode<E> node) {
        children.add(index, node);
    }

    public void removeChild(int index) {
        children.remove(index);
    }

    public E getKey(int index) {
        return keys.get(index);
    }


    //if we want to add a new key we first check if the element is in the array of keys.
    //this way we don't add the same element twice

    public void addKey(int index, E element) {
        if (!keys.contains(element)) {
            keys.add(index, element);
        }
    }

    public void removeKey(int index) {
        keys.remove(index);
    }

    //keysnumber stores the max number of keys the b tree can have so whe check if the node is full using it
    public boolean isFull() {
        return keysNumber == keys.size();
    }

    public boolean isOverflow() {
        return keysNumber < keys.size();
    }

    public boolean isNull() {
        return keys.isEmpty();
    }

    public int getSize() {
        return keys.size();
    }
}