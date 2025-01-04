var largestRectangleArea = function (heights) {
  var maxArea = 0;
  var length = heights.length;
  for (var i = 0; i < length; i++) {
    var minHeight = Infinity;
    for (var j = i; j < length; j++) {
      minHeight = Math.min(minHeight, heights[j]);
      maxArea = Math.max(maxArea, minHeight * (j - i + 1));
    }
  }
  return maxArea;
};
