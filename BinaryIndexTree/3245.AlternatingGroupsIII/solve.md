# Alternating Groups in Circular Tiles — BitSet Approach Notes

## Intuition

The problem involves counting groups of alternating colors in a circular array, where the array can be updated dynamically. The challenge lies in efficiently counting these alternating groups and handling updates without recalculating everything from scratch.

Given the circular nature and the need for efficient updates, we recognize that a straightforward approach could involve excessive recomputation. Instead, we can leverage a BitSet to track transitions between colors and efficiently calculate the number of groups based on these transitions.

## Approach

### Representation of Colors

We first create an adjusted array of colors where each color is XORed with its index's parity (even/odd). This helps in simplifying the problem by turning it into a linear, non-circular problem.

### BitSet for Transition Tracking

We use a BitSet to track the positions where the color changes occur (transitions). This allows us to quickly determine the start and end of alternating groups.

### Frequency Array

We maintain a frequency array that keeps track of the length of these alternating groups. Another BitSet helps us manage which group lengths are currently present.

### Processing Queries

For each query:

- if it is an update operation, we adjust the BitSet and frequency array accordingly
- if it is a query for counting alternating groups of a specific length, we compute the result by summing up the relevant group counts

### Handling Circular Nature

The circular property of the array is handled by creating a virtual doubled array and ensuring that the transition tracking and frequency calculations are appropriately adjusted.

## Complexity

### Time complexity

The initialization takes `O(n)`, where `n` is the length of the array.

Each query, whether an update or a counting operation, takes `O(log n)` in the worst case due to the operations on the BitSet.

### Space complexity

`O(n)`, primarily due to the storage for:

- the adjusted color array
- the BitSet
- the frequency array

## Code

```java
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

class Solution {

    public List<Integer> numberOfAlternatingGroups(int[] colors, int[][] queries) {
        return new AbstractList<Integer>() {

            private List<Integer> resultList;

            private void initialize() {
                resultList = new ArrayList<>();

                int length = colors.length;
                int[] adjustedColors = new int[2 * length];
                for (int i = 0; i < 2 * length; i++) {
                    adjustedColors[i] = colors[i % length] ^ (i % 2 == 0 ? 0 : 1);
                }

                BitSet bitSet = new BitSet(2 * length + 3);
                for (int i = 1; i < 2 * length; i++) {
                    if (adjustedColors[i] != adjustedColors[i - 1]) {
                        bitSet.set(i);
                    }
                }

                int[] frequency = new int[2 * length + 1];
                BitSet frequencySet = new BitSet(2 * length + 1);
                for (int i = 0; i < length; i++) {
                    if (bitSet.get(i)) {
                        int nextIndex = bitSet.next(i + 1);
                        if (nextIndex == -1) {
                            nextIndex = 2 * length;
                        }
                        frequency[nextIndex - i]++;
                        frequencySet.set(nextIndex - i);
                    }
                }

                for (int[] query : queries) {
                    if (query[0] == 1) {
                        if (bitSet.next(0) == -1) {
                            resultList.add(length);
                        } else {
                            int count = 0;
                            for (int i = frequencySet.next(query[1]); i != -1; i = frequencySet.next(i + 1)) {
                                count += (i - query[1] + 1) * frequency[i];
                            }
                            if (adjustedColors[2 * length - 1] != adjustedColors[0]) {
                                int firstIndex = bitSet.next(0);
                                if (firstIndex >= query[1]) {
                                    count += (firstIndex - query[1] + 1);
                                }
                            }

                            resultList.add(count);
                        }
                    } else {
                        int index = query[1];
                        int newValue = query[2];
                        if (colors[index] == newValue) {
                            continue;
                        }
                        colors[index] ^= 1;
                        update(index, bitSet, frequency, length, frequencySet, adjustedColors);
                        update(index + length, bitSet, frequency, length, frequencySet, adjustedColors);
                    }
                }
            }

            private void update(int index, BitSet bitSet, int[] frequency, int length, BitSet frequencySet, int[] adjustedColors) {
                if (index > 0) {
                    int previousIndex = bitSet.prev(index - 1);
                    int nextIndex = bitSet.next(previousIndex + 1);
                    if (nextIndex == -1) {
                        nextIndex = 2 * length;
                    }
                    if (previousIndex != -1 && previousIndex < length && --frequency[nextIndex - previousIndex] == 0) {
                        frequencySet.unset(nextIndex - previousIndex);
                    }
                }
                if (bitSet.get(index)) {
                    int previousIndex = index;
                    int nextIndex = bitSet.next(index + 1);
                    if (nextIndex == -1) {
                        nextIndex = 2 * length;
                    }
                    if (previousIndex != -1 && previousIndex < length && --frequency[nextIndex - previousIndex] == 0) {
                        frequencySet.unset(nextIndex - previousIndex);
                    }
                }
                if (bitSet.get(index + 1)) {
                    int previousIndex = index + 1;
                    int nextIndex = bitSet.next(index + 2);
                    if (nextIndex == -1)
                        nextIndex = 2 * length;
                    if (previousIndex != -1 && previousIndex < length && --frequency[nextIndex - previousIndex] == 0) {
                        frequencySet.unset(nextIndex - previousIndex);
                    }
                }
                bitSet.unset(index);
                bitSet.unset(index + 1);
                adjustedColors[index] ^= 1;
                if (index > 0 && adjustedColors[index] != adjustedColors[index - 1]) {
                    bitSet.set(index);
                }
                if (index + 1 < adjustedColors.length && adjustedColors[index + 1] != adjustedColors[index]) {
                    bitSet.set(index + 1);
                }

                if (index > 0) {
                    int previousIndex = bitSet.prev(index - 1);
                    int nextIndex = bitSet.next(previousIndex + 1);
                    if (nextIndex == -1)
                        nextIndex = 2 * length;
                    if (previousIndex != -1 && previousIndex < length && ++frequency[nextIndex - previousIndex] == 1) {
                        frequencySet.set(nextIndex - previousIndex);
                    }
                }
                if (bitSet.get(index)) {
                    int previousIndex = index;
                    int nextIndex = bitSet.next(index + 1);
                    if (nextIndex == -1)
                        nextIndex = 2 * length;
                    if (previousIndex != -1 && previousIndex < length && ++frequency[nextIndex - previousIndex] == 1) {
                        frequencySet.set(nextIndex - previousIndex);
                    }
                }
                if (bitSet.get(index + 1)) {
                    int previousIndex = index + 1;
                    int nextIndex = bitSet.next(index + 2);
                    if (nextIndex == -1)
                        nextIndex = 2 * length;
                    if (previousIndex != -1 && previousIndex < length && ++frequency[nextIndex - previousIndex] == 1) {
                        frequencySet.set(nextIndex - previousIndex);
                    }
                }
            }

            private void init() {
                if (resultList == null) {
                    initialize();
                    System.gc();
                }
            }

            @Override
            public Integer get(int index) {
                init();
                return resultList.get(index);
            }

            @Override
            public int size() {
                init();
                return resultList.size();
            }

        };

    }

    public static class BitSet {
        private long[][] set;
        private int size;

        public BitSet(int size) {
            this.size = size;
            int depth = 1;
            for (int m = size; m > 1; m >>>= 6, depth++)
                ;

            set = new long[depth][];
            for (int i = 0, m = size >>> 6; i < depth; i++, m >>>= 6) {
                set[i] = new long[m + 1];
            }
        }

        public BitSet setRange(int range) {
            for (int i = 0; i < set.length; i++, range = range + 63 >>> 6) {
                for (int j = 0; j < range >>> 6; j++) {
                    set[i][j] = -1L;
                }
                if ((range & 63) != 0) {
                    set[i][range >>> 6] |= (1L << range) - 1;
                }
            }
            return this;
        }

        public BitSet unsetRange(int range) {
            if (range >= 0) {
                for (int i = 0; i < set.length; i++, range = range + 63 >>> 6) {
                    for (int j = 0; j < range + 63 >>> 6; j++) {
                        set[i][j] = 0;
                    }
                    if ((range & 63) != 0) {
                        set[i][range >>> 6] &= -(1L << range);
                    }
                }
            }
            return this;
        }

        public BitSet set(int position) {
            if (position >= 0 && position < size) {
                for (int i = 0; i < set.length; i++, position >>>= 6) {
                    set[i][position >>> 6] |= 1L << position;
                }
            }
            return this;
        }

        public BitSet unset(int position) {
            if (position >= 0 && position < size) {
                for (int i = 0; i < set.length && (i == 0 || set[i - 1][position] == 0L); i++, position >>>= 6) {
                    set[i][position >>> 6] &= ~(1L << position);
                }
            }
            return this;
        }

        public boolean get(int position) {
            return position >= 0 && position < size && set[0][position >>> 6] << ~position < 0;
        }

        public BitSet toggle(int position) {
            return get(position) ? unset(position) : set(position);
        }

        public int prev(int position) {
            for (int i = 0; i < set.length && position >= 0; i++, position >>>= 6, position--) {
                int previous = prev(set[i][position >>> 6], position & 63);
                if (previous != -1) {
                    position = position >>> 6 << 6 | previous;
                    while (i > 0)
                        position = position << 6 | 63 - Long.numberOfLeadingZeros(set[--i][position]);
                    return position;
                }
            }
            return -1;
        }

        public int next(int position) {
            for (int i = 0; i < set.length && position >>> 6 < set[i].length; i++, position >>>= 6, position++) {
                int next = next(set[i][position >>> 6], position & 63);
                if (next != -1) {
                    position = position >>> 6 << 6 | next;
                    while (i > 0)
                        position = position << 6 | Long.numberOfTrailingZeros(set[--i][position]);
                    return position;
                }
            }
            return -1;
        }

        private static int prev(long set, int n) {
            long h = set << ~n;
            if (h == 0L) {
                return -1;
            }
            return -Long.numberOfLeadingZeros(h) + n;
        }

        private static int next(long set, int n) {
            long h = set >>> n;
            if (h == 0L) {
                return -1;
            }
            return Long.numberOfTrailingZeros(h) + n;
        }

        @Override
        public String toString() {
            List<Integer> list = new ArrayList<>();
            for (int pos = next(0); pos != -1; pos = next(pos + 1)) {
                list.add(pos);
            }
            return list.toString();
        }
    }
}
```
