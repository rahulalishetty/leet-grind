var postorderTraversal = function (root) {
  if (!root) return [];

  const stack = [],
    output = [];
  let cur = root,
    prev = null;

  while (cur || stack.length > 0) {
    if (cur) {
      stack.push(cur);
      cur = cur.left;
    } else {
      cur = stack.slice(-1);

      if (cur.right === null || cur.right === prev) {
        output.push(cur.val);
        stack.pop();
        prev = cur;
        cur = null;
      } else {
        cur = cur.right;
      }
    }
  }

  return output;
};
