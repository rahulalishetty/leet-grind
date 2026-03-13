# Design Browser History — Approaches

## Approach 1: Two Stacks

### Intuition

To store browser navigation history, a **stack** is a natural choice because it follows **Last-In-First-Out (LIFO)** order.

- The **history stack** stores previously visited URLs.
- The **future stack** stores URLs when the user navigates backward.

We also keep a variable:

```
current
```

which represents the current webpage.

### Key Idea

- When visiting a new page:
  - Push `current` into the **history stack**
  - Clear the **future stack**
  - Update `current`

- When going **back**:
  - Move `current` into **future**
  - Pop from **history**

- When going **forward**:
  - Move `current` into **history**
  - Pop from **future**

---

### Implementation

```java
class BrowserHistory {
    private Stack<String> history, future;
    private String current;

    public BrowserHistory(String homepage) {
        history = new Stack<>();
        future = new Stack<>();
        current = homepage;
    }

    public void visit(String url) {
        history.push(current);
        current = url;
        future = new Stack<>();
    }

    public String back(int steps) {
        while(steps > 0 && !history.empty()) {
            future.push(current);
            current = history.pop();
            steps--;
        }
        return current;
    }

    public String forward(int steps) {
        while(steps > 0 && !future.empty()) {
            history.push(current);
            current = future.pop();
            steps--;
        }
        return current;
    }
}
```

### Complexity Analysis

Let:

```
n = number of visit calls
m = maximum steps for navigation
l = length of URL
```

Time Complexity

| Operation | Complexity  |
| --------- | ----------- |
| visit     | O(1)        |
| back      | O(min(m,n)) |
| forward   | O(min(m,n)) |

Space Complexity

```
O(l * n)
```

---

# Approach 2: Doubly Linked List

## Intuition

A **doubly linked list** can represent browser history.

Each node stores:

- URL
- pointer to previous page
- pointer to next page

We maintain:

```
current pointer
```

When visiting a new page, we simply:

- remove forward references
- attach a new node after current

---

### Implementation

```java
class DLLNode {
    public String data;
    public DLLNode prev, next;

    DLLNode(String url) {
        data = url;
        prev = null;
        next = null;
    }
}

class BrowserHistory {

    private DLLNode linkedListHead;
    private DLLNode current;

    public BrowserHistory(String homepage) {
        linkedListHead = new DLLNode(homepage);
        current = linkedListHead;
    }

    public void visit(String url) {
        DLLNode newNode = new DLLNode(url);
        current.next = newNode;
        newNode.prev = current;
        current = newNode;
    }

    public String back(int steps) {
        while (steps > 0 && current.prev != null) {
            current = current.prev;
            steps--;
        }
        return current.data;
    }

    public String forward(int steps) {
        while (steps > 0 && current.next != null) {
            current = current.next;
            steps--;
        }
        return current.data;
    }
}
```

### Complexity Analysis

Time Complexity

| Operation | Complexity  |
| --------- | ----------- |
| visit     | O(l)        |
| back      | O(min(m,n)) |
| forward   | O(min(m,n)) |

Space Complexity

```
O(l * n)
```

---

# Approach 3: Dynamic Array

## Intuition

Instead of stacks or linked lists, we can maintain a **dynamic array** of visited URLs.

We keep two indices:

```
currURL → current page
lastURL → right boundary of history
```

Key idea:

- Moving **back/forward** only updates the pointer.
- Visiting a new URL overwrites forward history.

This approach provides **O(1)** navigation.

---

### Algorithm

Variables:

```
visitedURLs
currURL
lastURL
```

visit(url)

```
currURL++
overwrite or append URL
lastURL = currURL
```

back(steps)

```
currURL = max(0, currURL - steps)
```

forward(steps)

```
currURL = min(lastURL, currURL + steps)
```

---

### Implementation

```java
class BrowserHistory {

    ArrayList<String> visitedURLs;
    int currURL, lastURL;

    public BrowserHistory(String homepage) {

        visitedURLs = new ArrayList<>(Arrays.asList(homepage));
        currURL = 0;
        lastURL = 0;
    }

    public void visit(String url) {

        currURL += 1;

        if (visitedURLs.size() > currURL) {
            visitedURLs.set(currURL, url);
        } else {
            visitedURLs.add(url);
        }

        lastURL = currURL;
    }

    public String back(int steps) {

        currURL = Math.max(0, currURL - steps);
        return visitedURLs.get(currURL);
    }

    public String forward(int steps) {

        currURL = Math.min(lastURL, currURL + steps);
        return visitedURLs.get(currURL);
    }
}
```

### Complexity Analysis

Time Complexity

| Operation | Complexity |
| --------- | ---------- |
| visit     | O(1)       |
| back      | O(1)       |
| forward   | O(1)       |

Space Complexity

```
O(l * n)
```

---

# Key Takeaways

| Approach           | Data Structure | Navigation Time |
| ------------------ | -------------- | --------------- |
| Two Stacks         | Stack          | O(min(m,n))     |
| Doubly Linked List | DLL            | O(min(m,n))     |
| Dynamic Array      | ArrayList      | O(1)            |

The **Dynamic Array approach** is the most optimized because navigation simply adjusts indices.
