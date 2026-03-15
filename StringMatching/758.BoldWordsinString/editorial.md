# 758. Bold Words in String — Approach #1: Brute Force

## Intuition

The goal is to determine **which letters in the string should be bold**.

Once we know which characters must be bold, generating the final string is straightforward:

- We wrap **continuous groups of bold characters** inside `<b>` and `</b>` tags.

To determine bold characters, we:

1. Check **all occurrences** of every word in `words`.
2. Mark the corresponding characters in `s` as bold.

---

## Key Idea

We maintain a boolean array:

```
mask[i] = true
```

if the character `s[i]` must be bold.

We scan through the string and check if any word in `words` starts at index `i`.

If it does, we mark all characters of that match as bold.

---

## Algorithm

### Step 1: Build Bold Mask

1. Initialize a boolean array `mask` of length `N = s.length`.
2. For each position `i` in `s`:
   - For each word in `words`:
     - Check if the word matches starting at `i`.
     - If yes, mark the corresponding indices in `mask` as `true`.

---

### Step 2: Construct Result String

While building the output:

A character `i` is the **start of a bold section** if:

```
mask[i] == true AND (i == 0 OR mask[i-1] == false)
```

A character `i` is the **end of a bold section** if:

```
mask[i] == true AND (i == N-1 OR mask[i+1] == false)
```

We insert `<b>` and `</b>` accordingly.

---

## Implementation

```java
class Solution {

    public String boldWords(String[] words, String S) {

        int N = S.length();

        boolean[] mask = new boolean[N];

        for (int i = 0; i < N; ++i)

            for (String word : words) search: {

                for (int k = 0; k < word.length(); ++k)

                    if (k + i >= S.length() || S.charAt(k + i) != word.charAt(k))

                        break search;

                for (int j = i; j < i + word.length(); ++j)

                    mask[j] = true;

            }

        StringBuilder ans = new StringBuilder();

        for (int i = 0; i < N; ++i) {

            if (mask[i] && (i == 0 || !mask[i - 1]))

                ans.append("<b>");

            ans.append(S.charAt(i));

            if (mask[i] && (i == N - 1 || !mask[i + 1]))

                ans.append("</b>");

        }

        return ans.toString();

    }

    public boolean match(String S, int i, int j, String T) {

        for (int k = i; k < j; ++k)

            if (k >= S.length() || S.charAt(k) != T.charAt(k - i))

                return false;

        return true;

    }

}
```

---

## Complexity Analysis

Let:

- `N` = length of string `S`
- `wi` = length of each word

### Time Complexity

```
O(N * Σ wi)
```

For each position `i`, we compare against every word.

---

### Space Complexity

```
O(N)
```

We use a boolean array `mask` of size `N`.
