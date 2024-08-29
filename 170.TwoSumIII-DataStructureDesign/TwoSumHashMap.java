class TwoSumHashMap {
  private HashMap<Integer, Integer> items;

  public TwoSum() {
    this.items = new HashMap<Integer, Integer>();
  }

  public void add(int number) {
    if (this.items.containsKey(number)) {
      this.items.replace(number, this.items.get(number) + 1);
    } else {
      this.items.put(number, 1);
    }
  }

  public boolean find(int value) {
    for (Map.Entry<Integer, Integer> entry : this.items.entrySet()) {
      int complement = value - entry.getKey();
      if (complement != entry.getKey()) {
        if (this.items.containsKey(complement))
          return true;
      } else {
        if (entry.getValue() > 1)
          return true;
      }
    }

    return false;
  }
}
