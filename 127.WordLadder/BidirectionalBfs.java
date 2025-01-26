class BidirectionalBfs {
  public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    if (beginWord == null || endWord == null || beginWord.length() != endWord.length()) {
      return 0;
    }
    Set<String> words = new HashSet(wordList);
    if (!words.contains(endWord)) {
      return 0;
    }
    if (beginWord.equals(endWord)) { // crucial for Solution #2
      return 1;
    }

    Deque<String> deque1 = new ArrayDeque(); // use as queue
    Deque<String> deque2 = new ArrayDeque(); // use as queue

    Set<String> visited1 = new HashSet();
    Set<String> visited2 = new HashSet();

    deque1.add(beginWord);
    deque2.add(endWord);

    visited1.add(beginWord);
    visited2.add(endWord);

    int distance = 2;

    while (!deque1.isEmpty() || !deque2.isEmpty()) {
      if (checkNeighbors(deque1, visited1, visited2, words)) {
        return distance;
      }
      distance++;
      if (checkNeighbors(deque2, visited2, visited1, words)) {
        return distance;
      }
      distance++;
    }
    return 0; // no transformation sequence exists
  }

  private boolean checkNeighbors(Deque<String> deque,
      Set<String> visitedFromThisSide,
      Set<String> visitedFromThatSide,
      Set<String> words) {
    int wordsInLevel = deque.size();
    for (int i = 0; i < wordsInLevel; i++) {
      String word = deque.removeFirst();
      for (String neighbor : getNeighbors(word, words)) {
        if (visitedFromThatSide.contains(neighbor)) {
          return true;
        }
        if (!visitedFromThisSide.contains(neighbor)) {
          visitedFromThisSide.add(neighbor);
          deque.add(neighbor);
        }
      }
    }
    return false;
  }

  // Generates all possible neighbors of given String
  private Set<String> getNeighbors(String str, Set<String> words) {
    Set<String> validWords = new HashSet();
    for (int i = 0; i < str.length(); i++) {
      char[] neighbor = str.toCharArray();
      for (char ch = 'a'; ch <= 'z'; ch++) {
        neighbor[i] = ch;
        String word = new String(neighbor);
        if (words.contains(word)) {
          validWords.add(word);
        }
      }
    }
    validWords.remove(str); // original String is not its own neighbor
    return validWords;
  }
}
