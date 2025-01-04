var largestRectangleArea = function (heights) {
  let stack = [-1];
  let max_area = 0;
  for (let i = 0; i < heights.length; i++) {
    while (
      stack[stack.length - 1] != -1 &&
      heights[stack[stack.length - 1]] >= heights[i]
    ) {
      let current_height = heights[stack.pop()];
      let current_width = i - stack[stack.length - 1] - 1;
      max_area = Math.max(max_area, current_height * current_width);
    }
    stack.push(i);
  }
  while (stack[stack.length - 1] != -1) {
    let current_height = heights[stack.pop()];
    let current_width = heights.length - stack[stack.length - 1] - 1;
    max_area = Math.max(max_area, current_height * current_width);
  }
  return max_area;
};
