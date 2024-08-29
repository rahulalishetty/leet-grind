var TwoSum = function () {
  this.items = [];
};

/**
 * @param {number} number
 * @return {void}
 */
TwoSum.prototype.add = function (number) {
  const items = this.items;
  const idx = findUpperBound(items, number);
  items.splice(idx, 0, number);
};

/**
 * @param {number} value
 * @return {boolean}
 */
TwoSum.prototype.find = function (value) {
  const items = this.items;
  let l = 0,
    h = items.length - 1;

  while (l < h) {
    const item = items[l] + items[h];
    if (item === value) {
      return true;
    } else if (item > value) {
      h--;
    } else {
      l++;
    }
  }

  return false;
};

/**
 * @param {list} items
 * @param {number} number
 * @return {int}
 */
function findUpperBound(items, number) {
  let l = 0,
    h = items.length - 1;

  while (l <= h) {
    const mid = Math.floor((h - l) / 2 + l);
    if (items[mid] < number) {
      l = mid + 1;
    } else {
      h = mid - 1;
    }
  }

  return l;
}

/**
 * Your TwoSum object will be instantiated and called as such:
 * var obj = new TwoSum()
 * obj.add(number)
 * var param_2 = obj.find(value)
 */

var obj = new TwoSum();
obj.add(5);
obj.add(1);
obj.add(3);
obj.add(4);
var param_2 = obj.find(7);
console.log(param_2);
