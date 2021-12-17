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

  var count = 0
  for (initialVelocityY in yTargetRange.first..1000) {
    for (initialVelocityX in 0..xTargetRange.last) {
      if (reachesTargetArea(initialVelocityX, initialVelocityY, xTargetRange, yTargetRange)) {
        count++
      }
    }
  }

  println(count)
}

fun reachesTargetArea(
    initialVelocityX: Int,
    initialVelocityY: Int,
    xTargetRange: IntRange,
    yTargetRange: IntRange,
): Boolean {
  var currentX = 0
  var currentY = 0
  var currentVelocityX = initialVelocityX
  var currentVelocityY = initialVelocityY
  while (currentY >= yTargetRange.first &&
      currentX <= xTargetRange.last &&
      (currentX !in xTargetRange || currentY !in yTargetRange)) {
    currentX += currentVelocityX
    if (currentVelocityX > 0) currentVelocityX--
    currentY += currentVelocityY--
  }
  return currentY in yTargetRange && currentX in xTargetRange
}
