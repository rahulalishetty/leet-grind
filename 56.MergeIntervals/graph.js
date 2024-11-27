var overlap = function (a, b) {
  return a[0] <= b[1] && b[0] <= a[1];
};
var buildGraph = function (intervals) {
  var graph = new Map();
  for (var i = 0; i < intervals.length; i++) {
    for (var j = i + 1; j < intervals.length; j++) {
      if (overlap(intervals[i], intervals[j])) {
        if (graph.has(intervals[i])) {
          graph.get(intervals[i]).push(intervals[j]);
        } else {
          graph.set(intervals[i], [intervals[j]]);
        }
        if (graph.has(intervals[j])) {
          graph.get(intervals[j]).push(intervals[i]);
        } else {
          graph.set(intervals[j], [intervals[i]]);
        }
      }
    }
  }
  return graph;
};
var mergeNodes = function (nodes) {
  var minStart = Infinity;
  var maxEnd = -Infinity;
  for (var node of nodes) {
    minStart = Math.min(minStart, node[0]);
    maxEnd = Math.max(maxEnd, node[1]);
  }
  return [minStart, maxEnd];
};
var markComponentDFS = function (
  start,
  graph,
  nodesInComp,
  compNumber,
  visited
) {
  var stack = [start];
  while (stack.length) {
    var node = stack.pop();
    if (!visited.has(node)) {
      visited.add(node);
      if (nodesInComp[compNumber]) {
        nodesInComp[compNumber].push(node);
      } else {
        nodesInComp[compNumber] = [node];
      }
      if (graph.has(node)) {
        for (var child of graph.get(node)) {
          stack.push(child);
        }
      }
    }
  }
};
var merge = function (intervals) {
  var graph = buildGraph(intervals);
  var nodesInComp = {};
  var visited = new Set();
  var compNumber = 0;
  for (var interval of intervals) {
    if (!visited.has(interval)) {
      markComponentDFS(interval, graph, nodesInComp, compNumber, visited);
      compNumber++;
    }
  }
  var merged = [];
  for (var comp = 0; comp < compNumber; comp++) {
    merged.push(mergeNodes(nodesInComp[comp]));
  }
  return merged;
};
