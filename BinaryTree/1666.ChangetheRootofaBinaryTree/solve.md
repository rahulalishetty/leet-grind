```
// Definition for a Node.
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node parent;
};
*/

class Solution {
    public Node flipBinaryTree(Node root, Node leaf) {
        return helper(root, leaf, null);
    }
    private Node helper(Node root, Node node, Node new_parent){
        Node old_parent = node.parent;
        node.parent = new_parent;
        if(node.left == new_parent){
            node.left = null;
        }
        if(node.right == new_parent){
            node.right = null;
        }
        if(node == root){
            return node;
        }
        if(node.left != null){
            node.right = node.left;
        }
        node.left = helper(root, old_parent, node);
        return node;
    }
}
```
