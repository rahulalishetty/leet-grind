# 631. Design Excel Sum Formula

Design the basic functionality of Excel and implement the sum formula.

## Class Definition

### Excel Class

- **Constructor**: `Excel(int height, char width)`
  - Initializes the object with the height and width of the sheet.
  - The sheet is an integer matrix `mat` of size `height x width` with:
    - Row index in the range `[1, height]`
    - Column index in the range `['A', width]`
  - All values are initially set to zero.

- **Methods**:
  - `void set(int row, char column, int val)`
    - Changes the value at `mat[row][column]` to `val`.
  - `int get(int row, char column)`
    - Returns the value at `mat[row][column]`.
  - `int sum(int row, char column, List<String> numbers)`
    - Sets the value at `mat[row][column]` to the sum of cells represented by `numbers`.
    - Returns the value at `mat[row][column]`.
    - The sum formula persists until the cell is overwritten by another value or formula.

### Format of `numbers` in `sum` Method

- `"ColRow"`: Represents a single cell.
  - Example: `"F7"` refers to `mat[7]['F']`.
- `"ColRow1:ColRow2"`: Represents a rectangular range of cells.
  - Example: `"B3:F7"` refers to all cells `mat[i][j]` where `3 <= i <= 7` and `'B' <= j <= 'F'`.

**Note**: Circular sum references are not allowed.

---

## Example

### Input

```plaintext
["Excel", "set", "sum", "set", "get"]
[[3, "C"], [1, "A", 2], [3, "C", ["A1", "A1:B2"]], [2, "B", 2], [3, "C"]]
```

### Output

```plaintext
[null, null, 4, null, 6]
```

### Explanation

```java
Excel excel = new Excel(3, "C");
// Construct a 3x3 2D array with all zeros.
//   A B C
// 1 0 0 0
// 2 0 0 0
// 3 0 0 0

excel.set(1, "A", 2);
// Set mat[1]["A"] to 2.
//   A B C
// 1 2 0 0
// 2 0 0 0
// 3 0 0 0

excel.sum(3, "C", ["A1", "A1:B2"]); // Returns 4
// Set mat[3]["C"] to the sum of mat[1]["A"] and the rectangle range mat[1]["A"] to mat[2]["B"].
//   A B C
// 1 2 0 0
// 2 0 0 0
// 3 0 0 4

excel.set(2, "B", 2);
// Set mat[2]["B"] to 2. Update mat[3]["C"] accordingly.
//   A B C
// 1 2 0 0
// 2 0 2 0
// 3 0 0 6

excel.get(3, "C"); // Returns 6
```

---

## Constraints

- `1 <= height <= 26`
- `'A' <= width <= 'Z'`
- `1 <= row <= height`
- `'A' <= column <= width`
- `-100 <= val <= 100`
- `1 <= numbers.length <= 5`
- `numbers[i]` is in the format `"ColRow"` or `"ColRow1:ColRow2"`.
- At most 100 calls will be made to `set`, `get`, and `sum`.
