var connect = function (root) {
  if (root == null) {
    return root;
  }
  // Initialize a queue data structure which contains
  // just the root of the tree
  let Q = [];
  Q.push(root);
  // Outer while loop which iterates over each level
  while (Q.length > 0) {
    // Note the size of the queue
    let size = Q.length;
    // Iterate over all the nodes on the current level
    for (let i = 0; i < size; i++) {
      // Pop a node from the front of the queue
      let node = Q.shift();
      // This check is important. We don't want to
      // establish any wrong connections. The queue will
      // contain nodes from 2 levels at most at any
      // point in time. This check ensures we only
      // don't establish next pointers beyond the end
      // of a level
      if (i < size - 1) {
        node.next = Q[0];
      }
      // Add the children, if any, to the back of
      // the queue
      if (node.left != null) {
        Q.push(node.left);
      }
      if (node.right != null) {
        Q.push(node.right);
      }
    }
  }
  // Since the tree has now been modified, return the root node
  return root;
};
