# 729. My Calendar I

Design a calendar that supports adding events only if they do not cause a double booking.

- Each event is a half-open interval [startTime, endTime) (startTime <= x < endTime).
- A double booking occurs when two events have a non-empty intersection.

Implement the MyCalendar class:

- `MyCalendar()` — initializes the calendar.
- `boolean book(int startTime, int endTime)` — returns `true` and adds the event if it does not overlap any existing event; otherwise returns `false` and does not add it.

Example

```txt
Input
["MyCalendar", "book", "book", "book"]
[[], [10, 20], [15, 25], [20, 30]]
Output
[null, true, false, true]

Explanation
MyCalendar myCalendar = new MyCalendar();
myCalendar.book(10, 20); // true
myCalendar.book(15, 25); // false — overlaps with [10, 20)
myCalendar.book(20, 30); // true  — does not overlap (20 is not included in the first interval)
```

Constraints

```txt
- 0 <= start < end <= 10^9
- At most 1000 calls to `book`
```
