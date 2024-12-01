var swapPairs = function (head) {
  // If the list has no node or has only one node left.
  if (head === null || head.next === null) {
    return head;
  }
  // Nodes to be swapped
  let firstNode = head;
  let secondNode = head.next;
  // Swapping
  firstNode.next = swapPairs(secondNode.next);
  secondNode.next = firstNode;
  // Now the head is the second node
  return secondNode;
};
