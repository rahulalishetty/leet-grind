# 1429. First Unique Number

You have a queue of integers, and you need to retrieve the **first unique integer** in the queue.

---

# Implement the `FirstUnique` Class

## Constructor

```
FirstUnique(int[] nums)
```

Initializes the object with the numbers in the queue.

---

## Methods

### `int showFirstUnique()`

Returns the value of the **first unique integer** in the queue.

If there is no such integer, return:

```
-1
```

---

### `void add(int value)`

Insert `value` into the queue.

---

# Example 1

### Input

```
["FirstUnique","showFirstUnique","add","showFirstUnique","add","showFirstUnique","add","showFirstUnique"]
[[[2,3,5]],[],[5],[],[2],[],[3],[]]
```

### Output

```
[null,2,null,2,null,3,null,-1]
```

### Explanation

```
FirstUnique firstUnique = new FirstUnique([2,3,5]);

firstUnique.showFirstUnique(); // return 2

firstUnique.add(5);            // queue = [2,3,5,5]

firstUnique.showFirstUnique(); // return 2

firstUnique.add(2);            // queue = [2,3,5,5,2]

firstUnique.showFirstUnique(); // return 3

firstUnique.add(3);            // queue = [2,3,5,5,2,3]

firstUnique.showFirstUnique(); // return -1
```

---

# Example 2

### Input

```
["FirstUnique","showFirstUnique","add","add","add","add","add","showFirstUnique"]
[[[7,7,7,7,7,7]],[],[7],[3],[3],[7],[17],[]]
```

### Output

```
[null,-1,null,null,null,null,null,17]
```

### Explanation

```
FirstUnique firstUnique = new FirstUnique([7,7,7,7,7,7]);

firstUnique.showFirstUnique(); // return -1

firstUnique.add(7);            // queue = [7,7,7,7,7,7,7]

firstUnique.add(3);            // queue = [7,7,7,7,7,7,7,3]

firstUnique.add(3);            // queue = [7,7,7,7,7,7,7,3,3]

firstUnique.add(7);            // queue = [7,7,7,7,7,7,7,3,3,7]

firstUnique.add(17);           // queue = [7,7,7,7,7,7,7,3,3,7,17]

firstUnique.showFirstUnique(); // return 17
```

---

# Example 3

### Input

```
["FirstUnique","showFirstUnique","add","showFirstUnique"]
[[[809]],[],[809],[]]
```

### Output

```
[null,809,null,-1]
```

### Explanation

```
FirstUnique firstUnique = new FirstUnique([809]);

firstUnique.showFirstUnique(); // return 809

firstUnique.add(809);          // queue = [809,809]

firstUnique.showFirstUnique(); // return -1
```

---

# Constraints

```
1 <= nums.length <= 10^5
1 <= nums[i] <= 10^8
1 <= value <= 10^8
```

- At most **50,000 calls** will be made to `showFirstUnique` and `add`.
