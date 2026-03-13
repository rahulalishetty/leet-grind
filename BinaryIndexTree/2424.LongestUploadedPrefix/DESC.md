# 2424. Longest Uploaded Prefix

You are given a stream of **n videos**, each represented by a distinct number from **1 to n** that you need to **upload to a server**.

You must implement a data structure that can track the **length of the longest uploaded prefix** at any moment.

---

# Definition

We consider **i** to be an uploaded prefix if:

```
all videos from 1 to i have been uploaded
```

The **longest uploaded prefix** is the **maximum i** satisfying this condition.

---

# Class Specification

Implement the class:

```
LUPrefix
```

### Constructor

```
LUPrefix(int n)
```

Initializes the object for a stream of **n videos**.

---

### Method: upload

```
void upload(int video)
```

Uploads the given video to the server.

---

### Method: longest

```
int longest()
```

Returns the **length of the longest uploaded prefix**.

---

# Example

## Input

```
["LUPrefix","upload","longest","upload","longest","upload","longest"]
[[4],[3],[],[1],[],[2],[]]
```

## Output

```
[null,null,0,null,1,null,3]
```

---

# Explanation

```
LUPrefix server = new LUPrefix(4);
```

Initialize stream of **4 videos**.

---

### Step 1

```
server.upload(3)
```

Uploaded videos:

```
{3}
```

---

### Step 2

```
server.longest()
```

Video **1** is missing, so prefix does not exist.

```
Return 0
```

---

### Step 3

```
server.upload(1)
```

Uploaded videos:

```
{1,3}
```

---

### Step 4

```
server.longest()
```

Now prefix:

```
[1]
```

```
Return 1
```

---

### Step 5

```
server.upload(2)
```

Uploaded videos:

```
{1,2,3}
```

---

### Step 6

```
server.longest()
```

Prefix becomes:

```
[1,2,3]
```

```
Return 3
```

---

# Constraints

```
1 <= n <= 10^5
1 <= video <= n
All video values are distinct
At most 2 * 10^5 calls to upload and longest
At least one call will be made to longest
```
