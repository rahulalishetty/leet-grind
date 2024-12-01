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
  let cur = head,
    prev = dummy;

  while (cur && cur.next) {
    const temp = cur.next;
    cur.next = temp.next;
    temp.next = cur;
    prev.next = temp;
    prev = cur;
    cur = cur.next;
  }

  return dummy.next;
};
