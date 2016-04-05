package mil.army.usace.ehlschlaeger.rgik.util;

import java.util.Stack;



/**
 * One node within a tree.  Has a left child, right child, and its
 * payload is an instance of TreeObject.
 */
class TreeNode {
    // package access members
    TreeNode   left, right; // left and right nodes
    TreeObject data;       // data item

    /**
     * Create new leaf node with given payload and no children.
     * @param d payload object
     */
    public TreeNode(TreeObject d) {
        data = d;
        left = right = null; // this node has no children
    }

    /**
     * Insert a TreeNode into a Tree that contains nodes. Don't insert if key is
     * identical.
     */
    public synchronized void insertIfDifferentKey(TreeObject d) {
        if (data.compare(d) < 0) {
            if (left == null)
                left = new TreeNode(d);
            else
                left.insert(d);
        } else if (data.compare(d) > 0) {
            if (right == null)
                right = new TreeNode(d);
            else
                right.insert(d);
        }
    }

    /**
     * Insert a TreeNode into a Tree that contains nodes. Insert if key is
     * identical. New object will be inserted AFTER old object with same key.
     */
    public synchronized void insert(TreeObject d) {
        TreeNode doNext = this;
        while (doNext != null) {
            if (doNext.data.compare(d) < 0) {
                if (doNext.left == null) {
                    doNext.left = new TreeNode(d);
                    doNext = null;
                } else {
                    doNext = doNext.left;
                    // System.out.print( "L");
                }
            } else {
                if (doNext.right == null) {
                    doNext.right = new TreeNode(d);
                    doNext = null;
                    // System.out.println( " ADD RIGHT");
                } else {
                    doNext = doNext.right;
                    // System.out.print( "R");
                }
            }
        }
        // System.out.println("");
    }
}



/**
 * Binary tree.  Keeps TreeObjects in order according to its
 * <code>compare</code> method.  All "inorder" methods use
 * this ordering; "preorder" and "postorder" do not.
 */
public class Tree {
    private TreeNode root;

    /**
     * Create new empty tree.
     */
    public Tree() {
        root = null;
    }

    /**
     * Insert a new node in the binary search tree.
     * If the tree has no root, given payload will be inserted as root.
     * Otherwise the data will be inserted in an appropriate location.
     * 
     * @param d payload to add to tree
     */
    public synchronized void insertNode(TreeObject d) {
        if (root == null)
            root = new TreeNode(d);
        else
            root.insert(d);
    }

    /**
     * Print all payloads using pre-order traversal (prints node, 
     * then all left children, then all right children.)
     */
    // Preorder Traversal
    public synchronized void preorderTraversal() {
        preorderHelper(root);
    }

    // Recursive method to perform preorder traversal
    private void preorderHelper(TreeNode node) {
        if (node == null)
            return;

        System.out.print(node.data.toString() + " ");
        preorderHelper(node.left);
        preorderHelper(node.right);
    }

    /**
     * Remove and return lowest-valued object from tree.
     * @return leftmost payload, or null if tree is empty
     */
    public synchronized TreeObject removeFirstInOrderNode() {
        if (root == null)
            return null;
        if (root.left == null) {
            TreeNode node = root;
            root = node.right;
            node.right = null;
            return node.data;
        }
        TreeNode node = root;
        TreeNode parent = root;
        while (node.left != null) {
            TreeNode leftNode = node.left;
            if (leftNode.left == null && leftNode.right == null) {
                node.left = null;
                node = leftNode;
            } else {
                parent = node;
                node = node.left;
            }
        }
        if (node.right != null) {
            parent.left = node.right;
        }
        return node.data;
    }

    /**
     * Print all payloads using in-order traversal
     * (prints all left children, then node, then all right children.)
     */
    public synchronized void inorderTraversal() {
        inorderHelper(root);
    }

    // Recursive method to perform inorder traversal
    private void inorderHelper(TreeNode node) {
        if (node == null)
            return;

        inorderHelper(node.left);
        System.out.print(node.data.toString() + " ");
        inorderHelper(node.right);
    }

    /**
     * Find lowest-valued object in tree.
     * @return leftmost payload, or null if tree is empty
     */
    public synchronized TreeObject inorderFirst() {
        if (root == null)
            return null;
        return inorderFirstHelper(root);
    }

    // Recursive method to perform inorder traversal
    // private TreeObject inorderFirstHelper( TreeNode node, TreeObject
    // minObject ) {
    private TreeObject inorderFirstHelper(TreeNode node) {
        if (node.left == null)
            return node.data;
        return inorderFirstHelper(node.left);
    }

    // There are three cases: 1) node is last node. If so, stack's last has no
    // right
    // child and all stack nodes are the right children back to the root node.
    // 2) node has right child node making next node to be the leftmost child
    // node
    // of right child. 3) node has no right child node. If so, go up stack until
    // the child is a parent node's left child.
    /**
     * This method returns the TreeObject next in sequence. Returns null if at
     * largest key in Tree. WARNING: This method is extremely slow. For large
     * datasets, it is better to use inorderFirst() and
     * removerFirstInOrderNode() together. (Of course, you destroy the Tree in
     * the process.)
     */
    public synchronized TreeObject inorderNext(TreeObject t) {
        // Find TreeObject t in Tree
        Stack<TreeNode> tNodes = new Stack<TreeNode>();
        boolean found = inorderNextFindHelper(t, tNodes, root);
        if (found == false)
            return null;

        TreeNode topStack = tNodes.pop();
        // check for case 2
        if (topStack.right != null) {
            // System.out.print( "f");
            TreeNode goingLeft = topStack.right;
            while (goingLeft.left != null) {
                // System.out.print( "e");
                goingLeft = goingLeft.left;
            }
            // System.out.print( "d");
            return (goingLeft.data);
        }
        
        // go up the stack to find a node that is the left child node (case
        // 3)
        while (tNodes.isEmpty() != true) {
            // System.out.print( "c");
            TreeNode parentNode = tNodes.pop();
            if (parentNode.left == topStack) {
                // System.out.print( "b");
                return (parentNode.data);
            }
            topStack = parentNode;
        }
        
        // if existing while loop, we are at the root node (case 1)
        // System.out.print( "a");
        return null;
    }

    /*
     * // Recursive method to perform inorder traversal private boolean
     * inorderNextFindHelper( TreeObject t, Stack tNodes, TreeNode node ) { if(
     * node == null ) { return false; } tNodes.push( node); if(
     * node.data.compare( t) < 0) { return inorderNextFindHelper( t, tNodes,
     * node.left); } else if( node.data.compare( t) > 0) { return
     * inorderNextFindHelper( t, tNodes, node.right); } else if( t != node.data)
     * { return inorderNextFindHelper( t, tNodes, node.right); } return true; }
     */

    // Iterative method to perform inorder traversal
    private boolean inorderNextFindHelper(TreeObject t, Stack<TreeNode> tNodes,
            TreeNode node) {
        if (node == null) {
            return false;
        }
        while (node != null) {
            tNodes.push(node);
            if (node.data.compare(t) < 0) {
                node = node.left;
            } else if (node.data.compare(t) > 0) {
                node = node.right;
            } else if (t != node.data) {
                node = node.right;
            }
        }
        return true;
    }

    /**
     * Print all payloads using post-order traversal
     * (prints all left children, then all right children, then node.)
     */
    public synchronized void postorderTraversal() {
        postorderHelper(root);
    }

    // Recursive method to perform postorder traversal
    private void postorderHelper(TreeNode node) {
        if (node == null)
            return;

        postorderHelper(node.left);
        postorderHelper(node.right);
        System.out.print(node.data.toString() + " ");
    }
}
