// initially, the cache is empty
let cache = {};
// Internal helper function to calculate the number of ways
function getNum(row, col) {
  // store the position as a tuple, which will be used as a key in the cache
  let rowCol = [row, col].toString();
  // if the key is already in the cache, then return the cached value
  if (cache[rowCol]) {
    return cache[rowCol];
  }
  // if the row or column is 0, or if the row and column are the same, then there is only one way
  if (row == 0 || col == 0 || row == col) {
    cache[rowCol] = 1;
    return 1;
  }
  // otherwise, the number of ways is the sum of the number of ways from the cell above and the cell to the left
  cache[rowCol] = getNum(row - 1, col - 1) + getNum(row - 1, col);
  return cache[rowCol];
}
// Function to return the row of Pascal's Triangle
function getRow(rowIndex) {
  let ans = [];
  for (let i = 0; i <= rowIndex; i++) {
    ans.push(getNum(rowIndex, i));
  }
  return ans;
}
