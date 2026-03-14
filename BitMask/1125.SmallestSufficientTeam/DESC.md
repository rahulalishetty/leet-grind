# 1125. Smallest Sufficient Team

## Problem Description

In a project, you are given:

- A list of required skills `req_skills`
- A list of people `people`

Each person has a subset of skills.

```
people[i] -> list of skills that person i has
```

A **sufficient team** is a set of people such that **every required skill appears in the team at least once**.

The team is represented by the **indices of the selected people**.

Return **any sufficient team with the smallest possible number of people**.

---

## Example 1

### Input

```
req_skills = ["java","nodejs","reactjs"]

people =
[
 ["java"],
 ["nodejs"],
 ["nodejs","reactjs"]
]
```

### Output

```
[0,2]
```

### Explanation

Person `0` provides:

```
java
```

Person `2` provides:

```
nodejs, reactjs
```

Together they cover all required skills.

---

## Example 2

### Input

```
req_skills =
["algorithms","math","java","reactjs","csharp","aws"]

people =
[
 ["algorithms","math","java"],
 ["algorithms","math","reactjs"],
 ["java","csharp","aws"],
 ["reactjs","csharp"],
 ["csharp","math"],
 ["aws","java"]
]
```

### Output

```
[1,2]
```

---

## Constraints

```
1 <= req_skills.length <= 16
```

```
1 <= req_skills[i].length <= 16
```

```
req_skills[i] consists of lowercase English letters
```

```
All strings in req_skills are unique
```

```
1 <= people.length <= 60
```

```
0 <= people[i].length <= 16
```

```
1 <= people[i][j].length <= 16
```

```
people[i][j] consists of lowercase English letters
```

```
All strings in people[i] are unique
```

```
Every skill in people[i] exists in req_skills
```

```
It is guaranteed that a sufficient team exists
```

---

## Notes

- Each skill must appear **at least once** in the selected team.
- The goal is to **minimize the number of people**.
- The returned team **can be in any order**.
