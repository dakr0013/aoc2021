package com.example

fun main() {
  val input = readLinesString(15)

  val ySingleSize = input.size
  val xSingleSize = input[0].length
  val yMax = input.size * 5 - 1
  val xMax = input[0].length * 5 - 1
  val riskLevelMap = Array(yMax + 1) { Array<Node>(xMax + 1) { Node(0, 0, 0) } }
  for (y in 0..yMax) {
    for (x in 0..xMax) {
      var riskLevel = input[y % ySingleSize][x % xSingleSize].digitToInt()
      riskLevel += y / ySingleSize + x / xSingleSize
      riskLevel = ((riskLevel - 1) % 9) + 1
      riskLevelMap[y][x] = Node(x, y, riskLevel)
    }
  }

  riskLevelMap[0][0].riskLevelFromStart = 0
  val startNode = riskLevelMap[0][0]
  dijkstra(startNode, riskLevelMap.map { it.toList() })

  println(riskLevelMap.last().last().riskLevelFromStart)
}
