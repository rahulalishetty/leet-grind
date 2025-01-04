var isNumber = function (s) {
  // This is the DFA we have designed above
  const dfa = [
    { digit: 1, sign: 2, dot: 3 },
    { digit: 1, dot: 4, exponent: 5 },
    { digit: 1, dot: 3 },
    { digit: 4 },
    { digit: 4, exponent: 5 },
    { sign: 6, digit: 7 },
    { digit: 7 },
    { digit: 7 },
  ];
  let current_state = 0;
  let group;
  for (const c of s) {
    if (c >= "0" && c <= "9") {
      group = "digit";
    } else if (c === "+" || c === "-") {
      group = "sign";
    } else if (c === "e" || c === "E") {
      group = "exponent";
    } else if (c === ".") {
      group = "dot";
    } else {
      return false;
    }
    if (!dfa[current_state][group]) {
      return false;
    }
    current_state = dfa[current_state][group];
  }
  return current_state === 1 || current_state === 4 || current_state === 7;
};
