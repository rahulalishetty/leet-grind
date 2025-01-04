var largestRectangleArea = function (heights) {
  function calculateArea(start, end) {
    if (start > end) return 0;
    let min_index = start;
    for (let i = start; i <= end; i++)
      if (heights[min_index] > heights[i]) min_index = i;
    return Math.max(
      heights[min_index] * (end - start + 1),
      Math.max(
        calculateArea(start, min_index - 1),
        calculateArea(min_index + 1, end)
      )
    );
  }
  return calculateArea(0, heights.length - 1);
};
