package com.example

import kotlin.math.abs

fun main() {
  val numbers = readLinesString(7).first().split(",").map(Integer::parseInt)

  val part1 = calcMinTotalFuel(numbers, ::totalFuelPart1)
  val part2 = calcMinTotalFuel(numbers, ::totalFuelPart2)

  println("Part1: $part1")
  println("Part2: $part2")
}

fun calcMinTotalFuel(
    numbers: List<Int>,
    totalFuelFunction: (positions: List<Int>, targetPos: Int) -> Int
): Int {
  val startPosition = numbers.average().toInt()
  val fuelMinus1 = totalFuelFunction(numbers, startPosition - 1)
  val fuel0 = totalFuelFunction(numbers, startPosition)
  val fuelPlus1 = totalFuelFunction(numbers, startPosition + 1)
  val direction =
      if (fuelPlus1 < fuel0) {
        +1
      } else if (fuelMinus1 < fuel0) {
        -1
      } else {
        return fuel0
      }

  val targetPosition = arrayOf(startPosition, startPosition + direction)
  val totalFuel =
      arrayOf(
          totalFuelFunction(numbers, targetPosition[0]),
          totalFuelFunction(numbers, targetPosition[1]))

  var i = 0
  while (true) {
    targetPosition[(i + 2) % 2] = targetPosition[(i + 1) % 2] + direction
    totalFuel[(i + 2) % 2] = totalFuelFunction(numbers, targetPosition[(i + 2) % 2])
    if (totalFuel[(i + 2) % 2] > totalFuel[(i + 1) % 2]) {
      break
    }
    i++
  }
  return totalFuel[(i + 1) % 2]
}

fun totalFuelPart1(positions: List<Int>, targetPos: Int): Int {
  var totalFuel = 0
  for (i in positions.indices) {
    totalFuel += abs(targetPos - positions[i])
  }
  return totalFuel
}

fun totalFuelPart2(positions: List<Int>, targetPos: Int): Int {
  var totalFuel = 0
  for (i in positions.indices) {
    val n = abs(targetPos - positions[i])
    totalFuel += (n * n + n) / 2
  }
  return totalFuel
}
