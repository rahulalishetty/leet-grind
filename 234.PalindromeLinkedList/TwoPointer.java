import java.util.ArrayList;
import java.util.List;

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

class TwoPointer {
  public boolean isPalindrome(ListNode head) {
    List<Integer> vals = new ArrayList<>();

    // convert LinkedList into ArrayList
    ListNode currNode = head;

    while (currNode != null) {
      vals.add(currNode.val);
      currNode = currNode.next;
    }

    int front = 0;
    int back = vals.size() - 1;

    while (front < back) {
      if (!vals.get(front).equals(vals.get(back))) {
        return false;
      }
      front++;
      back--;
    }

    return true;
  }

  public static void main(String[] args) {
    ListNode head = new ListNode(1, new ListNode(2));

    TwoPointer tp = new TwoPointer();
    System.out.println(tp.isPalindrome(head));

    head = new ListNode(1, new ListNode(2, new ListNode(2, new ListNode(1))));
    System.out.println(tp.isPalindrome(head));
  }
}
