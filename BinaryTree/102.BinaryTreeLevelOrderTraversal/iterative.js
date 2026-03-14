var levelOrder = function (root) {
  var levels = [];
  if (root === null) return levels;
  var queue = [root];
  var level = 0;
  while (queue.length !== 0) {
    // Start the current level
    levels.push([]);
    // Number of elements in the current level
    var level_length = queue.length;
    for (var i = 0; i < level_length; i++) {
      var node = queue.shift();
      // fulfill the current level
      levels[level].push(node.val);
      // add child nodes of the current level
      // in the queue for the next level
      if (node.left !== null) queue.push(node.left);
      if (node.right !== null) queue.push(node.right);
    }
    // go to next level
    level++;
  }
  return levels;
};
