class TrieNode {
  TrieNode[] children = new TrieNode[2];
}

class TrieManipulation {
  public int findMaximumXOR(int[] nums) {
    int maxNum = nums[0];
    for (int num : nums) {
      maxNum = Math.max(maxNum, num);
    }

    int L = (Integer.toBinaryString(maxNum)).length();
    int maxXor = 0;

    TrieNode root = new TrieNode();
    for (int num : nums) {
      TrieNode node = root, xorNode = root;
      int currXor = 0;

      for (int i = L - 1; i >= 0; i--) {
        int bit = (num >> i) & 1;
        int toggledBit = bit ^ 1;

        if (node.children[bit] == null) {
          TrieNode newNode = new TrieNode();
          node.children[bit] = newNode;
        }
        node = node.children[bit];

        if (xorNode.children[toggledBit] != null) {
          currXor |= (1 << i);
          xorNode = xorNode.children[toggledBit];
        } else {
          xorNode = xorNode.children[bit];
        }
      }
      maxXor = Math.max(maxXor, currXor);
    }

    return maxXor;
  }
}
