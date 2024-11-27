var singleNumber = function (nums) {
  let hash_table = {};
  for (let num of nums) {
    if (hash_table[num]) {
      hash_table[num]++;
    } else {
      hash_table[num] = 1;
    }
  }
  for (let num of nums) {
    if (hash_table[num] == 1) {
      return num;
    }
  }
  return 0;
};
