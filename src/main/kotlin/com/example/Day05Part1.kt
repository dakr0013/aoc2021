package com.example

fun main() {
  val rawLines = readFileString(5, 1)
  val points =
      rawLines
          .map {
            it.split("->").map { pointString ->
              val xy = pointString.trim().split(",")
              val x = Integer.parseInt(xy[0])
              val y = Integer.parseInt(xy[1])
              Part1.Point(x, y)
            }
          }
          .map { Part1.Line(it.first(), it.last()).getPoints() }
          .flatten()

  var maxX = 0
  var maxY = 0
  for (p in points) {
    if (p.x > maxX) maxX = p.x
    if (p.y > maxY) maxY = p.y
  }

  val diagram = Array(maxX + 1) { IntArray(maxY + 1) }
  for (p in points) {
    diagram[p.x][p.y]++
  }

  var overlaps = 0
  for (x in diagram.indices) {
    for (y in diagram[x].indices) {
      if (diagram[x][y] >= 2) {
        overlaps++
      }
    }
  }
  println(overlaps)
}

object Part1 {
  data class Point(val x: Int, val y: Int)

  data class Line(val p1: Point, val p2: Point) {
    fun getPoints(): List<Point> {
      val points = mutableListOf<Point>()
      if (p1.y == p2.y) {
        for (x in range(p1.x, p2.x)) {
          points.add(Point(x, p1.y))
        }
      } else if (p1.x == p2.x) {
        for (y in range(p1.y, p2.y)) {
          points.add(Point(p1.x, y))
        }
      }
      return points
    }
  }

  fun range(a: Int, b: Int) = if (a < b) a..b else b..a
}
