# 1032. Stream of Characters

Design an algorithm that accepts a stream of characters and checks if a suffix of these characters is a string of a given array of strings `words`.

For example:

If:

```
words = ["abc", "xyz"]
```

And the stream adds characters one by one:

```
'a', 'x', 'y', 'z'
```

Then the algorithm should detect that the suffix `"xyz"` of the stream `"axyz"` matches `"xyz"` from `words`.

---

## Implement the `StreamChecker` class

### Constructor

```
StreamChecker(String[] words)
```

Initializes the object with the array of words.

### Method

```
boolean query(char letter)
```

- Accepts a new character from the stream.
- Returns `true` if **any non-empty suffix** of the stream forms a word in `words`.
- Otherwise returns `false`.

---

# Example

### Input

```
["StreamChecker", "query", "query", "query", "query", "query", "query", "query", "query", "query", "query", "query", "query"]
[[["cd", "f", "kl"]], ["a"], ["b"], ["c"], ["d"], ["e"], ["f"], ["g"], ["h"], ["i"], ["j"], ["k"], ["l"]]
```

### Output

```
[null, false, false, false, true, false, true, false, false, false, false, false, true]
```

### Explanation

```
StreamChecker streamChecker = new StreamChecker(["cd", "f", "kl"]);

streamChecker.query("a"); // return False
streamChecker.query("b"); // return False
streamChecker.query("c"); // return False
streamChecker.query("d"); // return True, because "cd" is in the word list
streamChecker.query("e"); // return False
streamChecker.query("f"); // return True, because "f" is in the word list
streamChecker.query("g"); // return False
streamChecker.query("h"); // return False
streamChecker.query("i"); // return False
streamChecker.query("j"); // return False
streamChecker.query("k"); // return False
streamChecker.query("l"); // return True, because "kl" is in the word list
```

---

# Constraints

- `1 <= words.length <= 2000`
- `1 <= words[i].length <= 200`
- `words[i]` consists of lowercase English letters.
- `letter` is a lowercase English letter.
- At most `4 * 10^4` calls will be made to `query`.
