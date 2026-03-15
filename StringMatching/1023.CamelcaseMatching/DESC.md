# 1023. Camelcase Matching

## Problem

Given an array of strings `queries` and a string `pattern`, return a boolean array `answer` where:

```
answer[i] = true  if queries[i] matches pattern
answer[i] = false otherwise
```

A query word `queries[i]` **matches** `pattern` if you can insert **lowercase English letters** into `pattern` so that it becomes exactly equal to `queries[i]`.

You may:

- Insert characters **at any position**
- Insert **zero or more lowercase characters**

However, **uppercase characters in the query must match the pattern exactly**.

---

## Example 1

**Input**

```
queries = ["FooBar","FooBarTest","FootBall","FrameBuffer","ForceFeedBack"]
pattern = "FB"
```

**Output**

```
[true,false,true,true,false]
```

**Explanation**

```
"FooBar"        -> "F" + "oo" + "B" + "ar"
"FootBall"      -> "F" + "oot" + "B" + "all"
"FrameBuffer"   -> "F" + "rame" + "B" + "uffer"
```

---

## Example 2

**Input**

```
queries = ["FooBar","FooBarTest","FootBall","FrameBuffer","ForceFeedBack"]
pattern = "FoBa"
```

**Output**

```
[true,false,true,false,false]
```

**Explanation**

```
"FooBar"   -> "Fo" + "o" + "Ba" + "r"
"FootBall" -> "Fo" + "ot" + "Ba" + "ll"
```

---

## Example 3

**Input**

```
queries = ["FooBar","FooBarTest","FootBall","FrameBuffer","ForceFeedBack"]
pattern = "FoBaT"
```

**Output**

```
[false,true,false,false,false]
```

**Explanation**

```
"FooBarTest" -> "Fo" + "o" + "Ba" + "r" + "T" + "est"
```

---

## Constraints

- `1 <= pattern.length <= 100`
- `1 <= queries.length <= 100`
- `1 <= queries[i].length <= 100`
- `queries[i]` and `pattern` consist of **English letters**
