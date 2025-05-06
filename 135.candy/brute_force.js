var candy = function (ratings) {
  let candies = new Array(ratings.length).fill(1);
  let hasChanged = true;
  while (hasChanged) {
    hasChanged = false;
    for (let i = 0; i < ratings.length; i++) {
      if (
        i !== ratings.length - 1 &&
        ratings[i] > ratings[i + 1] &&
        candies[i] <= candies[i + 1]
      ) {
        candies[i] = candies[i + 1] + 1;
        hasChanged = true;
      }
      if (
        i > 0 &&
        ratings[i] > ratings[i - 1] &&
        candies[i] <= candies[i - 1]
      ) {
        candies[i] = candies[i - 1] + 1;
        hasChanged = true;
      }
    }
  }
  return candies.reduce((a, b) => a + b, 0);
};
