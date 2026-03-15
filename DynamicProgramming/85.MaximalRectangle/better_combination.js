let maximalRectangle = function (matrix) {
  function leetcode84(heights) {
    let stack = [-1];
    let maxarea = 0;
    for (let i = 0; i < heights.length; ++i) {
      while (
        heights[stack[stack.length - 1]] >= heights[i] &&
        stack[stack.length - 1] != -1
      ) {
        maxarea = Math.max(
          maxarea,
          heights[stack.pop()] * (i - stack[stack.length - 1] - 1)
        );
      }
      stack.push(i);
    }
    while (stack[stack.length - 1] != -1) {
      maxarea = Math.max(
        maxarea,
        heights[stack.pop()] * (heights.length - stack[stack.length - 1] - 1)
      );
    }
    return maxarea;
  }
  if (!matrix.length) return 0;
  let maxarea = 0;
  let dp = Array(matrix[0].length).fill(0);
  for (let i = 0; i < matrix.length; i++) {
    for (let j = 0; j < matrix[0].length; j++) {
      dp[j] = matrix[i][j] == "1" ? dp[j] + 1 : 0;
    }
    maxarea = Math.max(maxarea, leetcode84(dp));
  }
  return maxarea;
};
