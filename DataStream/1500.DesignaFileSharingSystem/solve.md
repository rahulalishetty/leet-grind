# 1500. Design a File Sharing System — Exhaustive Java Notes

## Problem Statement

We need to design a file-sharing system for a file split into chunks `1..m`.

The system supports:

- `join(ownedChunks)`: assign the **smallest available positive user ID**, and register the chunks this user owns.
- `leave(userID)`: user leaves, and their ID becomes reusable.
- `request(userID, chunkID)`: return all current users owning `chunkID` in **ascending order**.
  If the returned list is non-empty, then the requester also receives that chunk.

---

## Core Requirements to Notice

There are really **three different subproblems** here:

1. **ID assignment**
   - Always assign the smallest free positive integer.
   - Reuse IDs after `leave`.

2. **Chunk ownership lookup**
   - Given `chunkID`, quickly find all users who currently own it, in sorted order.

3. **Cleanup on leave**
   - When a user leaves, remove them from **every chunk they own**.

That means any good design must maintain both directions:

- `user -> owned chunks`
- `chunk -> owning users`

Without both directions, one operation becomes expensive.

---

# 1. Key Insight

A request does two things:

1. returns the users who currently own a chunk
2. if that list is non-empty, the requester becomes an owner too

So `request()` is not a read-only query.
It is also a state-changing operation.

That is the main detail many people miss.

---

# 2. Data Structures We Need

A clean practical solution uses:

- **Min-heap / priority queue** for reusable IDs
- **nextId** counter for fresh IDs when heap is empty
- **Map<Integer, Set<Integer>> userToChunks**
- **Map<Integer, SortedSet<Integer>> chunkToUsers**

or, since `m` is known in advance, even better:

- `TreeSet<Integer>[] chunkOwners = new TreeSet[m + 1]`

This lets us return users in sorted order directly.

---

# 3. Approach 1 — Naive but Correct

## Idea

Use:

- `Map<Integer, Set<Integer>> userToChunks`
- `Map<Integer, Set<Integer>> chunkToUsers`
- a `Set<Integer>` of active users

For `join`, scan from `1` upward until we find the smallest unused ID.
For `request`, fetch owners from a hash set, convert to list, sort it, and return it.
For `leave`, remove the user from all owned chunks.

This is fully correct, but some operations are slower than necessary.

---

## Why It Works

- We can always find some smallest free ID by checking active users.
- We know exactly what chunks a user owns, so leave cleanup is possible.
- We know exactly who owns a chunk, so request lookup is possible.

The problem is efficiency:

- finding smallest free ID by linear scan is costly
- sorting owners on every request is unnecessary repeated work

---

## Java Code

```java
import java.util.*;

class FileSharingNaive {
    private final int m;
    private final Set<Integer> activeUsers;
    private final Map<Integer, Set<Integer>> userToChunks;
    private final Map<Integer, Set<Integer>> chunkToUsers;

    public FileSharingNaive(int m) {
        this.m = m;
        this.activeUsers = new HashSet<>();
        this.userToChunks = new HashMap<>();
        this.chunkToUsers = new HashMap<>();
    }

    public int join(List<Integer> ownedChunks) {
        int id = 1;
        while (activeUsers.contains(id)) {
            id++;
        }

        activeUsers.add(id);

        Set<Integer> chunks = new HashSet<>();
        for (int chunk : ownedChunks) {
            chunks.add(chunk);
            chunkToUsers.computeIfAbsent(chunk, k -> new HashSet<>()).add(id);
        }
        userToChunks.put(id, chunks);

        return id;
    }

    public void leave(int userID) {
        Set<Integer> chunks = userToChunks.get(userID);
        if (chunks != null) {
            for (int chunk : chunks) {
                Set<Integer> owners = chunkToUsers.get(chunk);
                if (owners != null) {
                    owners.remove(userID);
                    if (owners.isEmpty()) {
                        chunkToUsers.remove(chunk);
                    }
                }
            }
        }

        userToChunks.remove(userID);
        activeUsers.remove(userID);
    }

    public List<Integer> request(int userID, int chunkID) {
        Set<Integer> owners = chunkToUsers.getOrDefault(chunkID, Collections.emptySet());
        List<Integer> ans = new ArrayList<>(owners);
        Collections.sort(ans);

        if (!ans.isEmpty()) {
            Set<Integer> userChunks = userToChunks.get(userID);
            if (userChunks.add(chunkID)) {
                chunkToUsers.computeIfAbsent(chunkID, k -> new HashSet<>()).add(userID);
            }
        }

        return ans;
    }
}
```

---

## Complexity

Let:

- `c = ownedChunks.size()` in `join`
- `u = number of active users`
- `k = number of owners of a chunk`
- `d = number of chunks owned by a leaving user`

### Time

- `join`: `O(u + c)`
- `leave`: `O(d)`
- `request`: `O(k log k)` because we sort every time

### Space

- `O(total ownership relations)`

---

## Drawback

This will pass conceptually, but it is not the best design because:

- smallest-ID allocation is linear
- request sorting is repeated work

We can do better.

---

# 4. Approach 2 — Min Heap for IDs + TreeSet for Sorted Owners

## Idea

Improve the naive solution in two places:

### 1. Reusable IDs

Use:

- `PriorityQueue<Integer> freeIds`
- `int nextId`

When a user leaves, push their ID into `freeIds`.

When a new user joins:

- if `freeIds` is non-empty, pop the smallest reusable ID
- otherwise use `nextId++`

This gives the smallest available ID efficiently.

### 2. Sorted owners per chunk

Instead of `HashSet<Integer>` for owners, use `TreeSet<Integer>`.

Then `request(chunkID)` can return owners already sorted.

This removes the repeated sorting cost.

---

## Why This Is Better

The problem specifically asks for the **smallest available** ID.

That is exactly what a min-heap is good at.

Also, the owners must be returned in ascending order.
A `TreeSet` maintains sorted order at all times, so we avoid sorting during every request.

---

## Java Code

```java
import java.util.*;

class FileSharing {
    private final int m;
    private final PriorityQueue<Integer> freeIds;
    private int nextId;

    // user -> chunks they own
    private final Map<Integer, Set<Integer>> userToChunks;

    // chunk -> sorted owners
    private final Map<Integer, TreeSet<Integer>> chunkToUsers;

    public FileSharing(int m) {
        this.m = m;
        this.freeIds = new PriorityQueue<>();
        this.nextId = 1;
        this.userToChunks = new HashMap<>();
        this.chunkToUsers = new HashMap<>();
    }

    public int join(List<Integer> ownedChunks) {
        int userId = freeIds.isEmpty() ? nextId++ : freeIds.poll();

        Set<Integer> chunks = new HashSet<>();
        for (int chunk : ownedChunks) {
            chunks.add(chunk);
            chunkToUsers.computeIfAbsent(chunk, k -> new TreeSet<>()).add(userId);
        }
        userToChunks.put(userId, chunks);

        return userId;
    }

    public void leave(int userID) {
        Set<Integer> chunks = userToChunks.get(userID);
        if (chunks == null) {
            return;
        }

        for (int chunk : chunks) {
            TreeSet<Integer> owners = chunkToUsers.get(chunk);
            if (owners != null) {
                owners.remove(userID);
                if (owners.isEmpty()) {
                    chunkToUsers.remove(chunk);
                }
            }
        }

        userToChunks.remove(userID);
        freeIds.offer(userID);
    }

    public List<Integer> request(int userID, int chunkID) {
        TreeSet<Integer> owners = chunkToUsers.get(chunkID);
        if (owners == null || owners.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> ans = new ArrayList<>(owners);

        Set<Integer> userChunks = userToChunks.get(userID);
        if (userChunks.add(chunkID)) {
            owners.add(userID);
        }

        return ans;
    }
}
```

---

## Complexity

Assume:

- `c = ownedChunks.size()` in `join`
- `d = number of chunks owned by leaving user`
- `k = number of owners of a chunk`

### Time

- `join`: `O(log U + c log U)`
  - `poll()` from heap is `O(log U)`
  - each insertion into a `TreeSet` is `O(log U)`

- `leave`: `O(d log U)`
  - remove user from each chunk's `TreeSet`

- `request`: `O(k + log U)`
  - building result list from sorted set costs `O(k)`
  - adding requester to `TreeSet` costs `O(log U)`

### Space

- `O(total ownership relations + active users)`

This is already a very strong solution.

---

# 5. Approach 3 — Best Practical Version Using Arrays for Chunks

## Idea

Since `m` is given at construction time and chunk IDs are in `1..m`, we do not actually need a hash map for chunk lookup.

We can use:

```java
TreeSet<Integer>[] chunkOwners = new TreeSet[m + 1];
```

This is cleaner and faster than a map for chunk access.

We still use:

- `PriorityQueue<Integer> freeIds`
- `nextId`
- `Map<Integer, Set<Integer>> userToChunks`

This is the best practical design for this problem.

---

## Why Arrays Help

Chunk IDs are dense integers in a fixed range.

Whenever you have keys like `1..m`, ask yourself:

> Do I really need hashing here?

Usually the answer is no.

Array indexing is simpler and cheaper.

---

## Java Code — Recommended Solution

```java
import java.util.*;

class FileSharing {
    private final PriorityQueue<Integer> freeIds;
    private int nextId;

    // user -> owned chunks
    private final Map<Integer, Set<Integer>> userToChunks;

    // chunk -> sorted users owning it
    private final TreeSet<Integer>[] chunkOwners;

    @SuppressWarnings("unchecked")
    public FileSharing(int m) {
        this.freeIds = new PriorityQueue<>();
        this.nextId = 1;
        this.userToChunks = new HashMap<>();
        this.chunkOwners = new TreeSet[m + 1];

        for (int i = 1; i <= m; i++) {
            chunkOwners[i] = new TreeSet<>();
        }
    }

    public int join(List<Integer> ownedChunks) {
        int userId = freeIds.isEmpty() ? nextId++ : freeIds.poll();

        Set<Integer> chunks = new HashSet<>();
        for (int chunk : ownedChunks) {
            chunks.add(chunk);
            chunkOwners[chunk].add(userId);
        }

        userToChunks.put(userId, chunks);
        return userId;
    }

    public void leave(int userID) {
        Set<Integer> chunks = userToChunks.remove(userID);
        if (chunks == null) {
            return;
        }

        for (int chunk : chunks) {
            chunkOwners[chunk].remove(userID);
        }

        freeIds.offer(userID);
    }

    public List<Integer> request(int userID, int chunkID) {
        TreeSet<Integer> owners = chunkOwners[chunkID];
        List<Integer> ans = new ArrayList<>(owners);

        if (!ans.isEmpty()) {
            Set<Integer> chunks = userToChunks.get(userID);
            if (chunks.add(chunkID)) {
                owners.add(userID);
            }
        }

        return ans;
    }
}
```

---

# 6. Dry Run on the Example

## Input

```text
join([1,2])
join([2,3])
join([4])
request(1,3)
request(2,2)
leave(1)
request(2,1)
leave(2)
join([])
```

---

## Step 1

```text
join([1,2])
```

- `freeIds` empty
- assign `id = 1`

State:

- user 1 -> `{1,2}`
- chunk 1 -> `{1}`
- chunk 2 -> `{1}`

---

## Step 2

```text
join([2,3])
```

- assign `id = 2`

State:

- user 2 -> `{2,3}`
- chunk 2 -> `{1,2}`
- chunk 3 -> `{2}`

---

## Step 3

```text
join([4])
```

- assign `id = 3`

State:

- user 3 -> `{4}`
- chunk 4 -> `{3}`

---

## Step 4

```text
request(1,3)
```

Owners of chunk 3:

```text
[2]
```

Non-empty, so user 1 also receives chunk 3.

Now:

- user 1 -> `{1,2,3}`
- chunk 3 -> `{1,2}`

Return:

```text
[2]
```

---

## Step 5

```text
request(2,2)
```

Owners of chunk 2:

```text
[1,2]
```

User 2 already owns chunk 2, so nothing changes.

Return:

```text
[1,2]
```

---

## Step 6

```text
leave(1)
```

Remove user 1 from all their chunks:

- chunk 1: remove 1
- chunk 2: remove 1
- chunk 3: remove 1

Push `1` into `freeIds`.

---

## Step 7

```text
request(2,1)
```

Owners of chunk 1:

```text
[]
```

Empty, so user 2 does not receive anything.

Return:

```text
[]
```

---

## Step 8

```text
leave(2)
```

Remove user 2 from their chunks and push ID 2 into `freeIds`.

Now free IDs include:

```text
[1,2]
```

---

## Step 9

```text
join([])
```

Pop smallest free ID:

```text
1
```

Return:

```text
1
```

Correct.

---

# 7. Why We Need Both Directions

A natural question is:

> Why not only store `chunk -> users`?

Because `leave(userID)` would become painful.

If we only know which users own a chunk, then to remove a user on leave we would need to scan all chunks and remove them everywhere.

That is wasteful.

Likewise, if we only store `user -> chunks`, then answering `request(chunkID)` would require scanning all users.

So the bidirectional mapping is not optional if we want efficient operations.

---

# 8. Common Mistakes

## Mistake 1: Forgetting that `request()` updates ownership

If a chunk is successfully downloaded, the requester now owns it too.

---

## Mistake 2: Sorting every time when a `TreeSet` can maintain order

If owners must be returned in ascending order repeatedly, maintaining sorted order incrementally is better.

---

## Mistake 3: Forgetting to reclaim IDs on `leave`

The ID must become reusable.

---

## Mistake 4: Adding duplicate chunk ownership

If the requester already owns the chunk, do not add duplicate ownership structures.

This is why `userToChunks` should be a `Set<Integer>`.

---

## Mistake 5: Using only one-direction mapping

Then either `leave()` or `request()` becomes unnecessarily expensive.

---

# 9. Comparison of Approaches

| Approach                  | ID Allocation | Chunk Owners               | Request Order  | Join           | Leave          | Request        | Verdict          |
| ------------------------- | ------------- | -------------------------- | -------------- | -------------- | -------------- | -------------- | ---------------- |
| Naive                     | linear scan   | `HashSet`                  | sort each time | slow           | okay           | slower         | correct but weak |
| Heap + Maps               | min-heap      | `TreeSet`                  | already sorted | good           | good           | good           | strong           |
| Heap + Array of `TreeSet` | min-heap      | direct indexed `TreeSet[]` | already sorted | best practical | best practical | best practical | recommended      |

---

# 10. Final Recommended Intuition

Use:

- a **min-heap** to always recycle the smallest available user ID
- a **set of chunks per user** so we can cleanly remove ownership on leave
- a **sorted set of owners per chunk** so requests are returned in ascending order without extra sorting

This gives a clean and efficient design that matches the problem's natural operations.

---

# 11. Final Recommended Java Solution

```java
import java.util.*;

class FileSharing {
    private final PriorityQueue<Integer> freeIds;
    private int nextId;
    private final Map<Integer, Set<Integer>> userToChunks;
    private final TreeSet<Integer>[] chunkOwners;

    @SuppressWarnings("unchecked")
    public FileSharing(int m) {
        freeIds = new PriorityQueue<>();
        nextId = 1;
        userToChunks = new HashMap<>();
        chunkOwners = new TreeSet[m + 1];
        for (int i = 1; i <= m; i++) {
            chunkOwners[i] = new TreeSet<>();
        }
    }

    public int join(List<Integer> ownedChunks) {
        int userId = freeIds.isEmpty() ? nextId++ : freeIds.poll();

        Set<Integer> chunks = new HashSet<>();
        for (int chunk : ownedChunks) {
            chunks.add(chunk);
            chunkOwners[chunk].add(userId);
        }

        userToChunks.put(userId, chunks);
        return userId;
    }

    public void leave(int userID) {
        Set<Integer> chunks = userToChunks.remove(userID);
        if (chunks == null) {
            return;
        }

        for (int chunk : chunks) {
            chunkOwners[chunk].remove(userID);
        }

        freeIds.offer(userID);
    }

    public List<Integer> request(int userID, int chunkID) {
        List<Integer> owners = new ArrayList<>(chunkOwners[chunkID]);

        if (!owners.isEmpty()) {
            Set<Integer> chunks = userToChunks.get(userID);
            if (chunks.add(chunkID)) {
                chunkOwners[chunkID].add(userID);
            }
        }

        return owners;
    }
}
```

---

# 12. Complexity of Recommended Solution

Let:

- `c = ownedChunks.size()` in `join`
- `d = number of chunks a user owns`
- `k = number of owners of requested chunk`
- `U = number of active users`

## Time

### `join(ownedChunks)`

- get smallest free ID: `O(log U)`
- add ownership for each chunk: `O(c log U)`

Overall:

```text
O(log U + c log U)
```

Since `ownedChunks.length <= 100`, this is very manageable.

---

### `leave(userID)`

If the user owns `d` chunks:

```text
O(d log U)
```

---

### `request(userID, chunkID)`

- copying owners into result list: `O(k)`
- adding requester to owner set if needed: `O(log U)`

Overall:

```text
O(k + log U)
```

---

## Space

We store:

- one chunk set per active user
- one owner set per chunk
- one ownership relation for each `(user, chunk)` pair

Overall:

```text
O(total ownership relations + U + m)
```

---

# 13. Follow-up Discussion

## Follow-up 1: What if users are identified by IP address?

That changes identity semantics.

An IP is not stable as a user identity:

- multiple users can share an IP
- the same user can reconnect with a different IP
- the same IP can disconnect and reconnect later

So IP should not be the primary identity key unless the product definition explicitly says:

> “same IP means same logical user”

In a real design, you would want a stable session/user identifier, not IP.

---

## Follow-up 2: What if users frequently join and leave without requesting chunks?

Yes, the heap-based ID reuse still works efficiently.

- `join` and `leave` remain cheap
- chunk ownership updates are proportional only to the user's owned chunk count

This is good because `ownedChunks.length` is capped at 100.

---

## Follow-up 3: What if everyone eventually requests many chunks?

Then total ownership relations grow a lot, and this design still handles it correctly.

The dominant cost becomes maintaining large owner sets.

That is unavoidable because the system genuinely contains more information.

---

## Follow-up 4: What if there are multiple files?

Then chunk ownership must be keyed by both:

```text
(fileId, chunkId)
```

You could represent this as:

- nested maps:
  - `fileId -> chunkId -> owners`
- or a combined key class:
  - `ChunkKey(fileId, chunkId)`

The same overall design still applies.

---

# 14. Interview-Style Summary

This is a bidirectional relationship problem.

To solve it efficiently:

- maintain **smallest reusable IDs** with a min-heap
- maintain **user -> chunks** for fast cleanup on leave
- maintain **chunk -> sorted owners** for fast request answers

The best practical Java solution uses:

- `PriorityQueue<Integer>` for free IDs
- `Map<Integer, Set<Integer>>` for user chunk ownership
- `TreeSet<Integer>[]` for chunk owners

That yields a clean and efficient implementation with:

```text
join    : O(log U + c log U)
leave   : O(d log U)
request : O(k + log U)
```

and matches the required behavior exactly.
