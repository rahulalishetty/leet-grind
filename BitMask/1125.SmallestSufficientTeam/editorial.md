# 1125. Smallest Sufficient Team

## Overview

In this problem we are given:

- A list of **required skills**
- A list of **people**, where each person has a subset of these skills

Our goal is to select the **smallest team of people** such that every required skill is covered by **at least one member** of the team.

Important constraints:

- `req_skills.length <= 16`
- `people.length <= 60`

The small number of skills strongly suggests using **bitmasking**, since `2^16` states are manageable.

---

# Approach 1: Bottom‑Up Dynamic Programming with Bitmasks

## Intuition

Let:

```
m = number of required skills
n = number of people
```

Since `m ≤ 16`, we can represent any set of skills using a **bitmask**.

Each bit represents whether a skill is present:

```
bit i = 1 → skill i included
bit i = 0 → skill i missing
```

Examples:

```
101111₂ = {0,1,2,3,5}
1001010₂ = {1,3,6}
0 = empty skill set
(2^m − 1) = all skills
```

The goal becomes:

> Find the smallest team such that the OR of their skill masks equals `(2^m − 1)`.

---

## DP Definition

Define:

```
dp[skillsMask]
```

- Represents the **smallest team** that covers skills in `skillsMask`.
- Stored as a **bitmask of people**.

Example:

```
dp[0110] = 00001
```

Means:

- Skills `0110` covered
- Team contains **person 0**

---

## Base Case

```
dp[0] = 0
```

If no skills are required, the team is empty.

---

## Transition

For each skill mask:

```
skillsMask
```

Try adding person `i`.

Let:

```
skillsMaskOfPerson[i]
```

represent the skills of person `i`.

Compute remaining skills:

```
smallerSkillsMask = skillsMask & ~skillsMaskOfPerson[i]
```

If this is different from `skillsMask`, then person `i` contributes new skills.

Candidate team:

```
peopleMask = dp[smallerSkillsMask] | (1 << i)
```

Update `dp[skillsMask]` if the candidate team is smaller.

---

## Algorithm

1. Map each skill to an index.
2. Convert each person's skills into a bitmask.
3. Initialize DP array of size `2^m`.
4. Iterate over all skill masks.
5. Try adding each person.
6. Update DP with the smallest team.

---

## Java Implementation

```java
class Solution {
    public int[] smallestSufficientTeam(String[] req_skills, List<List<String>> people) {
        int n = people.size(), m = req_skills.length;

        HashMap<String, Integer> skillId = new HashMap<>();
        for (int i = 0; i < m; i++)
            skillId.put(req_skills[i], i);

        int[] skillsMaskOfPerson = new int[n];

        for (int i = 0; i < n; i++)
            for (String skill : people.get(i))
                skillsMaskOfPerson[i] |= 1 << skillId.get(skill);

        long[] dp = new long[1 << m];
        Arrays.fill(dp, (1L << n) - 1);
        dp[0] = 0;

        for (int skillsMask = 1; skillsMask < (1 << m); skillsMask++) {
            for (int i = 0; i < n; i++) {
                int smallerSkillsMask = skillsMask & ~skillsMaskOfPerson[i];

                if (smallerSkillsMask != skillsMask) {
                    long peopleMask = dp[smallerSkillsMask] | (1L << i);

                    if (Long.bitCount(peopleMask) < Long.bitCount(dp[skillsMask]))
                        dp[skillsMask] = peopleMask;
                }
            }
        }

        long answerMask = dp[(1 << m) - 1];

        int[] ans = new int[Long.bitCount(answerMask)];
        int idx = 0;

        for (int i = 0; i < n; i++)
            if (((answerMask >> i) & 1) == 1)
                ans[idx++] = i;

        return ans;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(2^m * n)
```

- `2^m` possible skill sets
- For each we iterate through `n` people

### Space Complexity

```
O(2^m)
```

Used for DP table.

---

# Approach 2: Top‑Down Dynamic Programming (Memoization)

## Intuition

Instead of computing DP iteratively, we compute it **recursively** using memoization.

Define function:

```
f(skillsMask)
```

This returns the **smallest team** covering `skillsMask`.

The recurrence is the same as in the bottom‑up approach.

If a result has already been computed, it is returned from the memo table.

---

## Base Case

```
f(0) = empty team
```

---

## Recurrence

For each person:

```
smallerSkillsMask = skillsMask & ~skillsMaskOfPerson[i]
candidateTeam = f(smallerSkillsMask) | (1 << i)
```

Pick the candidate with the **fewest people**.

---

## Java Implementation

```java
class Solution {

    int n;
    int[] skillsMaskOfPerson;
    long[] dp;

    private Long f(int skillsMask) {

        if (skillsMask == 0)
            return 0L;

        if (dp[skillsMask] != -1)
            return dp[skillsMask];

        for (int i = 0; i < n; i++) {

            int smallerSkillsMask = skillsMask & ~skillsMaskOfPerson[i];

            if (smallerSkillsMask != skillsMask) {

                long peopleMask = f(smallerSkillsMask) | (1L << i);

                if (dp[skillsMask] == -1 ||
                    Long.bitCount(peopleMask) < Long.bitCount(dp[skillsMask])) {

                    dp[skillsMask] = peopleMask;
                }
            }
        }

        return dp[skillsMask];
    }

    public int[] smallestSufficientTeam(String[] req_skills, List<List<String>> people) {

        n = people.size();
        int m = req_skills.length;

        HashMap<String, Integer> skillId = new HashMap<>();

        for (int i = 0; i < m; i++)
            skillId.put(req_skills[i], i);

        skillsMaskOfPerson = new int[n];

        for (int i = 0; i < n; i++)
            for (String skill : people.get(i))
                skillsMaskOfPerson[i] |= 1 << skillId.get(skill);

        dp = new long[1 << m];
        Arrays.fill(dp, -1);

        long answerMask = f((1 << m) - 1);

        int[] ans = new int[Long.bitCount(answerMask)];
        int idx = 0;

        for (int i = 0; i < n; i++)
            if (((answerMask >> i) & 1) == 1)
                ans[idx++] = i;

        return ans;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(2^m * n)
```

Each DP state is computed once.

### Space Complexity

```
O(2^m)
```

Used for memoization table.
