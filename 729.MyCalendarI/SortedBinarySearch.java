class SortedBinarySearch {
  private final TreeMap<Integer, Integer> calendar;

  public MyCalendar() {
    this.calendar = new TreeMap<>();
  }

  // book [start, end), return true if no overlap
  public boolean book(int start, int end) {
    // Check with the event that starts <= start (predecessor)
    Map.Entry<Integer, Integer> prev = calendar.floorEntry(start);
    if (prev != null && prev.getValue() > start) {
      return false; // previous event ends after our start => overlap
    }

    // Check with the next event that starts >= start (successor)
    Map.Entry<Integer, Integer> next = calendar.ceilingEntry(start);
    if (next != null && end > next.getKey()) {
      return false; // our end exceeds next's start => overlap
    }

    calendar.put(start, end);
    return true;
  }
}
