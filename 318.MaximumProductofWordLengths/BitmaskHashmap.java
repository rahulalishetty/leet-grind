class BitmaskHashmap {
  public int bitNumber(char ch) {
    return (int) ch - (int) 'a';
  }

  public int maxProduct(String[] words) {
    Map<Integer, Integer> hashmap = new HashMap();

    int bitmask = 0, bitNum = 0;
    for (String word : words) {
      bitmask = 0;
      for (char ch : word.toCharArray()) {
        // add bit number bitNumber in bitmask
        bitmask |= 1 << bitNumber(ch);
      }
      // there could be different words with the same bitmask
      // ex. ab and aabb
      hashmap.put(bitmask, Math.max(hashmap.getOrDefault(bitmask, 0), word.length()));
    }

    int maxProd = 0;
    for (int x : hashmap.keySet())
      for (int y : hashmap.keySet())
        if ((x & y) == 0)
          maxProd = Math.max(maxProd, hashmap.get(x) * hashmap.get(y));

    return maxProd;
  }
}
