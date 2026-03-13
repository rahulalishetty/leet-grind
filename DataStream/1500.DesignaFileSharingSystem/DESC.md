# 1500. Design a File Sharing System

We will use a file-sharing system to share a very large file that consists of **m small chunks** with IDs from **1 to m**.

When users join the system, the system should assign a **unique ID** to them.

- The unique ID should be used once for each user.
- When a user leaves the system, the ID **can be reused again**.

Users can request a certain chunk of the file. The system should return a list of IDs of all the users who own this chunk. If the user receives a non-empty list of IDs, they receive the requested chunk successfully.

---

# Implement the `FileSharing` Class

## Constructor

```
FileSharing(int m)
```

Initializes the object with a file of **m chunks**.

---

## Methods

### `int join(int[] ownedChunks)`

A new user joins the system owning some chunks of the file.

The system should:

- Assign an ID to the user.
- The ID must be the **smallest positive integer not currently taken by another user**.

Return the assigned ID.

---

### `void leave(int userID)`

The user with `userID` leaves the system.

After leaving:

- Their chunks are no longer available for sharing.

---

### `int[] request(int userID, int chunkID)`

The user `userID` requests the file chunk `chunkID`.

Return:

- A **sorted list of user IDs** who own this chunk.

If the returned list is **non-empty**, the requesting user successfully receives the chunk.

---

# Example

### Input

```
["FileSharing","join","join","join","request","request","leave","request","leave","join"]
[[4],[[1,2]],[[2,3]],[[4]],[1,3],[2,2],[1],[2,1],[2],[[]]]
```

### Output

```
[null,1,2,3,[2],[1,2],null,[],null,1]
```

---

# Explanation

```
FileSharing fileSharing = new FileSharing(4);
// The file contains 4 chunks
```

```
fileSharing.join([1, 2]);
```

User joins with chunks `[1,2]`.

Assigned ID:

```
1
```

---

```
fileSharing.join([2, 3]);
```

User joins with chunks `[2,3]`.

Assigned ID:

```
2
```

---

```
fileSharing.join([4]);
```

User joins with chunk `[4]`.

Assigned ID:

```
3
```

---

```
fileSharing.request(1, 3);
```

User `1` requests chunk `3`.

Only user `2` owns this chunk.

Return:

```
[2]
```

User `1` now owns chunks `[1,2,3]`.

---

```
fileSharing.request(2, 2);
```

User `2` requests chunk `2`.

Users `[1,2]` have this chunk.

Return:

```
[1,2]
```

---

```
fileSharing.leave(1);
```

User `1` leaves the system.

Their chunks are removed.

---

```
fileSharing.request(2, 1);
```

User `2` requests chunk `1`.

No users own it.

Return:

```
[]
```

---

```
fileSharing.leave(2);
```

User `2` leaves the system.

---

```
fileSharing.join([]);
```

New user joins with **no chunks**.

IDs `1` and `2` are free.

Smallest available ID:

```
1
```

Return:

```
1
```

---

# Constraints

```
1 <= m <= 10^5
0 <= ownedChunks.length <= min(100, m)
1 <= ownedChunks[i] <= m
```

- `ownedChunks` values are **unique**.
- `1 <= chunkID <= m`
- `userID` is guaranteed to be valid if IDs are assigned correctly.
- At most **10⁴ calls** will be made to:

```
join
leave
request
```

- Each `leave` call corresponds to a previous `join`.

---

# Follow-up Questions

### 1. What if users are identified by IP address instead of a unique ID?

Users may disconnect and reconnect with the same IP. The system must handle identity persistence and reconnections carefully.

---

### 2. What if users frequently join and leave without requesting chunks?

The system must efficiently handle frequent **ID allocation and reuse**.

---

### 3. What if all users join once, request all chunks, and then leave?

The design must efficiently support **bulk operations** and chunk ownership tracking.

---

### 4. What if the system shares **multiple files**?

If there are `n` files and the `i`‑th file has `m[i]` chunks, the system must:

- Maintain chunk ownership **per file**
