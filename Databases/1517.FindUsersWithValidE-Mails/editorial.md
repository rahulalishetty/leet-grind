# 1517. Find Users With Valid E‑Mails — Approach

## Approach: Selecting Rows Based on Conditions (Using Regular Expressions)

### Algorithm

When a problem requires validating whether a **string follows a specific pattern**, the most effective solution is often to use **Regular Expressions (RegEx)**.

Regular expressions allow us to define a pattern that a string must match.

---

# Key RegEx Concepts Used

### Start of String

```
^
```

Represents the **start of the string**.

---

### Character Ranges

```
[a-z]
```

Matches any **lowercase letter**.

```
[A-Z]
```

Matches any **uppercase letter**.

```
[0-9]
```

Matches any **digit**.

Combined example:

```
[a-zA-Z]
```

Matches **any letter**.

---

### Negated Character Range

```
[^a-z]
```

Matches any character **NOT in the range a–z**.

Inside brackets `^` means **negation**, not start.

---

### Repetition Operators

```
[a-z]*
```

Matches characters **0 or more times**.

```
[a-z]+
```

Matches characters **1 or more times**.

---

### Dot Character

```
.
```

Matches **any single character**.

To match a literal dot:

```
\.
```

The backslash **escapes the special meaning** of `.`.

---

### End of String

```
$
```

Represents the **end of the string**.

---

# Constructing the Email Validation Pattern

Valid email rules:

1. Prefix must **start with a letter**
2. Remaining prefix characters may include:
   - letters
   - digits
   - `_`
   - `.`
   - `-`
3. Domain must be **@leetcode.com**

Regex pattern:

```
^[a-zA-Z][a-zA-Z0-9_.-]*\@leetcode\.com$
```

Explanation:

| Part              | Meaning                          |
| ----------------- | -------------------------------- |
| `^`               | Start of string                  |
| `[a-zA-Z]`        | First character must be a letter |
| `[a-zA-Z0-9_.-]*` | Remaining prefix characters      |
| `\@leetcode`      | Domain prefix                    |
| `\.com`           | Domain suffix                    |
| `$`               | End of string                    |

---

# SQL Implementation

```sql
SELECT user_id, name, mail
FROM Users
-- escape @ and . because they have special regex meanings
WHERE mail REGEXP '^[a-zA-Z][a-zA-Z0-9_.-]*\\@leetcode\\.com$';
```

---

# Key Concepts

- **REGEXP** allows pattern matching in SQL.
- `^` and `$` anchor the match to the full string.
- Character ranges define valid characters.
- Escaping (`\`) ensures special characters are interpreted literally.
