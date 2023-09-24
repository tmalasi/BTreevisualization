package com.example.btreevisualizationdemo;

import java.io.*;
import java.util.LinkedList;

public class BTree<K extends Comparable<K>> implements Serializable {
    private BTNode<K> root = null;
    private int order;//the degree
    private int index;
    private int treeSize;
    //the min number of keys that can be stored in a node except root
    private final int halfNumber;

    //indicates a null or empty node in  the b tree
    public final BTNode<K> nullBTNode = new BTNode<K>();
    //holds a clone of the b tree when we make changes to it
    private LinkedList<BTree<K>> stepTrees = new LinkedList<BTree<K>>();

    //constructor
    public BTree(int order) {
        this.order = order;
        halfNumber = (order - 1) / 2;
    }

    public boolean isEmpty() {
        return root == null;
    }
    //getters and setters

    public BTNode<K> getRoot() {
        return root;
    }

    public void setRoot(BTNode<K> root) {
        this.root = root;
    }

    public int getOrder() {
        return order;
    }

    public LinkedList<BTree<K>> getStepTrees() {
        return stepTrees;
    }

    public void setStepTrees(LinkedList<BTree<K>> stepTrees) {
        this.stepTrees = stepTrees;
    }


    //we calculate the height of the tree starting from the root down
    //node=root
    public int getHeight(BTNode<K> node) {
        int height = 0;
        BTNode<K> currentNode = node;
        //if node is not empty
        while (!currentNode.equals(nullBTNode)) {
            currentNode = currentNode.getChild(0);
            height++;
        }
        return height;
    }

    //we want to get a node that has a certain key value
    public BTNode<K> getNode(K key) {
        //if it is empty there are no nodes
        if (isEmpty()) {
            return nullBTNode;
        }
        //
        BTNode<K> currentNode = root;

        //while current node is not null we iterate through its keys
        while (!currentNode.equals(nullBTNode)) {
            int i = 0;
            while (i < currentNode.getSize()) {
                if (currentNode.getKey(i).equals(key)) {
                    index = i;
                    return currentNode;
                } else if (currentNode.getKey(i).compareTo(key) > 0) {
                    //if the other keys are bigger than the one we are looking,
                    // we go to its child since tree is ordered
                    currentNode = currentNode.getChild(i);
                    i = 0;
                } else {
                    i++;
                }
            }
            //if we have cecked all the keys and the key is bigger
            // we check the last node
            if (!currentNode.isNull()) {
                currentNode = currentNode.getChild(currentNode.getSize());
            }
        }
        //the key not found
        return nullBTNode;
    }

    // When node is full we use this method to insert the new key
    private BTNode<K> FullNode(K key, BTNode<K> fullNode) {
        int fullNodeSize = fullNode.getSize();
        // Add the node to the appropriate location within that node
        for (int i = 0; i < fullNodeSize; i++) {
            //If the key at the full node is biger than the key we want to enter we replace it
            if (fullNode.getKey(i).compareTo(key) > 0) {
                fullNode.addKey(i, key);
                break;
            }
        }
        // If the size has remained the same no key was eneterd so we add it to the end
        if (fullNodeSize == fullNode.getSize())
            fullNode.addKey(fullNodeSize, key);
        // step trees stores a clone of the object

        stepTrees.add(cloneCreate.clone(this));

        return FullNode(fullNode);
    }

    //be splitting a full node (fullNode) into two separate nodes
    private BTNode<K> FullNode(BTNode<K> fullNode) {
        // Creates a new node
        BTNode<K> newNode = new BTNode<K>(order);

        // Iterates over the first half of the keys in the fullNode
        for (int i = 0; i < halfNumber; i++) {
            // Adds the key at index 0 of the fullNode to the newNode
            newNode.addKey(i, fullNode.getKey(0));
            // Removes the key at index 0 from the fullNode
            fullNode.removeKey(0);
        }
        // Returns the newly created newNode
        return newNode;
    }

    //responsible for extracting the remaining keys and child nodes from a fullNode
    private BTNode<K> getRestOfFullNodes(BTNode<K> fullNode) {
        // Creates a new node
        BTNode<K> newNode = new BTNode<K>(order);

        // Retrieves the size of the fullNode
        int halfNodeSize = fullNode.getSize();

        // Iterates over the keys and children of the fullNode
        for (int i = 0; i < halfNodeSize; i++) {
            // If it's not the first key, adds the key at index 1 of the fullNode to the newNode
            if (i != 0) {
                newNode.addKey(i - 1, fullNode.getKey(1));

                // Removes the key at index 1 from the fullNode
                fullNode.removeKey(1);
            }

            // Adds the child at index 0 of the fullNode to the newNode
            newNode.addChild(i, fullNode.getChild(0));

            // Removes the child at index 0 from the fullNode
            fullNode.removeChild(0);
        }

        // Returns the newly created newNode
        return newNode;
    }

    //merge a child node with parent node
    private void mergeWithParentNode(BTNode<K> childNode, int index) {
        // Retrieves the parent of the childNode
        BTNode<K> parentNode = childNode.getParent();
        // Adds the key at index 0 of the childNode
        // to the parent node at the specified index
        parentNode.addKey(index, childNode.getKey(0));
        // Removes the child node at the specified index from the parent node
        parentNode.removeChild(index);
        // Adds the child node at index 0 of the childNode
        // to the parent node at the specified index
        parentNode.addChild(index, childNode.getChild(0));
        // Adds the child node at index 1 of the childNode
        // to the parent node at the index after the specified index
        parentNode.addChild(index + 1, childNode.getChild(1));
    }

    private void mergeWithParentNode(BTNode<K> childNode) {
        int fatherNodeSize = childNode.getParent().getSize();
        //It finds the appropriate index in the parent node of the childNode where the merge operation should occur.
        for (int i = 0; i < fatherNodeSize; i++) {
            if (childNode.getParent().getKey(i).compareTo(childNode.getKey(0)) > 0) {
                mergeWithParentNode(childNode, i);
                break;
            }
        }
        //childNode is greater than all keys in the parent node
        if (fatherNodeSize == childNode.getParent().getSize()) {
            mergeWithParentNode(childNode, fatherNodeSize);
        }
        //updates the parent reference for all the child nodes of the parent node.
        for (int i = 0; i <= childNode.getParent().getSize(); i++)
            childNode.getParent().getChild(i).setParent(childNode.getParent());
    }

    // This method is responsible for setting the parent of each child node of the given node
// after a split operation in a B-tree.
    private void setSplitParentNode(BTNode<K> node) {
        for (int i = 0; i <= node.getSize(); i++)
            node.getChild(i).setParent(node);
    }

    private void processOverflow(BTNode<K> currentNode) {
        // create a new node by splitting the overflowing currentNode
        BTNode<K> newNode = FullNode(currentNode);
        // move child nodes from currentNode to newNode
        for (int i = 0; i <= newNode.getSize(); i++) {
            newNode.addChild(i, currentNode.getChild(0));
            currentNode.removeChild(0);
        }

        //create a node with the remaining child nodes from currentNode
        BTNode<K> originalNode = getRestOfFullNodes(currentNode);
        currentNode.addChild(0, newNode);
        currentNode.addChild(1, originalNode);

        originalNode.setParent(currentNode);
        newNode.setParent(currentNode);

        setSplitParentNode(originalNode);
        setSplitParentNode(newNode);

        stepTrees.add(cloneCreate.clone(this));
    }

    public void insert(K key) {
        // If tree is empty
        if (isEmpty()) {
            root = new BTNode<K>(order);
            root.addKey(0, key);
            treeSize++;
            root.setParent(nullBTNode);
            root.addChild(0, nullBTNode);
            root.addChild(1, nullBTNode);
            //adding a clone of the current object
            stepTrees.add(cloneCreate.clone(this));
            return;
        }
        // If tree is not empty

        BTNode<K> currentNode = root;
        //Navigate to the location to insert the key
        //appropriate node where a given key should be located or inserted
        while (!currentNode.isLeafNode()) {
            int i = 0;
            while (i < currentNode.getSize()) {
                // break if currentNode is leaf
                if (currentNode.isLeafNode()) {
                    i = currentNode.getSize();
                }
                //if nodes key is bigger then the given key then we start to search at its child at that index
                else if (currentNode.getKey(i).compareTo(key) > 0) {
                    currentNode = currentNode.getChild(i);
                    i = 0;
                }
                //if its smaller we keep looking at the current node
                else {
                    i++;
                }
            }
            //the key is bigger so we look at the last child of this node
            if (!currentNode.isLeafNode())
                currentNode = currentNode.getChild(currentNode.getSize());
        }
        //if leaf node is not full
        if (!currentNode.isFull()) {
            int i = 0;
            while (i < currentNode.getSize()) {
                //insert somewhere with the key > insertKey
                if (currentNode.getKey(i).compareTo(key) > 0) {
                    if (currentNode.getKey(i).equals(key)) {
                        return; // Key already exists, no need to insert
                    }
                    currentNode.addKey(i, key);
                    currentNode.addChild(currentNode.getSize(), nullBTNode);
                    treeSize++;

                    stepTrees.add(cloneCreate.clone(this));
                    return;
                }
                //we keep searching its keys
                else {
                    i++;
                }
            }
            //it means that the key is bigger than the values that are inn the node
            currentNode.addKey(currentNode.getSize(), key);
            currentNode.addChild(currentNode.getSize(), nullBTNode);
            treeSize++;
            stepTrees.add(cloneCreate.clone(this));
        }
        //leaf node is full
        else {
            // If node is full
            //Insert the node in the appropriate position within that node
            //Then take 1/2 key + child in the node (just inserted)
            BTNode<K> newChildNode = FullNode(key, currentNode);
            for (int i = 0; i < halfNumber; i++) {
                newChildNode.addChild(i, currentNode.getChild(0));
                currentNode.removeChild(0);
            }
            newChildNode.addChild(halfNumber, nullBTNode);

            //Get half a half, thus, the current node will have only the middle key left
            // Uploading n to 1 item (to be a father)
            BTNode<K> originalFatherNode = getRestOfFullNodes(currentNode);
            currentNode.addChild(0, newChildNode);
            currentNode.addChild(1, originalFatherNode);
            originalFatherNode.setParent(currentNode);
            newChildNode.setParent(currentNode);
            treeSize++;

            stepTrees.add(cloneCreate.clone(this));
            //checks the parents for overflow more keys than it should be
            if (!currentNode.getParent().equals(nullBTNode)) {
                while (!currentNode.getParent().isOverflow() && !currentNode.getParent().equals(nullBTNode)) {
                    boolean flag = currentNode.getSize() == 1 && !currentNode.getParent().isOverflow();
                    //if child or parent is overflow we merge them
                    if (currentNode.isOverflow() || flag) {
                        mergeWithParentNode(currentNode);
                        currentNode = currentNode.getParent();
                        stepTrees.add(cloneCreate.clone(this));

                        // If it's full again, repeat the action earlier

                        if (currentNode.isOverflow()) {
                            processOverflow(currentNode);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    //finds the index of the node as a child  if its not found it returns -1
    private int findChild(BTNode<K> node) {
        if (!node.equals(root)) {
            BTNode<K> fatherNode = node.getParent();

            for (int i = 0; i <= fatherNode.getSize(); i++) {
                //find the index of our node
                if (fatherNode.getChild(i).equals(node))
                    return i;
            }
        }

        //if not found
        return -1;
    }

    private BTNode<K> balanceDeletedNode(BTNode<K> node) {
        boolean flag;
        //find teh index of the node within its parents node
        int nodeIndex = findChild(node);
        K pair;
        BTNode<K> fatherNode = node.getParent();
        //teh adjecent node
        BTNode<K> currentNode;
        //we check if the node was in the index 0 or not
        // the sibling node is the right sibling
        if (nodeIndex == 0) {
            currentNode = fatherNode.getChild(1);
            // Is the (deleted) node on the left-most side (index 0)
            flag = true;
        } else {
            currentNode = fatherNode.getChild(nodeIndex - 1);
            flag = false;
        }

        int currentSize = currentNode.getSize();
        //if the node has more than the min number of keys then we redistrubute
        if (currentSize > halfNumber) {
            //if the node was in the leftmost side
            if (flag) {
                //the key is taken from the beginning of the sibling node
                //taken to the end of deleted
                pair = fatherNode.getKey(0);
                node.addKey(node.getSize(), pair);
                fatherNode.removeKey(0);
                pair = currentNode.getKey(0);
                currentNode.removeKey(0);
                node.addChild(node.getSize(), currentNode.getChild(0));
                currentNode.removeChild(0);
                fatherNode.addKey(0, pair);
                if (node.isLeafNode()) {
                    node.removeChild(0);
                }
                stepTrees.add(cloneCreate.clone(this));

            } else {
                //if the node was in the rightmost side
                pair = fatherNode.getKey(nodeIndex - 1);
                node.addKey(0, pair);
                fatherNode.removeKey(nodeIndex - 1);
                pair = currentNode.getKey(currentSize - 1);
                currentNode.removeKey(currentSize - 1);
                node.addChild(0, currentNode.getChild(currentSize));
                currentNode.removeChild(currentSize);
                fatherNode.addKey(nodeIndex - 1, pair);
                if (node.isLeafNode()) {
                    node.removeChild(0);
                }
//
                stepTrees.add(cloneCreate.clone(this));
            }
            return node;
        }
        //if the node does not have enough keys we should merge
        else {
            //If the deleted node is on the left-most side, the deleted node and the right sibling are merged together.
            if (flag) {
                // move the key from the fatherNode to the currentNode
                currentNode.addKey(0, fatherNode.getKey(0));
                fatherNode.removeKey(0);
                fatherNode.removeChild(0);
                // If the root becomes empty after the merge,
                // the right sibling becomes the new root
                if (root.getSize() == 0) {
                    root = currentNode;
                    currentNode.setParent(nullBTNode);
                }
                // If the merged node becomes empty,
                // update the child node reference in currentNode
                if (node.getSize() == 0) {
                    currentNode.addChild(0, node.getChild(0));
                    currentNode.getChild(0).setParent(currentNode);
                }
                //Move the remaining keys and child nodes
                // from the merged node to the currentNode
                for (int i = 0; i < node.getSize(); i++) {
                    currentNode.addKey(i, node.getKey(i));
                    currentNode.addChild(i, node.getChild(i));
                    currentNode.getChild(i).setParent(currentNode);
                }

                stepTrees.add(cloneCreate.clone(this));
            }
            //If the deleted node is not on the left-most side, the deleted node and the left sibling are merged together.
            else {
                //move the key from the fatherNode to the currentNode
                currentNode.addKey(currentNode.getSize(), fatherNode.getKey(nodeIndex - 1));
                fatherNode.removeKey(nodeIndex - 1);
                fatherNode.removeChild(nodeIndex);
                //If the root becomes empty after the merge,
                // set the currentNode as the new root
                if (root.getSize() == 0) {
                    root = currentNode;
                    currentNode.setParent(nullBTNode);
                }
                //If the merged node becomes empty,
                // update the child node reference in currentNode
                int currentNodeSize = currentNode.getSize();
                if (node.getSize() == 0) {
                    currentNode.addChild(currentNodeSize, node.getChild(0));
                    currentNode.getChild(currentNodeSize).setParent(currentNode);
                }
                //Move the remaining keys and child nodes
                // from the merged node to the currentNode
                for (int i = 0; i < node.getSize(); i++) {
                    currentNode.addKey(currentNodeSize + i, node.getKey(i));
                    currentNode.addChild(currentNodeSize + i, node.getChild(i));
                    currentNode.getChild(currentNodeSize + i).setParent(currentNode);
                }

                stepTrees.add(cloneCreate.clone(this));
            }
            return fatherNode;
        }
    }

    //used to replace a key in a node with a key from a child node during the deletion process in a B-tree
    private BTNode<K> replaceNode(BTNode<K> node) {
        //he nearest (oldest) right child node of the given node
        BTNode<K> currentNode = node.getChild(index + 1);
        while (!currentNode.isLeafNode()) {
            currentNode = currentNode.getChild(0);
        }

        if (currentNode.getSize() - 1 < halfNumber) {
            // Replaced with the nearest (oldest) child
            currentNode = node.getChild(index);
            int currentNodeSize = currentNode.getSize();
            while (!currentNode.isLeafNode()) {
                currentNode = currentNode.getChild(currentNodeSize);
            }
            node.addKey(index, currentNode.getKey(currentNodeSize - 1));
            currentNode.removeKey(currentNodeSize - 1);
            currentNode.addKey(currentNodeSize - 1, node.getKey(index + 1));
            node.removeKey(index + 1);
            index = currentNode.getSize() - 1;


            stepTrees.add(cloneCreate.clone(this));
        } else {
            // Replace with the nearest right child (minimum)
            node.addKey(index + 1, currentNode.getKey(0));
            currentNode.removeKey(0);
            currentNode.addKey(0, node.getKey(index));
            node.removeKey(index);
            index = 0;

            stepTrees.add(cloneCreate.clone(this));
        }
        return currentNode;
    }

    public void delete(K key) {

        stepTrees.add(cloneCreate.clone(this));
        // Find the node containing the key
        BTNode<K> node = getNode(key);
        BTNode<K> deleteNode = null;
        //if there is no node with that key
        if (node.equals(nullBTNode))
            return;

        // If it is root, tree 1 node 1 key -> Delete always
        if (node.equals(root) && node.getSize() == 1 && node.isLeafNode()) {
            root = null;
            treeSize--;
            stepTrees.add(cloneCreate.clone(this));
        } else {
            boolean flag = true;
            boolean isReplaced = false;
            //if its leaf
            if (!node.isLeafNode()) {
                // we replace the node with a key from its child nodes
                node = replaceNode(node);
                deleteNode = node;
                isReplaced = true;
            }

            //size of the node after deletion is less than the required minimum (halfNumber)
            if (node.getSize() - 1 < halfNumber) {
                //we balance the node
                node = balanceDeletedNode(node);
                if (isReplaced) {
                    //takes the keys in the node
                    for (int i = 0; i <= node.getSize(); i++) {
                        //takes the keys of the child node
                        for (int j = 0; i < node.getChild(i).getSize(); j++) {
                            //if the child node has a key equal to key we were deleting
                            if (node.getChild(i).getKey(j).equals(key)) {
                                deleteNode = node.getChild(i);
                                break;
                            }
                        }
                    }
                }
            } else if (node.isLeafNode()) {
                node.removeChild(0);
            }
            //a loop that continues as long as the node is not the root
            // and its size is less than 'halfNumber'
            while (!node.getChild(0).equals(root) && node.getSize() < halfNumber && flag) {
                if (node.equals(root)) {
                    for (int i = 0; i <= root.getSize(); i++) {
                        //size of the node becomes empty after deletion
                        if (root.getChild(i).getSize() == 0) {
                            //we need to balance
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    node = balanceDeletedNode(node);
                }
            }

            //if true node was not replaced
            if (deleteNode == null) {
                // Check whether previously deleted or just replace
                node = getNode(key);
            } else {
                node = deleteNode;
            }
            //, the code proceeds to delete the key by iterating through the keys in the node and removing the matching key
            if (!node.equals(nullBTNode)) {
                //After replace is completed, delete node di (then, node has become)
                for (int i = 0; i < node.getSize(); i++) {
                    if (node.getKey(i) == key) {
                        node.removeKey(i);
                    }
                }
                treeSize--;
                stepTrees.add(cloneCreate.clone(this));
            }
        }
    }
}

//to create a deep copy (clone) of an object
class cloneCreate {

    public static <T extends Serializable> T clone(T object) {
        T cloneObject = null;
        try {
            //to write the obj into byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //to serialize the object and write it to the stream
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            //to allow reading the object from the byte array
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            //to deserialize the object
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cloneObject = (T) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObject;
    }
}