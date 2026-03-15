var numDecodings = function (s) {
  let memo = {};

  const recursiveWithMemo = (index, str) => {
    if (memo.hasOwnProperty(index)) {
      return memo[index];
    }
    if (index == str.length) {
      return 1;
    }
    if (str.charAt(index) == "0") {
      return 0;
    }
    if (index == str.length - 1) {
      return 1;
    }

    let ans = recursiveWithMemo(index + 1, str);
    if (parseInt(str.substring(index, index + 2)) <= 26) {
      ans += recursiveWithMemo(index + 2, str);
    }

    return (memo[index] = ans);
  };

  return recursiveWithMemo(0, s, memo);
};
