package com.example

fun main() {
  val snailfishNumbers = readLinesString(18).map { SnailfishNumberParser().parse(it) }
  var largestMagnitude = 0
  for (num1 in snailfishNumbers.drop(1)) {
    for (num2 in snailfishNumbers.minus(num1)) {
      val sum = num1 + num2
      val magnitude = sum.magnitude()
      if (magnitude > largestMagnitude) {
        largestMagnitude = magnitude
      }
    }
  }
  println(largestMagnitude)
}
