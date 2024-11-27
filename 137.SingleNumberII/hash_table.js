var singleNumber = function (nums) {
  let freq = {};
  for (let num of nums) {
    if (freq[num] === undefined) {
      freq[num] = 1;
    } else {
      freq[num]++;
    }
  }

  let loner = 0;
  for (let key in freq) {
    if (freq[key] === 1) {
      loner = key;
      break;
    }
  }

  return loner;
};
