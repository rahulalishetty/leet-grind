# 458. Poor Pigs

## Problem Statement

There are **buckets** buckets of liquid, where **exactly one bucket is poisonous**.

To determine which bucket is poisonous, you feed some number of pigs the liquid and observe whether they die.

However, you only have **minutesToTest** minutes to determine which bucket is poisonous.

---

## Feeding Rules

You can perform the following steps:

1. **Choose some live pigs to feed.**
2. For each pig, choose which buckets to feed it.
   - The pig will consume all the chosen buckets **simultaneously**.
   - Feeding takes **no time**.
   - A pig can drink from **any number of buckets**.
   - A bucket can be fed to **multiple pigs**.
3. **Wait for `minutesToDie` minutes.**
   - During this time you cannot perform any additional feeding.
4. After `minutesToDie` minutes:
   - Any pig that drank from the **poisonous bucket dies**.
   - All other pigs **survive**.
5. You can repeat the above process until you **run out of testing time**.

---

## Goal

Given:

```
buckets
minutesToDie
minutesToTest
```

Return the **minimum number of pigs** needed to determine **which bucket is poisonous** within the allotted time.

---

## Example 1

### Input

```
buckets = 4
minutesToDie = 15
minutesToTest = 15
```

### Output

```
2
```

### Explanation

At time **0**:

- Feed **Pig 1**: buckets `1` and `2`
- Feed **Pig 2**: buckets `2` and `3`

At time **15**, possible outcomes:

| Pig1  | Pig2  | Poisonous Bucket |
| ----- | ----- | ---------------- |
| Dead  | Alive | Bucket 1         |
| Alive | Dead  | Bucket 3         |
| Dead  | Dead  | Bucket 2         |
| Alive | Alive | Bucket 4         |

Thus **2 pigs** can uniquely identify the bucket.

---

## Example 2

### Input

```
buckets = 4
minutesToDie = 15
minutesToTest = 30
```

### Output

```
2
```

### Explanation

At time **0**:

- Pig1 drinks bucket **1**
- Pig2 drinks bucket **2**

At time **15**:

- If either pig dies → the poisoned bucket is known.
- If both pigs survive → continue testing.

At time **15**:

- Pig1 drinks bucket **3**
- Pig2 drinks bucket **4**

At time **30**:

- One pig must die → identify the poisoned bucket.

---

## Constraints

- `1 <= buckets <= 1000`
- `1 <= minutesToDie <= minutesToTest <= 100`

---
