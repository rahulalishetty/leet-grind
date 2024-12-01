/**
 * Definition for singly-linked list.
 * function ListNode(val, next) {
 *     this.val = (val===undefined ? 0 : val)
 *     this.next = (next===undefined ? null : next)
 * }
 */
/**
 * @param {ListNode} head
 * @return {ListNode}
 */
var swapPairs = function (head) {
  const dummy = new ListNode(0, head);

  function recurse(node, prev) {
    if (!node) return;

    if (node.next) {
      const temp = node.next;
      node.next = temp.next;
      temp.next = node;
      prev.next = temp;
      recurse(node.next, node);
    }
  }

  recurse(head, dummy);
  return dummy.next;
};
