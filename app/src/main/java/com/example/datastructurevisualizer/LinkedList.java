package com.example.datastructurevisualizer;

import android.graphics.Canvas;

/**
 * LinkedList implementation to be used by other Data Structures.
 *
 * Since LinkedList is only used as a smaller part of larger data structures,
 * it includes some special functionality. For example, it stores xPos and
 * yPos in order to guarantee correct placement. It also overrides render so as
 * not to clear the screen before rendering.
 */
public class LinkedList extends TreeVisualizer {

    // This LinkedList will have only one child.
    static final int numChildren = 1;

    // Position of this LinkedList on the Canvas.
    int xPos = (int)(AnimationParameters.NODE_RADIUS * 1.5);
    int yPos = (int)(AnimationParameters.NODE_RADIUS * 1.5);

    // Used to track whether nodeList is being used as a queue or a stack.
    boolean isStack = false;

    /**
     * Return numChildren per node, which is 1. Used in TreeVisualize.
     */
    @Override
    public int getNumChildren() { return numChildren; }

    /**
     * Checks whether this LinkedList is empty.
     *
     * @return true if the LinkedList is empty, otherwise false.
     */
    protected boolean isEmpty() {
        return root == null;

    }

    /**
     * Sets the desired position of the root Node of this LinkedList.
     * This method does not actually move the Nodes to their proper positions.
     *
     * @param xPos x position of root Node.
     * @param yPos y position of root Node
     */
//    public void setPosition(int xPos, int yPos) {
//        this.xPos = xPos;
//        this.yPos = yPos;
//
//    }

    /**
     * Gets the bottom left corner of the Canvas. Used to place stacks.
     *
     * @return the bottom left corner of the Canvas.
     */
    private int getBot() {
        return (int) (MainActivity.getCanvas().getHeight() -
                AnimationParameters.NODE_RADIUS * 1.5);

    }

    /**
     * Inserts the key into the LinkedList at the root.
     *
     * @param key the key to be inserted.
     */
    public void stackInsert(int key) {
        Node node = new Node(key, getNumChildren());
        node.children[0] = root;
        root = node;
        highlightNode(node);

        // Marks nodeList as a stack.
        isStack = true;

        // Places the Nodes at their desired positions.
        if (node.children[0] == null) placeTreeNodes(xPos, getBot(), 0, (int) AnimationParameters.depthLen);
        else placeTreeNodes(node.children[0].position[0], (int) (node.children[0].position[1] -
                        AnimationParameters.depthLen), 0, (int) AnimationParameters.depthLen);
        placeNodesAtDestination();

    }

    /**
     * Inserts a key into the LinkedList in the fashion of a priority queue.
     * Smaller Nodes will be placed at the head.
     *
     * @param key the key to be inserted.
     * @param value the value of the Node.
     */
    public void priorityQueueInsert(int key, int value) {
        Node node = new Node(key, getNumChildren());
        node.value = value;
        highlightNode(node);

        // Marks nodeList as a non stack.
        isStack = false;

        // Places the new Node at the root if the queue is empty or if it is
        // the least Node in the queue.
        if (root == null || node.value <= root.value) {
            node.children[0] = root;
            root = node;
            placeTreeNodes(xPos, yPos, 0, (int) AnimationParameters.depthLen);
            placeNodesAtDestination();
            return;

        }

        // Finds the appropriate place for the Node in the queue.
        Node currNode = root;
        while (currNode.children[0] != null &&
                currNode.children[0].value <= node.value)
            currNode = currNode.children[0];

        // Places the Node.
        node.children[0] = currNode.children[0];
        currNode.children[0] = node;

        // Places the Nodes at their desired positions.
        placeTreeNodes(xPos, yPos, 0, (int) AnimationParameters.depthLen);
        placeNodesAtDestination();

    }

    /**
     * Inserts the key into the LinekdList at the tail.
     *
     * @param key the key to be inserted.
     */
    public void queueInsert(int key) {
        Node node = new Node(key, getNumChildren());
        highlightNode(node);

        // Marks nodeList as a non stack.
        isStack = false;

        // Places the Node at the root if the LinkedList is empty.
        if (root == null) {
            root = node;

        }
        // Places the Node at the tail if the LinkedList is non-empty.
        else {
            Node currNode = root;
            while (currNode.children[0] != null) currNode = currNode.children[0];
            currNode.children[0] = node;

        }

        // Places the Nodes at their desired positions.
        placeTreeNodes(xPos, yPos, 0, (int) AnimationParameters.depthLen);
        placeNodesAtDestination();

    }

    /**
     * Deletes the root and returns its key. If root is null, returns -1.
     *
     * @return the key of root or -1.
     */
    public int pop() {
        Node node = root;

        // Returns -1 if the root is null.
        if (root == null) return -1;

        // If the root is not null, replaces root and returns its key.
        root = root.children[0];

        // Places tree Nodes based on whether nodeList is a stack or a queue.
        if (isStack)
            if (root != null) placeTreeNodes(xPos, root.position[1],
                    0, (int) AnimationParameters.depthLen);
            else placeTreeNodes(xPos, getBot(), 0, (int) AnimationParameters.depthLen);
        else placeTreeNodes(xPos, yPos, 0, (int) AnimationParameters.depthLen);
        placeNodesAtDestination();

        // Returns the key that has been removed.
        return node.key;

    }

    /**
     * Returns the key of the root. If root is null, returns -1.
     *
     * @return the key of the root or -1.
     */
    public int peek() {

        // Returns -1 if the root is null.
        if (root == null) return -1;

        // If the root is not null, returns its key.
        return root.key;

    }

    /**
     * Unused insertion method.
     *
     * @param key the key to be inserted.
     */
    @Override
    public void insertNoAnim(int key) {
        queueInsert(key);

    }

    /**
     * Unused animated insertion method.
     *
     * @param key the key to be inserted.
     */
    @Override
    protected void insertAnim(int key) {
        insertNoAnim(key);

    }

    /**
     * Unused removal method.
     *
     * @param key the key to be removed.
     */
    @Override
    public void removeNoAnim(int key) {

        // If the LinkedList is empty, returns.
        if (root == null) return;

        // Deletes the root.
        if (root.key == key) {
            root = root.children[0];
            placeTreeNodes(xPos, yPos, 0, (int) AnimationParameters.depthLen);
            placeNodesAtDestination();
            return;

        }

        // Finds and removes the inputed key.
        for (Node currNode = root; currNode.children[0] != null; currNode = currNode.children[0]) {
            if (currNode.children[0].key == key) {
                currNode.children[0] = currNode.children[0].children[0];
                return;

            }
        }

        // Places the Nodes at their desired positions.
        placeTreeNodes(xPos, yPos, 0, (int) AnimationParameters.depthLen);
        placeNodesAtDestination();

    }

    /**
     * Unused animated removal method.
     *
     * @param key the key to be removed.
     */
    @Override
    protected void removeAnim(int key) {
        removeNoAnim(key);

    }

    /**
     * Renders the LinkedList. Unlike other renderers, this will not clear the canvas.
     *
     * @param canvas the Canvas to draw in.
     */
    @Override
    public void render(Canvas canvas) {
        drawTreeRecursive(root, canvas);

    }
}