package com.example

fun main() {
  val (xTargetRange, yTargetRange) =
      readLinesString(17)
          .first()
          .replace("target area: x=", "")
          .replace(" y=", "")
          .split(",")
          .map { range ->
            val (start, end) = range.split("..").map { it.toInt() }
            start..end
          }

  var highestPosition = -1
  for (initialVelocityY in 0..1000) {
    val currentHighestPosition = calcHighestPosition(initialVelocityY, yTargetRange)

    if (currentHighestPosition > highestPosition) {
      highestPosition = currentHighestPosition
    }
  }

  println(highestPosition)
}

fun calcHighestPosition(initialVelocityY: Int, yTargetRange: IntRange): Int {
  var currentY = 0
  var currentVelocityY = initialVelocityY
  var currentHighestPosition = currentY
  while (currentY >= yTargetRange.first && currentY !in yTargetRange) {
    currentY += currentVelocityY--
    if (currentY > currentHighestPosition) {
      currentHighestPosition = currentY
    }
  }
  return if (currentY in yTargetRange) {
    currentHighestPosition
  } else {
    -1
  }
}
