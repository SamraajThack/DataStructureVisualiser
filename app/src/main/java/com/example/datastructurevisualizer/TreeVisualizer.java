package com.example.datastructurevisualizer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Superclass for all trees. Enables code reuse during tree visualization.
 *
 * Contains numChildren, a field indicating the number of children per node for a given tree.
 * Contains root, the root node for a given tree. It is located here so as to be used by
 * shared visualization methods.
 * Contains pre-order, post-order, in-order, and breadth-first traversals.
 * Contains placeNodes, which places each Node in the tree at an appropriate
 * position. Will place the root at the inputed position, or at the upper
 * center of the screen if no position is inputed.
 * Redefines getAllNodes and getNode to function over a Tree.
 * Defines getDepth and checkInsert to prevent invalid entries into the tree.
 * Redefines render to recursively render Tree Nodes and edges between them.
 * Contains a method createJSON which is used to save the Tree.
 */
public abstract class TreeVisualizer extends NodeVisualizer {

    // Root of this tree.
    Node root;

    /**
     * This method is used to get the number of children in a tree.
     * Each tree will override it to return its own numChildren.
     */
    public abstract int getNumChildren();

    /**
     * Performs a pre-order traversal over a tree. Will perform an animation
     * indicating the current node being targeted and the stack of explored Nodes.
     *
     * @param currNode the node currently targeted by the traversal.
     */
    private void treePreOrderTraversal(final Node currNode) {
        int numChildren = getNumChildren();

        // Returns if currNode is null.
        if (currNode == null) return;

        // Adds this Node to the stack.
        queueStackAddAnimation(currNode, "Exploring " + currNode.key,
                AnimationParameters.ANIM_TIME);

        // Highlights the current Node.
        queueNodeSelectAnimation(currNode, "Current Node " + currNode.key,
                AnimationParameters.ANIM_TIME);

        // Explores left subtree.
        for (int i = 0; i < numChildren / 2; ++i)
            treePreOrderTraversal(currNode.children[i]);

        // Explores right subtree.
        for (int i = numChildren / 2; i < numChildren; ++i)
            treePreOrderTraversal(currNode.children[i]);

        // Removes this Node from the stack.
        queueListPopAnimation("Finished exploring " + currNode.key,
                AnimationParameters.ANIM_TIME);

    }

    /**
     * Begins a pre-order traversal.
     */
    public void preOrderTraversal() {
        beginAnimation();
        treePreOrderTraversal(root);
        stopAnimation();

    }

    /**
     * Performs a post-order traversal over a tree. Will perform an animation
     * indicating the current node being targeted and the stack of explored Nodes.
     *
     * @param currNode the node currently targeted by the traversal.
     */
    private void treePostOrderTraversal(Node currNode) {
        int numChildren = getNumChildren();

        // Returns if currNode is null.
        if (currNode == null) return;

        // Adds this Node to the stack.
        queueStackAddAnimation(currNode, "Exploring " + currNode.key,
                AnimationParameters.ANIM_TIME);

        // Explores left subtree.
        for (int i = 0; i < numChildren / 2; ++i)
            treePostOrderTraversal(currNode.children[i]);

        // Explores right subtree.
        for (int i = numChildren / 2; i < numChildren; ++i)
            treePostOrderTraversal(currNode.children[i]);

        // Highlights the current Node.
        queueNodeSelectAnimation(currNode, "Current Node " + currNode.key,
                AnimationParameters.ANIM_TIME);

        // Removes this Node from the stack.
        queueListPopAnimation("Finished exploring " + currNode.key,
                AnimationParameters.ANIM_TIME);

    }


    /**
     * Begins a post-order traversal.
     */
    public void postOrderTraversal() {
        beginAnimation();
        treePostOrderTraversal(root);
        stopAnimation();

    }

    /**
     * Performs an in-order traversal over a tree. Will perform an animation
     * indicating the current node being targeted and the stack of explored Nodes.
     *
     * @param currNode the node currently targeted by the traversal.
     */
    private void treeInOrderTraversal(Node currNode) {
        int numChildren = getNumChildren();

        // Returns if currNode is null.
        if (currNode == null) return;

        // Adds this Node to the stack.
        queueStackAddAnimation(currNode, "Exploring " + currNode.key,
                AnimationParameters.ANIM_TIME);

        // Explores left subtree.
        for (int i = 0; i < numChildren / 2; ++i)
            treeInOrderTraversal(currNode.children[i]);

        // Highlights the current Node.
        queueNodeSelectAnimation(currNode, "Current Node " + currNode.key,
                AnimationParameters.ANIM_TIME);

        // Explores right subtree.
        for (int i = numChildren / 2; i < numChildren; ++i)
            treeInOrderTraversal(currNode.children[i]);

        // Removes this Node from the stack.
        queueListPopAnimation("Finished exploring " + currNode.key,
                AnimationParameters.ANIM_TIME);

    }

    /**
     * Begins an in-order traversal.
     */
    public void inOrderTraversal() {
        beginAnimation();
        treeInOrderTraversal(root);
        stopAnimation();

    }

    /**
     * Performs a breadth-first traversal over a tree. Will perform an animation
     * indicating the current node being targeted and the queue of Nodes to explore.
     */
    private void treeBreadthFirstTraversal() {
        Node currNode = root;
        java.util.LinkedList<Node> queue = new java.util.LinkedList<Node>();

        // Highlights the first Node.
        queueNodeSelectAnimation(currNode, "Exploring " + currNode.key,
                AnimationParameters.ANIM_TIME);

        // Explores Nodes until the queue is empty.
        while (true) {

            // Marks that this Node's children should be explored.
            for (int i = 0; i < getNumChildren(); ++i) {
                if (currNode.children[i] != null) {
                    queue.addLast(currNode.children[i]);
                    queueQueueAddAnimation(currNode.children[i],
                            "Queueing " + currNode.children[i].key,
                            AnimationParameters.ANIM_TIME);

                }
            }

            // Pops the next Node from the queue.
            if (!queue.isEmpty()) {
                currNode = queue.pop();
                queueListPopAnimation("Popped " + currNode.key,
                        AnimationParameters.ANIM_TIME);
                queueNodeSelectAnimation(currNode, "Exploring " + currNode.key,
                        AnimationParameters.ANIM_TIME);

            }
            // If the queue is empty, breaks.
            else break;

        }
    }

    /**
     * Begins a breadth-first traversal.
     */
    public void breadthFirstTraversal() {
        beginAnimation();
        treeBreadthFirstTraversal();
        stopAnimation();

    }

    /**
     * Performs a search traversal over a tree. Will perform an animation
     * indicating the current node being searched.
     *
     * @param currNode the node currently targeted by the traversal.
     */
    private void valueSearch(int key, Node currNode) {
        int numChildren = getNumChildren();

        // Returns if currNode is null.
        if (currNode == null) {
            queueNodeSelectAnimation(null, "Current Node null, desired Node not found",
                    AnimationParameters.ANIM_TIME);
            return;

        }

        // Finishes the traversal if the key has been found.
        if (currNode.key == key) {
            queueNodeSelectAnimation(currNode, key + " == "
                    + currNode.key + ", desired Node found",
                    AnimationParameters.ANIM_TIME);

        }
        // Explores the left subtree.
        else if (key < currNode.key) {
            for (int i = 0; i < numChildren / 2; ++i) {
                queueNodeSelectAnimation(currNode.children[i],
                        key + " < " + currNode.key +
                                ", exploring left subtree",
                        AnimationParameters.ANIM_TIME);
                valueSearch(key, currNode.children[i]);

            }
        }
        // Explores the right subtree.
        else {
            for (int i = numChildren / 2; i < numChildren; ++i) {
                queueNodeSelectAnimation(currNode.children[i],
                        key + " > " + currNode.key +
                                ", exploring right subtree",
                        AnimationParameters.ANIM_TIME);
                valueSearch(key, currNode.children[i]);

            }
        }
    }

    /**
     * Begins searching for a key.
     *
     * @param key the key to search for.
     */
    public void search(int key) {
        beginAnimation();
        queueNodeSelectAnimation(root, "Start at root",
                AnimationParameters.ANIM_TIME);
        valueSearch(key, root);
        stopAnimation();

    }

    /**
     * Recursively places each node in the Tree. Each successive layer will have
     * width / numChildren horizontal distance between Nodes and depthLen vertical
     * distance between Nodes.
     *
     * @param width horizontal distance between Nodes.
     * @param depth current depth within the Tree.
     * @param depthLen the vertical distance between layers of the tree.
     * @param currNode the Node whose children should be placed.
     */
    private void placeTreeNodesRecursive(float width, int depth, int depthLen, Node currNode) {
        float currX, currY;
        int numChildren;

        // Returns if the bottom of the Tree has been reached.
        if (depth <= 0 || currNode == null) return;

        // Stores the number of children for measurement.
        numChildren = getNumChildren();

        // Starts from the current position.
        currX = currNode.destination[0];
        currY = currNode.destination[1];

        // Offsets currX to the leftmost Node.
        // Note: offsets slightly more than appropriate so the for loop below is easier to write.
        currX -= (int)((width * (1.0 + numChildren)) / 2.0);

        // Offsets currY by depthLen.
        currY += depthLen;

        // Recursively places each child Node.
        for (int i = 0; i < numChildren; ++i) {
            currX += width;

            // Will only place non-null nodes.
            if (currNode.children[i] != null) {
                currNode.children[i].destination[0] = (int)currX;
                currNode.children[i].destination[1] = (int)currY;
                placeTreeNodesRecursive(width / numChildren, depth - 1, depthLen, currNode.children[i]);

            }
        }
    }

    /**
     * Places all Nodes. Does so by using the Tree's depth and desired width to
     * calculate the appropriate width between the Nodes on the first layer, then
     * recursively calculating the appropriate width between Nodes in every
     * successive layer.
     *
     * This method can be used for Trees with any fixed number of children (that
     * includes LinkedLists).
     *
     * @param xStart the x position of the root.
     * @param yStart the y position of the root.
     * @param treeWidth the total width of the tree (Node center to Node center).
     * @param depthLen the vertical distance between layers of the tree.
     */
    public void placeTreeNodes(int xStart, int yStart, int treeWidth, int depthLen) {
        int numChildren = getNumChildren();
        int depth = getDepth();
        float width;

        // Will not execute if the tree is empty.
        if (root == null) return;

        // Initializes position of root.
        root.destination[0] = xStart;
        root.destination[1] = yStart;

        // Calculates the width between children of the root Node.
        width = (float)treeWidth / numChildren;

        // If rendering a LinkedList, sets width to 0 for convenience.
        if (numChildren == 1) width = 0;

        // Begins recursively placing the Tree Nodes.
        placeTreeNodesRecursive(width, depth, depthLen, root);

    }

    /**
     * Places Nodes in the tree. Starts at the center of the tree, NODE_RADIUS * 3
     * units from the top.
     */
    public void placeTreeNodes() {
        int width;
        float rad;

        // Stores width and rad for convenience.
        width = MainActivity.getCanvas().getWidth();
        rad = AnimationParameters.NODE_RADIUS;

        // Places the Nodes.
        placeTreeNodes((int) (width / 2 + rad * 1.5), (int) (rad * 2),
                (int) (width - rad * 5), (int) AnimationParameters.depthLen);

    }

    /**
     * Recursively draws Nodes in a tree. Does so by drawing the vectors
     * between currNode and its children, then drawing currNode, then
     * drawing currNode's children.
     *
     * If the Node has key Integer.MIN_VALUE, it will not be rendered. This is
     * because RedBlackTree uses a special unseen Node which carries that value.
     *
     * @param currNode the current Node to be drawn.
     */
    protected void drawTreeRecursive(Node currNode, Canvas canvas) {

        // Returns if currNode is null.
        if (currNode == null) return;

        // Draws vectors between this Node and all child Nodes.
        Paint colour = new Paint();
        colour.setStrokeWidth(6);
        colour.setARGB(255, AnimationParameters.VEC_R, AnimationParameters.VEC_G,
                AnimationParameters.VEC_B);
        for (int i = 0; i < getNumChildren(); ++i) {
            if (currNode.children[i] != null && currNode.key != Integer.MIN_VALUE &&
                    currNode.children[i].key != Integer.MIN_VALUE) {
                canvas.drawLine(
                        currNode.position[0], currNode.position[1],
                        currNode.children[i].position[0], currNode.children[i].position[1],
                        colour);

            }
        }

        // Draws the current Node.
        drawNode(currNode, canvas);

        // Draws all child Nodes.
        for (int i = 0; i < getNumChildren(); ++i)
            drawTreeRecursive(currNode.children[i], canvas);

    }

    /**
     * Renders the tree to the inputed canvas, starting at the root.
     * Will also render the nodeList.
     *
     * @param canvas the Canvas to draw in.
     */
    @Override
    public void render(Canvas canvas) {

        // Makes the entire Canvas White.
        canvas.drawRGB(AnimationParameters.BACK_R,
                AnimationParameters.BACK_G, AnimationParameters.BACK_B);

        // Draws the Tree over the Canvas.
        drawTreeRecursive(root, canvas);

        // Draws the nodeList over the Canvas.
        nodeList.render(canvas);

        // Renders this frame to the Canvas.
        super.render(canvas);

    }

    /**
     * Quickly places all nodes and renders the tree.
     * To be used at the end of insertions, deletions, and traversals.
     */
    @Override
    protected void finalRender() {
        finishTraversalAnimation();
        placeTreeNodes();
        placeNodesAtDestination();
        render();

    }

     /**
     * Recursively parses through the tree to fill an ArrayList of nodes.
     *
     * @param currNode the current Node being viewed.
     * @return an ArrayList containing all children in this Node's subtrees.
     */
    private ArrayList<Node> getAllNodesRecursive(Node currNode) {
        ArrayList<Node> nodes = new ArrayList<Node>();

        // Returns an empty arrayList if this Node is null.
        if (currNode == null) return new ArrayList<Node>();

        // Adds all subtrees to nodes.
        for (int i = 0; i < getNumChildren(); ++i)
            nodes.addAll(getAllNodesRecursive(currNode.children[i]));

        // Adds this node to nodes.
        nodes.add(currNode);

        // Returns the ArrayList of Nodes.
        return nodes;

    }

    /**
     * Returns an ArrayList containing all Nodes in this data structure.
     *
     * @return an ArrayList containing all Nodes in this data structure.
     */
    @Override
    public ArrayList<Node> getAllNodes() {
        return getAllNodesRecursive(root);

    }

    /**
     * Performs a depth first traversal to get all of the keys in the order they were upon saving
     *
     * @return an ArrayList containing all keys in this data structure.
     */
    public ArrayList<Integer> getAllKeysOrdered() {
        ArrayList<Integer> keyArrl = new ArrayList<>();
        if(root == null){
            return null;
        }
        Node currNode = root;
        keyArrl.add(currNode.key);
        java.util.LinkedList<Node> queue = new java.util.LinkedList<Node>();


        // Explores Nodes until the queue is empty.
        while (true) {

            // Marks that this Node's children should be explored.
            for (int i = 0; i < getNumChildren(); ++i) {
                if (currNode.children[i] != null) {
                    queue.addLast(currNode.children[i]);
                }
            }

            // Pops the next Node from the queue.
            if (!queue.isEmpty()) {
                currNode = queue.pop();
                keyArrl.add(currNode.key);
            }
            // If the queue is empty, breaks.
            else break;

        }

        return keyArrl;

    }



    /**
     * Returns an ArrayList containing all keys in this data structure.
     *
     * @param dateModified when the file was created
     * @param type what type of data structure is being saved
     * @return a JSONObject to be stored as a JSON file
     */
    public JSONObject createJSON(String dateModified, String type){
        ArrayList<Integer> keyArrl = getAllKeysOrdered();
        if(keyArrl == null || keyArrl.isEmpty()){
            return null;
        }

        //try to create a JSONObject
        try {
            JSONObject jObj = new JSONObject();
            //insert the file name and type
            jObj.put("Date Modified", dateModified);
            jObj.put("Type", type);

            //check to make sure not saving empty file
            if(keyArrl == null || keyArrl.isEmpty()){
                return null;
            }

            //insert JSONArray of the keys
            JSONArray jsArray = new JSONArray(keyArrl);
            jObj.put("Values", jsArray);

            //return the JSONObject
            return jObj;

            //catch the error if JSONObject not made
        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
            // Do something to recover ... or kill the app.
            return null;
        }
    }

    /**
     * Checks whether the key being inserted is a duplicate
     *
     * @param key the value being inserted
     * @return true if key is not a duplicate, false if it is a duplicate
     */
    public boolean checkInsert(int key){

        // Returns false if the key is present.
        for (int curr : getAllKeys()) if(curr == key) return false;
        // Returns true if the key is not found.
        return true;

    }

    /**
     * Recursively parses through the tree to calculate its maximum depth.
     *
     * @param currNode the current Node being viewed.
     * @return the maximum depth of this Node's subtree.
     */
    private int getDepthRecursive(Node currNode) {
        int max = 0;
        int val;

        // Return 0 if this Node is null or if it is invalid.
        if (currNode == null || currNode.key < 0) return 0;

        // Finds the maximum depth of this Node's subtrees.
        for (int i = 0; i < getNumChildren(); ++i) {
            val = getDepthRecursive(currNode.children[i]);
            max = Math.max(max, val);

        }

        // Returns the maximum depth of this Node's subtree plus one.
        return max + 1;

    }

    /**
     * Returns the depth of this tree.
     */
    public int getDepth() {
        return getDepthRecursive(root);

    }
}