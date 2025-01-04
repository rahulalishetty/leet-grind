/**
 * @param {number} n
 * @return {string[][]}
 */
var solveNQueens = function (n) {
  let res = [],
    board = Array(n)
      .fill()
      .map(() => Array(n).fill("."));
  const dir = [
    [0, 1],
    [1, 0],
    [0, -1],
    [-1, 0],
    [1, 1],
    [-1, -1],
    [1, -1],
    [-1, 1],
  ];

  function isInside(x, y) {
    return x >= 0 && x < n && y >= 0 && y < n;
  }

  function isConflicted(x, y) {
    let dx = x,
      dy = y;

    for (let i = 0; i < dir.length; i++) {
      (dx = x), (dy = y);
      while (isInside((dx = dx + dir[i][0]), (dy = dy + dir[i][1]))) {
        if (board[dx][dy] == "Q") return true;
      }
    }

    return false;
  }

  function findPosition(i) {
    if (i == n) return true;

    let found = false;

    for (let dx = 0; dx < n; dx++) {
      if (!isConflicted(i, dx)) {
        board[i][dx] = "Q";
        found = findPosition(i + 1, dx);
        if (found) {
          res.push(board.map((row) => row.join("")));
        }
        board[i][dx] = ".";
      }
    }

    return false;
  }

  findPosition(0, 0);
  return res;
};
