var connect = function (root) {
  if (!root) {
    return root;
  }
  let leftmost = root;
  let prev = null;
  let curr = null;
  while (leftmost) {
    prev = null;
    curr = leftmost;
    leftmost = null;
    while (curr) {
      let res = processChild(curr.left, prev, leftmost);
      prev = res[0];
      leftmost = res[1];
      res = processChild(curr.right, prev, leftmost);
      prev = res[0];
      leftmost = res[1];
      curr = curr.next;
    }
  }
  return root;
};
var processChild = function (childNode, prev, leftmost) {
  if (childNode) {
    if (prev) {
      prev.next = childNode;
    } else {
      leftmost = childNode;
    }
    prev = childNode;
  }
  return [prev, leftmost];
};
