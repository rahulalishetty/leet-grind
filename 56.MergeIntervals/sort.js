/**
 * @param {number[][]} intervals
 * @return {number[][]}
 */
var merge = function (intervals) {
  let final = [];

  intervals.sort((a, b) => a[0] - b[0]);

  for (let i = 0; i < intervals.length; i++) {
    if (final.length && final[final.length - 1][1] >= intervals[i][0]) {
      final[final.length - 1][1] = Math.max(
        intervals[i][1],
        final[final.length - 1][1]
      );
    } else {
      final.push([...intervals[i]]);
    }
  }

  return final;
};
