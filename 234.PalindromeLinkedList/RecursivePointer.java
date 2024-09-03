class ListNode {
  int val;
  ListNode next;

  ListNode() {
  }

  ListNode(int val) {
    this.val = val;
  }

  ListNode(int val, ListNode next) {
    this.val = val;
    this.next = next;
  }
}

class RecursivePointer {
  private ListNode frontPointer;

  private boolean recursiveCheck(ListNode currNode) {
    if (currNode != null) {
      if (!recursiveCheck(currNode.next))
        return false;

      if (currNode.val != frontPointer.val)
        return false;

      frontPointer = frontPointer.next;
    }
    return true;
  }

  public boolean isPalindrome(ListNode head) {
    frontPointer = head;
    return recursiveCheck(head);
  }

  public static void main(String[] args) {
    ListNode head = new ListNode(1, new ListNode(2));

    TwoPointer tp = new TwoPointer();
    System.out.println(tp.isPalindrome(head));

    head = new ListNode(1, new ListNode(2, new ListNode(2, new ListNode(1))));
    System.out.println(tp.isPalindrome(head));
  }
}
