# 616. Add Bold Tag in String — Approach: Mark Bold Characters

## Intuition

First, let's understand the two rules given in the problem description.

1. **If two substrings overlap**, they should be wrapped together with only **one pair of bold tags**.
2. **If two substrings wrapped by bold tags are consecutive**, they should also be **combined into one bold section**.

### Example: Overlapping

```
words = ["aa"]
s = "aaa"
```

The substring `"aa"` appears twice and the occurrences overlap.

Result:

```
<b>aaa</b>
```

### Example: Consecutive

```
words = ["aa", "bb"]
s = "aabb"
```

The matches are adjacent, so they are merged.

Result:

```
<b>aabb</b>
```

---

## Key Idea

If we know **which characters should be bold**, inserting tags becomes simple.

We create a boolean array:

```
bold[i] = true
```

if the character `s[i]` should be inside a bold section.

---

## Step 1: Mark Bold Characters

We create a boolean array `bold` of length `n`.

For each word:

1. Find the first occurrence using a built‑in substring search.
2. Mark the matching characters as bold.
3. Continue searching for the next occurrence.

Example code snippet:

```java
int n = s.length();
boolean[] bold = new boolean[n];

for (String word : words) {
    for (int i = 0; i <= n - word.length(); i++) {
        if (s.substring(i, i + word.length()).equals(word)) {
            for (int j = i; j < i + word.length(); j++) {
                bold[j] = true;
            }
        }
    }
}
```

---

## Using Built‑in Search Functions

To improve efficiency we can use language‑provided substring search methods:

| Language | Method      |
| -------- | ----------- |
| Java     | `indexOf()` |
| Python   | `find()`    |
| C++      | `find()`    |

Example behavior:

```
s = "abcdefg"
s.find("cde") → 2
```

Search starting from a position:

```
s = "aabbaa"
s.find("aa",1) → 4
```

Algorithm for each word:

1. `start = s.indexOf(word)`
2. While `start != -1`
   - Mark characters `[start, start + word.length)`
   - `start = s.indexOf(word, start + 1)`

---

## Step 2: Insert Bold Tags

Once the `bold[]` array is ready, construct the result.

For each index `i`:

Start a bold section if:

```
bold[i] == true AND (i == 0 OR bold[i-1] == false)
```

End a bold section if:

```
bold[i] == true AND (i == n-1 OR bold[i+1] == false)
```

---

## Algorithm

1. Create boolean array `bold[n]`.
2. For each word:
   - Find occurrences using substring search.
   - Mark characters as bold.
3. Build result string:
   - Add `<b>` when a bold segment starts.
   - Add `</b>` when a bold segment ends.
4. Return the constructed string.

---

## Implementation

```java
class Solution {
    public String addBoldTag(String s, String[] words) {

        int n = s.length();
        boolean[] bold = new boolean[n];

        for (String word : words) {

            int start = s.indexOf(word);

            while (start != -1) {

                for (int i = start; i < start + word.length(); i++) {
                    bold[i] = true;
                }

                start = s.indexOf(word, start + 1);
            }
        }

        StringBuilder ans = new StringBuilder();

        for (int i = 0; i < n; i++) {

            if (bold[i] && (i == 0 || !bold[i - 1])) {
                ans.append("<b>");
            }

            ans.append(s.charAt(i));

            if (bold[i] && (i == n - 1 || !bold[i + 1])) {
                ans.append("</b>");
            }
        }

        return ans.toString();
    }
}
```

---

## Complexity Analysis

Let:

- `n = s.length`
- `m = words.length`
- `k = average length of words`

### Time Complexity

Depends on the language implementation of substring search.

For Java `indexOf()`:

```
O(m * (n² * k − n * k²))
```

Worst case example:

```
s = "aaaaa...."
word = "aaaaaa"
```

The search might be executed `O(n − k)` times.

### Space Complexity

```
O(n)
```

We store a boolean array `bold` of size `n`.
