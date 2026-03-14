# 731. My Calendar II

Implement a calendar that allows booking events unless the new event would cause any time to be triple-booked.

Each event is a half-open interval [startTime, endTime) with integer endpoints.

Implement the `MyCalendarTwo` class:

- `MyCalendarTwo()` — Initializes the calendar object.
- `boolean book(int startTime, int endTime)` — Returns `true` and adds the event if it does not cause a triple booking; otherwise returns `false` and does not add the event.

Example

Input:
["MyCalendarTwo", "book", "book", "book", "book", "book", "book"]
[[], [10, 20], [50, 60], [10, 40], [5, 15], [5, 10], [25, 55]]

Output:
[null, true, true, true, false, true, true]

Explanation:

- `MyCalendarTwo myCalendarTwo = new MyCalendarTwo();`
- `myCalendarTwo.book(10, 20);` → returns `true`.
- `myCalendarTwo.book(50, 60);` → returns `true`.
- `myCalendarTwo.book(10, 40);` → returns `true` (double booking allowed).
- `myCalendarTwo.book(5, 15);` → returns `false` (would cause a triple booking).
- `myCalendarTwo.book(5, 10);` → returns `true` (does not include time 10).
- `myCalendarTwo.book(25, 55);` → returns `true` (overlaps produce at most double bookings).

Constraints

- `0 <= start < end <= 10^9`
- At most 1000 calls to `book`.
