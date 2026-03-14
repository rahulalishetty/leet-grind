# 691. Stickers to Spell Word — Approaches

## Approach 1: Optimized Exhaustive Search

### Intuition

A straightforward solution is to **exhaustively search combinations of stickers**. Since the data is randomized, we can apply several optimizations to speed up the search.

Key optimizations:

1. **Ignore irrelevant letters**
   - For each sticker, ignore letters that **do not appear in the target**.

2. **Pruning**
   - If the current candidate solution **cannot be better than the best solution already found**, stop exploring that branch.

3. **Reach good solutions early**
   - We attempt combinations likely to reach an optimal answer quickly so that pruning becomes more effective.

4. **Remove dominated stickers**
   - Sticker **A dominates B** if:

```
A.count(letter) >= B.count(letter) for every letter
```

If sticker A dominates sticker B, B can be removed from consideration.

---

## Algorithm

### Step 1 — Count letters

For each sticker:

- Build a **frequency array** counting letters present in the target.
- Ignore other letters.

Also compute:

```
targetCount
```

which stores the frequency of characters in the target.

---

### Step 2 — Remove dominated stickers

If one sticker dominates another, remove the dominated one.

Because dominance is **transitive**, we only need to perform the check once.

---

### Step 3 — Exhaustive search

We recursively try combinations of stickers.

Function:

```
search(ans, row)
```

Parameters:

- `ans` → current number of stickers used
- `row` → index of sticker currently being considered

We maintain:

```
best
```

which stores the best solution found so far.

---

### Step 4 — Pruning

Stop recursion if:

```
ans >= best
```

because this branch cannot improve the solution.

---

### Step 5 — Determine maximum usage

For a sticker we compute how many times it can be used:

Example:

```
sticker = abb
target = aaabbbbccccc
```

Maximum usable stickers:

```
max ceil(targetCount[i] / stickerCount[i])
```

for all letters in the sticker.

---

### Step 6 — Explore possibilities

Try using the sticker:

```
used, used-1, used-2 ... 0
```

in descending order to reach optimal solutions faster.

---

## Java Implementation

```java
class Solution {

    int best;
    int[][] stickersCount;
    int[] targetCount;

    public void search(int ans, int row) {

        if (ans >= best) return;

        if (row == stickersCount.length) {
            for (int c : targetCount)
                if (c > 0) return;

            best = ans;
            return;
        }

        int used = 0;

        for (int i = 0; i < stickersCount[row].length; i++) {
            if (targetCount[i] > 0 && stickersCount[row][i] > 0) {
                used = Math.max(
                        used,
                        (targetCount[i] - 1) / stickersCount[row][i] + 1
                );
            }
        }

        for (int i = 0; i < stickersCount[row].length; i++) {
            targetCount[i] -= used * stickersCount[row][i];
        }

        search(ans + used, row + 1);

        while (used > 0) {
            for (int i = 0; i < stickersCount[row].length; i++) {
                targetCount[i] += stickersCount[row][i];
            }
            used--;
            search(ans + used, row + 1);
        }
    }

    public int minStickers(String[] stickers, String target) {

        int[] targetNaiveCount = new int[26];

        for (char c : target.toCharArray())
            targetNaiveCount[c - 'a']++;

        int[] index = new int[26];
        int t = 0;

        for (int i = 0; i < 26; i++) {
            if (targetNaiveCount[i] > 0)
                index[i] = t++;
            else
                index[i] = -1;
        }

        targetCount = new int[t];
        t = 0;

        for (int c : targetNaiveCount)
            if (c > 0)
                targetCount[t++] = c;

        stickersCount = new int[stickers.length][t];

        for (int i = 0; i < stickers.length; i++) {
            for (char c : stickers[i].toCharArray()) {
                int j = index[c - 'a'];
                if (j >= 0) stickersCount[i][j]++;
            }
        }

        int anchor = 0;

        for (int i = 0; i < stickers.length; i++) {
            for (int j = anchor; j < stickers.length; j++)
                if (j != i) {

                    boolean dominated = true;

                    for (int k = 0; k < t; k++) {
                        if (stickersCount[i][k] > stickersCount[j][k]) {
                            dominated = false;
                            break;
                        }
                    }

                    if (dominated) {
                        int[] tmp = stickersCount[i];
                        stickersCount[i] = stickersCount[anchor];
                        stickersCount[anchor++] = tmp;
                        break;
                    }
                }
        }

        best = target.length() + 1;
        search(0, anchor);

        return best <= target.length() ? best : -1;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Let:

```
N = number of stickers
T = number of target letters
```

Worst case:

```
O(N^(T+1) * T^2)
```

Another bound:

```
O( (T−1 choose N+T−1) * T^2 )
```

---

### Space Complexity

```
O(N + T)
```

Used for:

- sticker counts
- target counts
- recursion stack

---

# Approach 2: Dynamic Programming

## Intuition

We represent target completion using **bitmasks**.

Let:

```
dp[state]
```

represent the **minimum number of stickers needed to satisfy characters whose bits are set in `state`**.

If target length is `T`, then:

```
state range = 0 → (1 << T) - 1
```

Final answer:

```
dp[(1 << T) - 1]
```

---

## Algorithm

For each state:

1. Try applying every sticker.
2. For every letter in the sticker:
   - find the first matching character in target not already covered in `state`

3. Mark the character as satisfied:

```
now |= 1 << i
```

4. Update DP:

```
dp[now] = min(dp[now], dp[state] + 1)
```

---

## Java Implementation

```java
class Solution {

    public int minStickers(String[] stickers, String target) {

        int N = target.length();

        int[] dp = new int[1 << N];

        for (int i = 1; i < 1 << N; i++)
            dp[i] = -1;

        for (int state = 0; state < 1 << N; state++) {

            if (dp[state] == -1)
                continue;

            for (String sticker : stickers) {

                int now = state;

                for (char letter : sticker.toCharArray()) {

                    for (int i = 0; i < N; i++) {

                        if (((now >> i) & 1) == 1)
                            continue;

                        if (target.charAt(i) == letter) {
                            now |= 1 << i;
                            break;
                        }
                    }
                }

                if (dp[now] == -1 || dp[now] > dp[state] + 1) {
                    dp[now] = dp[state] + 1;
                }
            }
        }

        return dp[(1 << N) - 1];
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(2^T * S * T)
```

Where:

```
T = target length
S = total characters across all stickers
```

---

### Space Complexity

```
O(2^T)
```

Used for the DP table.
