package com.example

fun main() {
  val heightmap = readLinesString(9).map { it.split("").drop(1).dropLast(1).map(Integer::parseInt) }

  val lowPoints = mutableListOf<Point>()
  for (x in heightmap.indices) {
    for (y in heightmap[x].indices) {
      val adjacentHeights = mutableListOf<Int>()
      if (x > 0) {
        adjacentHeights.add(heightmap[x - 1][y])
      }
      if (x < heightmap.lastIndex) {
        adjacentHeights.add(heightmap[x + 1][y])
      }
      if (y > 0) {
        adjacentHeights.add(heightmap[x][y - 1])
      }
      if (y < heightmap[x].lastIndex) {
        adjacentHeights.add(heightmap[x][y + 1])
      }

      val curPoint = Point(x, y, heightmap[x][y])
      if (adjacentHeights.all { it > curPoint.height }) {
        lowPoints.add(curPoint)
      }
    }
  }
  val riskLevels = lowPoints.map { it.height + 1 }
  val basinSizes = lowPoints.map { Basin(it, heightmap).size() }
  val resultPart2 = basinSizes.sorted().takeLast(3).reduce { acc, i -> acc * i }

  println("Part1: ${riskLevels.sum()}")
  println("Part2: $resultPart2")
}

class Basin(private val lowPoint: Point, private val heightMap: List<List<Int>>) {
  private val basin = mutableSetOf<Point>()

  init {
    initBasin()
  }

  fun size() = basin.size

  private fun initBasin() {
    basin.add(lowPoint)
    left(lowPoint)
    right(lowPoint)
    down(lowPoint)
    up(lowPoint)
  }

  private fun left(from: Point) {
    val y = from.y
    for (x in from.x - 1 downTo 0) {
      val point = Point(x, y, heightMap[x][y])
      if (point.height == 9) {
        break
      } else if (basin.contains(point)) {
        break
      } else {
        basin.add(point)
        up(point)
        down(point)
      }
    }
  }

  private fun right(from: Point) {
    val y = from.y
    for (x in from.x + 1..heightMap.lastIndex) {
      val point = Point(x, y, heightMap[x][y])
      if (point.height == 9) {
        break
      } else if (basin.contains(point)) {
        break
      } else {
        basin.add(point)
        up(point)
        down(point)
      }
    }
  }

  private fun down(from: Point) {
    val x = from.x
    for (y in from.y + 1..heightMap[x].lastIndex) {
      val point = Point(x, y, heightMap[x][y])
      if (point.height == 9) {
        break
      } else if (basin.contains(point)) {
        break
      } else {
        basin.add(point)
        left(point)
        right(point)
      }
    }
  }

  private fun up(from: Point) {
    val x = from.x
    for (y in from.y - 1 downTo 0) {
      val point = Point(x, y, heightMap[x][y])
      if (point.height == 9) {
        break
      } else if (basin.contains(point)) {
        break
      } else {
        basin.add(point)
        left(point)
        right(point)
      }
    }
  }

  override fun toString(): String {
    return "Basin($basin)"
  }
}

data class Point(val x: Int, val y: Int, val height: Int)
