package com.example

import kotlin.math.abs

fun main() {
  val rawLines = readFileString(5)
  val points =
      rawLines
          .map {
            it.split("->").map { pointString ->
              val xy = pointString.trim().split(",")
              val x = Integer.parseInt(xy[0])
              val y = Integer.parseInt(xy[1])
              Part2.Point(x, y)
            }
          }
          .map { Part2.Line(it.first(), it.last()).getPoints() }
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

object Part2 {
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
      } else {
        val signX = (p2.x - p1.x) / abs(p2.x - p1.x)
        val signY = (p2.y - p1.y) / abs(p2.y - p1.y)
        for (delta in 0..abs(p1.x - p2.x)) {
          points.add(Point(p1.x + signX * delta, p1.y + signY * delta))
        }
      }
      return points
    }
  }

  fun range(a: Int, b: Int) = if (a < b) a..b else b..a
}
