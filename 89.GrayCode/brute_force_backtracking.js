var grayCode = function (n) {
  const res = [0];
  const seen = new Set(res);
  const helper = (n) => {
    if (res.length === Math.pow(2, n)) {
      return true;
    }

    const curr = res[res.length - 1];
    for (let i = 0; i < n; i++) {
      const next = curr ^ (1 << i);

      if (!seen.has(next)) {
        seen.add(next);
        res.push(next);
        if (helper(n)) return true;
        seen.delete(next);
        res.pop();
      }
    }

    return false;
  };

  helper(n);
  return res;
};
