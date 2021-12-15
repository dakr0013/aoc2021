package com.example

import kotlin.math.min

fun main() {
  val riskLevelMap =
      readLinesString(15).mapIndexed { y, line ->
        line.toCharArray().mapIndexed { x, riskLevel -> Node(x, y, riskLevel.digitToInt()) }
      }
  riskLevelMap[0][0].riskLevelFromStart = 0

  val startNode = riskLevelMap[0][0]
  dijkstra(startNode, riskLevelMap.map { it.toList() })

  println(riskLevelMap.last().last().riskLevelFromStart)
}

fun dijkstra(
    startNode: Node,
    riskLevelMap: List<List<Node>>,
) {
  val unvisitedNodes = mutableSetOf<Node>()
  var currentNode = startNode
  while (true) {
    for (node in currentNode.getUnvisitedNeighbors(riskLevelMap)) {
      unvisitedNodes.add(node)
      val tentativeDistance = currentNode.riskLevelFromStart + node.riskLevel
      node.riskLevelFromStart = min(tentativeDistance, node.riskLevelFromStart)
    }
    currentNode.isVisited = true
    unvisitedNodes.remove(currentNode)
    if (currentNode == riskLevelMap.last().last()) {
      break
    }
    val nodeWithSmallestDistance = unvisitedNodes.minOrNull()
    if (nodeWithSmallestDistance != null) {
      currentNode = nodeWithSmallestDistance
    } else {
      break
    }
  }
}

class Node(
    val x: Int,
    val y: Int,
    val riskLevel: Int,
    var isVisited: Boolean = false,
    var riskLevelFromStart: Int = Int.MAX_VALUE
) : Comparable<Node> {
  fun getUnvisitedNeighbors(riskLevelMap: List<List<Node>>): List<Node> {
    val neighbors = mutableListOf<Node>()
    val xMax = riskLevelMap[0].lastIndex
    val yMax = riskLevelMap.lastIndex

    if (x > 0) {
      val left = riskLevelMap[y][x - 1]
      neighbors.add(left)
    }

    if (x < xMax) {
      val right = riskLevelMap[y][x + 1]
      neighbors.add(right)
    }

    if (y < yMax) {
      val lower = riskLevelMap[y + 1][x]
      neighbors.add(lower)
    }

    if (y > 0) {
      val upper = riskLevelMap[y - 1][x]
      neighbors.add(upper)
    }

    return neighbors.filterNot { it.isVisited }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Node

    if (x != other.x) return false
    if (y != other.y) return false

    return true
  }

  override fun hashCode(): Int {
    var result = x
    result = 31 * result + y
    return result
  }

  override fun compareTo(other: Node) = riskLevelFromStart.compareTo(other.riskLevelFromStart)
}
