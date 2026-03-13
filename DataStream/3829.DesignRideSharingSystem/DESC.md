# 3829. Design Ride Sharing System

A ride sharing system manages ride requests from riders and availability from drivers. Riders request rides, and drivers become available over time. The system should match riders and drivers in the order they arrive.

---

# Problem Description

Implement the `RideSharingSystem` class.

## Constructor

```
RideSharingSystem()
```

Initializes the ride sharing system.

---

## Methods

### addRider

```
void addRider(int riderId)
```

Adds a new rider with the given `riderId` to the waiting queue.

---

### addDriver

```
void addDriver(int driverId)
```

Adds a new driver with the given `driverId` to the available drivers queue.

---

### matchDriverWithRider

```
int[] matchDriverWithRider()
```

Matches the **earliest available driver** with the **earliest waiting rider**.

Returns:

```
[driverId, riderId]
```

If no match is available, return:

```
[-1, -1]
```

Both the matched driver and rider are removed from the system.

---

### cancelRider

```
void cancelRider(int riderId)
```

Cancels the ride request of the rider with the given `riderId` **if the rider exists and has not yet been matched**.

---

# Example 1

## Input

```
["RideSharingSystem","addRider","addDriver","addRider","matchDriverWithRider",
"addDriver","cancelRider","matchDriverWithRider","matchDriverWithRider"]

[[],[3],[2],[1],[],[5],[3],[],[]]
```

## Output

```
[null,null,null,null,[2,3],null,null,[5,1],[-1,-1]]
```

## Explanation

```
RideSharingSystem system = new RideSharingSystem();
```

```
system.addRider(3)   → rider queue: [3]
system.addDriver(2)  → driver queue: [2]
system.addRider(1)   → rider queue: [3,1]
```

```
system.matchDriverWithRider()
→ returns [2,3]
```

```
system.addDriver(5) → driver queue: [5]
```

```
system.cancelRider(3)
→ rider 3 already matched, no effect
```

```
system.matchDriverWithRider()
→ returns [5,1]
```

```
system.matchDriverWithRider()
→ returns [-1,-1]
```

---

# Example 2

## Input

```
["RideSharingSystem","addRider","addDriver","addDriver","matchDriverWithRider",
"addRider","cancelRider","matchDriverWithRider"]

[[],[8],[8],[6],[],[2],[2],[]]
```

## Output

```
[null,null,null,null,[8,8],null,null,[-1,-1]]
```

## Explanation

```
RideSharingSystem system = new RideSharingSystem();
```

```
system.addRider(8)  → rider queue: [8]
system.addDriver(8) → driver queue: [8]
system.addDriver(6) → driver queue: [8,6]
```

```
system.matchDriverWithRider()
→ returns [8,8]
```

```
system.addRider(2) → rider queue: [2]
```

```
system.cancelRider(2)
→ rider 2 cancels request
```

```
system.matchDriverWithRider()
→ returns [-1,-1]
```

---

# Constraints

```
1 <= riderId, driverId <= 1000
```

```
Each riderId is unique among riders and is added at most once.
Each driverId is unique among drivers and is added at most once.
```

```
At most 1000 total calls will be made to:
addRider
addDriver
matchDriverWithRider
cancelRider
```
