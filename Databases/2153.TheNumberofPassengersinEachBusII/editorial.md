# 2153. The Number of Passengers in Each Bus II

## Detailed Summary of Two Accepted Approaches

We need to report, for each bus:

- how many passengers actually boarded that bus

The rules are:

1. a passenger can board a bus only if:

   ```text
   passenger_arrival_time <= bus_arrival_time
   ```

2. a passenger can board at most one bus
3. each bus has a limited capacity
4. if more passengers are waiting than the bus can carry, only up to `capacity` passengers board

This is a chronological simulation problem.

A correct solution must account for **carry-over waiting passengers** from earlier buses.

This summary covers two approaches:

1. **Using MySQL variables**
2. **Using recursion**

---

# Core observation

At the moment a bus arrives, the number of passengers who can board depends on:

- how many passengers have arrived up to that time
- how many have already been picked up by previous buses
- the bus capacity

So for each bus, the boarding count is:

```text
min(bus capacity, passengers waiting at that moment)
```

The difficulty is computing the number of waiting passengers correctly across time.

---

# Example input

## Buses

| bus_id | arrival_time | capacity |
| -----: | -----------: | -------: |
|      1 |            2 |        1 |
|      2 |            4 |       10 |
|      3 |            7 |        2 |

## Passengers

| passenger_id | arrival_time |
| -----------: | -----------: |
|           11 |            1 |
|           12 |            1 |
|           13 |            5 |
|           14 |            6 |
|           15 |            7 |

---

# What happens in the example

## Before Bus 1 arrives at time 2

Passengers waiting:

- 11
- 12

Bus 1 capacity = 1

So Bus 1 boards:

```text
1 passenger
```

Remaining waiting:

```text
1 passenger
```

---

## Before Bus 2 arrives at time 4

No new passengers arrived between time 2 and time 4.

So only the leftover passenger from Bus 1 is waiting.

Bus 2 capacity = 10

So Bus 2 boards:

```text
1 passenger
```

Remaining waiting:

```text
0 passengers
```

---

## Before Bus 3 arrives at time 7

New passengers arriving after Bus 2 and on or before Bus 3:

- 13 at time 5
- 14 at time 6
- 15 at time 7

So 3 passengers are waiting.

Bus 3 capacity = 2

So Bus 3 boards:

```text
2 passengers
```

Remaining waiting:

```text
1 passenger
```

---

## Final output

| bus_id | passengers_cnt |
| -----: | -------------: |
|      1 |              1 |
|      2 |              1 |
|      3 |              2 |

---

# Approach 1: Using Variables

## Core idea

This approach first computes, for each bus:

- how many passengers are eligible to board that bus in total

Then it processes buses in arrival order and uses variables to keep track of:

- how many passengers have already boarded earlier buses
- how many can board the current bus

The key formula is:

```text
boarded on current bus
=
min(capacity, eligible_passengers - already_boarded_before)
```

This works because `eligible_passengers` is cumulative up to the bus arrival time.

So subtracting previously boarded passengers gives the number of still-waiting passengers.

---

## Step 1: Build `OrderedBusArrivals`

```sql
WITH OrderedBusArrivals AS (
  SELECT
    bus_id,
    b.arrival_time,
    capacity,
    COUNT(passenger_id) AS eligible_passengers
  FROM
    Buses b
    LEFT JOIN Passengers p
      ON p.arrival_time <= b.arrival_time
  WHERE
    bus_id IS NOT NULL
  GROUP BY
    bus_id
  ORDER BY
    b.arrival_time
)
```

---

## Why this works

For each bus, this counts all passengers who arrived on or before the bus arrival time.

So it gives cumulative eligibility.

### Example result

| bus_id | arrival_time | capacity | eligible_passengers |
| -----: | -----------: | -------: | ------------------: |
|      1 |            2 |        1 |                   2 |
|      2 |            4 |       10 |                   2 |
|      3 |            7 |        2 |                   5 |

Interpretation:

- Bus 1 can potentially pick from 2 passengers
- Bus 2 can potentially pick from 2 passengers total up to time 4
- Bus 3 can potentially pick from 5 passengers total up to time 7

This is not yet the final boarding count, because some of those passengers may already have taken earlier buses.

---

## Step 2: Use MySQL variables to track boarded totals

The solution initializes two variables:

- `@accumulated_boarding`
- `@boarded_passengers`

Then it processes buses in arrival order.

```sql
SELECT
  bus_id,
  passengers_cnt
FROM
  (
    SELECT
      bus_id,
      capacity,
      eligible_passengers,
      @boarded_passengers := LEAST(
        capacity, eligible_passengers - @accumulated_boarding
      ) AS passengers_cnt,
      @accumulated_boarding := @accumulated_boarding + @boarded_passengers
    FROM
      OrderedBusArrivals,
      (
        SELECT
          @accumulated_boarding := 0,
          @boarded_passengers := 0
      ) AS Initialization
  ) AS FinalResult
ORDER BY
  bus_id;
```

---

## Meaning of the variables

### `@accumulated_boarding`

This stores the total number of passengers already assigned to earlier buses.

### `@boarded_passengers`

This stores the number of passengers assigned to the current bus.

---

## Why the formula works

For the current bus:

```sql
eligible_passengers - @accumulated_boarding
```

gives the number of eligible passengers who are still unboarded.

Then:

```sql
LEAST(capacity, remaining_waiting)
```

ensures the bus takes no more than:

- its own capacity
- the number of remaining waiting passengers

That is the correct boarding rule.

---

## Walkthrough on the sample

From `OrderedBusArrivals`:

| bus_id | arrival_time | capacity | eligible_passengers |
| -----: | -----------: | -------: | ------------------: |
|      1 |            2 |        1 |                   2 |
|      2 |            4 |       10 |                   2 |
|      3 |            7 |        2 |                   5 |

Initialize:

```text
@accumulated_boarding = 0
```

### Bus 1

Remaining waiting:

```text
eligible_passengers - accumulated = 2 - 0 = 2
```

Boarded:

```text
min(1, 2) = 1
```

Update accumulated:

```text
0 + 1 = 1
```

### Bus 2

Remaining waiting:

```text
2 - 1 = 1
```

Boarded:

```text
min(10, 1) = 1
```

Update accumulated:

```text
1 + 1 = 2
```

### Bus 3

Remaining waiting:

```text
5 - 2 = 3
```

Boarded:

```text
min(2, 3) = 2
```

Update accumulated:

```text
2 + 2 = 4
```

Final counts:

| bus_id | passengers_cnt |
| -----: | -------------: |
|      1 |              1 |
|      2 |              1 |
|      3 |              2 |

---

## Final query for Approach 1

```sql
WITH OrderedBusArrivals AS (
  SELECT
    bus_id,
    b.arrival_time,
    capacity,
    COUNT(passenger_id) AS eligible_passengers
  FROM
    Buses b
    LEFT JOIN Passengers p ON p.arrival_time <= b.arrival_time
  WHERE
    bus_id IS NOT NULL
  GROUP BY
    bus_id
  ORDER BY
    b.arrival_time
)
SELECT
  bus_id,
  passengers_cnt
FROM
  (
    SELECT
      bus_id,
      capacity,
      eligible_passengers,
      @boarded_passengers := LEAST(
        capacity, eligible_passengers - @accumulated_boarding
      ) AS passengers_cnt,
      @accumulated_boarding := @accumulated_boarding + @boarded_passengers
    FROM
      OrderedBusArrivals,
      (
        SELECT
          @accumulated_boarding := 0,
          @boarded_passengers := 0
      ) AS Initialization
  ) AS FinalResult
ORDER BY
  bus_id;
```

---

## Strengths of Approach 1

- compact
- efficient
- good for MySQL-specific environments
- models the process using cumulative counts

### Tradeoffs

- depends on MySQL variable behavior
- less portable
- can feel procedural rather than declarative

---

# Approach 2: Using Recursion

## Core idea

Instead of using variables, this approach models the boarding process bus by bus using a recursive CTE.

The structure is:

1. compute each bus’s previous bus arrival time
2. count only the **new passengers** who arrived since the last bus
3. recursively carry forward leftover waiting passengers
4. compute boarded passengers for each bus

This is closer to an explicit simulation.

---

# Step 1: Build `UpdatedBuses`

```sql
WITH RECURSIVE
UpdatedBuses AS (
    SELECT
        B.bus_id,
        B.arrival_time,
        B.capacity,
        COALESCE(LAG(B.arrival_time) OVER (ORDER BY B.arrival_time), 0) AS previous_bus_arrival
    FROM Buses B
)
```

---

## Why this works

For each bus, it stores the arrival time of the previous bus.

That lets us define the interval in which **new passengers** arrived for the current bus.

For the first bus, there is no previous bus, so `COALESCE(..., 0)` sets the previous arrival to `0`.

### Example result

| bus_id | arrival_time | capacity | previous_bus_arrival |
| -----: | -----------: | -------: | -------------------: |
|      1 |            2 |        1 |                    0 |
|      2 |            4 |       10 |                    2 |
|      3 |            7 |        2 |                    4 |

---

# Step 2: Count new passengers between buses

```sql
PassengerArrivalCounts AS (
    SELECT
        B.bus_id,
        B.arrival_time,
        B.capacity,
        B.previous_bus_arrival,
        COUNT(P.passenger_id) AS new_passengers,
        ROW_NUMBER() OVER (ORDER BY B.arrival_time) AS bus_sequence_number
    FROM UpdatedBuses B
    LEFT JOIN Passengers P
        ON P.arrival_time <= B.arrival_time AND P.arrival_time > B.previous_bus_arrival
    GROUP BY B.bus_id, B.arrival_time, B.capacity
)
```

---

## Why this works

This counts only the passengers who arrived:

- after the previous bus
- on or before the current bus

So `new_passengers` is not cumulative.
It is the fresh inflow of passengers since the previous bus.

It also assigns a sequence number to buses based on arrival order, because `bus_id` itself may not reflect the arrival sequence.

### Example result

| bus_id | arrival_time | capacity | previous_bus_arrival | new_passengers | bus_sequence_number |
| -----: | -----------: | -------: | -------------------: | -------------: | ------------------: |
|      1 |            2 |        1 |                    0 |              2 |                   1 |
|      2 |            4 |       10 |                    2 |              0 |                   2 |
|      3 |            7 |        2 |                    4 |              3 |                   3 |

Interpretation:

- Bus 1 sees 2 new passengers
- Bus 2 sees 0 new passengers since Bus 1
- Bus 3 sees 3 new passengers since Bus 2

---

# Step 3: Recursively compute boarded and remaining passengers

```sql
BusBoardingDetails AS (
    SELECT
        bus_sequence_number,
        bus_id,
        LEAST(capacity, new_passengers) AS passengers_boarded,
        (new_passengers - LEAST(capacity, new_passengers)) AS passengers_remaining
    FROM PassengerArrivalCounts
    WHERE bus_sequence_number = 1

    UNION ALL

    SELECT
        PAC.bus_sequence_number,
        PAC.bus_id,
        LEAST(PAC.capacity, PAC.new_passengers + REC.passengers_remaining) AS passengers_boarded,
        (PAC.new_passengers + REC.passengers_remaining) - LEAST(PAC.capacity, PAC.new_passengers + REC.passengers_remaining) AS passengers_remaining
    FROM
        BusBoardingDetails REC,
        PassengerArrivalCounts PAC
    WHERE
        PAC.bus_sequence_number = REC.bus_sequence_number + 1
)
```

---

## Base case: first bus

For the first bus:

- all waiting passengers are just `new_passengers`
- no leftover passengers exist from earlier buses

So:

```sql
LEAST(capacity, new_passengers)
```

gives how many board.

And:

```sql
new_passengers - boarded
```

gives how many remain waiting.

---

## Recursive case: later buses

For each later bus, the total number of people who could board is:

```text
new_passengers + previous_bus_remaining
```

Then boarded passengers are:

```text
min(capacity, total_waiting)
```

and remaining passengers become:

```text
total_waiting - boarded
```

This propagates the waiting queue correctly through the bus sequence.

---

## Walkthrough on the sample

From `PassengerArrivalCounts`:

| seq | bus_id | capacity | new_passengers |
| --: | -----: | -------: | -------------: |
|   1 |      1 |        1 |              2 |
|   2 |      2 |       10 |              0 |
|   3 |      3 |        2 |              3 |

### Bus 1 (base case)

Waiting:

```text
2
```

Boarded:

```text
min(1, 2) = 1
```

Remaining:

```text
2 - 1 = 1
```

### Bus 2 (recursive case)

Total waiting:

```text
new_passengers + previous_remaining = 0 + 1 = 1
```

Boarded:

```text
min(10, 1) = 1
```

Remaining:

```text
1 - 1 = 0
```

### Bus 3 (recursive case)

Total waiting:

```text
3 + 0 = 3
```

Boarded:

```text
min(2, 3) = 2
```

Remaining:

```text
3 - 2 = 1
```

Final boarding counts:

| bus_id | passengers_cnt |
| -----: | -------------: |
|      1 |              1 |
|      2 |              1 |
|      3 |              2 |

---

## Final query for Approach 2

```sql
WITH RECURSIVE

    UpdatedBuses AS (
        SELECT
            B.bus_id,
            B.arrival_time,
            B.capacity,
            COALESCE(LAG(B.arrival_time) OVER (ORDER BY B.arrival_time), 0) AS previous_bus_arrival
        FROM Buses B
    ),

    PassengerArrivalCounts AS (
        SELECT
            B.bus_id,
            B.arrival_time,
            B.capacity,
            B.previous_bus_arrival,
            COUNT(P.passenger_id) AS new_passengers,
            ROW_NUMBER() OVER (ORDER BY B.arrival_time) AS bus_sequence_number
        FROM UpdatedBuses B
        LEFT JOIN Passengers P
            ON P.arrival_time <= B.arrival_time AND P.arrival_time > B.previous_bus_arrival
        GROUP BY B.bus_id, B.arrival_time, B.capacity
    ),

    BusBoardingDetails AS (
        SELECT
            bus_sequence_number,
            bus_id,
            LEAST(capacity, new_passengers) AS passengers_boarded,
            (new_passengers - LEAST(capacity, new_passengers)) AS passengers_remaining
        FROM PassengerArrivalCounts
        WHERE bus_sequence_number = 1

        UNION ALL

        SELECT
            PAC.bus_sequence_number,
            PAC.bus_id,
            LEAST(PAC.capacity, PAC.new_passengers + REC.passengers_remaining) AS passengers_boarded,
            (PAC.new_passengers + REC.passengers_remaining) - LEAST(PAC.capacity, PAC.new_passengers + REC.passengers_remaining) AS passengers_remaining
        FROM
            BusBoardingDetails REC,
            PassengerArrivalCounts PAC
        WHERE
            PAC.bus_sequence_number = REC.bus_sequence_number + 1
    )

SELECT
    bus_id,
    passengers_boarded AS passengers_cnt
FROM BusBoardingDetails
ORDER BY bus_id;
```

---

## Strengths of Approach 2

- more declarative than variables
- explicitly models leftovers from previous buses
- easier to reason about as a true simulation
- portable to SQL dialects with recursive CTE support

### Tradeoffs

- longer
- more complex
- recursion may be less familiar to some readers

---

# Comparing the two approaches

## Approach 1: Variables

### Best when

- using MySQL specifically
- you want a compact cumulative-count solution

### Pros

- concise
- efficient
- elegant once variable flow is understood

### Cons

- MySQL-specific
- less declarative
- harder to maintain in some environments

---

## Approach 2: Recursion

### Best when

- you want a clearer simulation model
- you prefer recursive state propagation
- portability matters more than compactness

### Pros

- explicitly models waiting passengers
- easier to explain as a bus-by-bus process

### Cons

- more verbose
- more moving parts

---

# Important SQL concepts used here

## 1. `LEAST()`

Used to cap boarded passengers by bus capacity.

## 2. `LAG()`

Used to identify the previous bus arrival time.

## 3. `ROW_NUMBER()`

Used to create a stable bus processing order.

## 4. Recursive CTE

Used to propagate leftover waiting passengers across buses.

## 5. MySQL variables

Used in Approach 1 to maintain cumulative boarding state.

---

# Key takeaways

1. This is a chronological queue simulation problem.
2. For each bus, boarded passengers are:
   - `min(capacity, waiting_passengers)`
3. Approach 1 uses cumulative eligible counts and MySQL variables.
4. Approach 2 uses interval-based new passenger counts and recursion.
5. Both approaches correctly account for passengers left behind by earlier buses.

---

## Final accepted implementations

### Approach 1: Using Variables

```sql
WITH OrderedBusArrivals AS (
  SELECT
    bus_id,
    b.arrival_time,
    capacity,
    COUNT(passenger_id) AS eligible_passengers
  FROM
    Buses b
    LEFT JOIN Passengers p ON p.arrival_time <= b.arrival_time
  WHERE
    bus_id IS NOT NULL
  GROUP BY
    bus_id
  ORDER BY
    b.arrival_time
)
SELECT
  bus_id,
  passengers_cnt
FROM
  (
    SELECT
      bus_id,
      capacity,
      eligible_passengers,
      @boarded_passengers := LEAST(
        capacity, eligible_passengers - @accumulated_boarding
      ) AS passengers_cnt,
      @accumulated_boarding := @accumulated_boarding + @boarded_passengers
    FROM
      OrderedBusArrivals,
      (
        SELECT
          @accumulated_boarding := 0,
          @boarded_passengers := 0
      ) AS Initialization
  ) AS FinalResult
ORDER BY
  bus_id;
```

### Approach 2: Using Recursion

```sql
WITH RECURSIVE

    UpdatedBuses AS (
        SELECT
            B.bus_id,
            B.arrival_time,
            B.capacity,
            COALESCE(LAG(B.arrival_time) OVER (ORDER BY B.arrival_time), 0) AS previous_bus_arrival
        FROM Buses B
    ),

    PassengerArrivalCounts AS (
        SELECT
            B.bus_id,
            B.arrival_time,
            B.capacity,
            B.previous_bus_arrival,
            COUNT(P.passenger_id) AS new_passengers,
            ROW_NUMBER() OVER (ORDER BY B.arrival_time) AS bus_sequence_number
        FROM UpdatedBuses B
        LEFT JOIN Passengers P
            ON P.arrival_time <= B.arrival_time AND P.arrival_time > B.previous_bus_arrival
        GROUP BY B.bus_id, B.arrival_time, B.capacity
    ),

    BusBoardingDetails AS (
        SELECT
            bus_sequence_number,
            bus_id,
            LEAST(capacity, new_passengers) AS passengers_boarded,
            (new_passengers - LEAST(capacity, new_passengers)) AS passengers_remaining
        FROM PassengerArrivalCounts
        WHERE bus_sequence_number = 1

        UNION ALL

        SELECT
            PAC.bus_sequence_number,
            PAC.bus_id,
            LEAST(PAC.capacity, PAC.new_passengers + REC.passengers_remaining) AS passengers_boarded,
            (PAC.new_passengers + REC.passengers_remaining) - LEAST(PAC.capacity, PAC.new_passengers + REC.passengers_remaining) AS passengers_remaining
        FROM
            BusBoardingDetails REC,
            PassengerArrivalCounts PAC
        WHERE
            PAC.bus_sequence_number = REC.bus_sequence_number + 1
    )

SELECT
    bus_id,
    passengers_boarded AS passengers_cnt
FROM BusBoardingDetails
ORDER BY bus_id;
```
