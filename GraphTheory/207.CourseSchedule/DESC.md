# 207. Course Schedule

You are tasked with taking `numCourses` courses, labeled from `0` to `numCourses - 1`. The course prerequisites are given as an array `prerequisites`, where `prerequisites[i] = [ai, bi]` means you must complete course `bi` before taking course `ai`.

For example, the pair `[0, 1]` means you need to complete course `1` before taking course `0`.

Return `true` if it is possible to finish all courses. Otherwise, return `false`.

## Examples

### Example 1:

**Input:**
`numCourses = 2, prerequisites = [[1,0]]`
**Output:**
`true`
**Explanation:**
You can take course `0` first, then course `1`.

### Example 2:

**Input:**
`numCourses = 2, prerequisites = [[1,0],[0,1]]`
**Output:**
`false`
**Explanation:**
There is a cycle: to take course `1`, you need to complete course `0`, and vice versa. Hence, it is impossible.

## Constraints

- `1 <= numCourses <= 2000`
- `0 <= prerequisites.length <= 5000`
- `prerequisites[i].length == 2`
- `0 <= ai, bi < numCourses`
- All pairs in `prerequisites` are unique.
