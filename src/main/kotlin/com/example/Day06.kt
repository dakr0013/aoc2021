package com.example

const val maxTimer = 8
const val maxDays = 256

fun main() {
  val numbers =
      readFileString(6, 1).first().split(",").map(Integer::parseInt).groupBy { it }.mapValues {
        it.value.size
      }

  var fishes = mutableMapOf<Int, Long>()
  for (timer in 0..maxTimer) {
    fishes[timer] = numbers[timer]?.toLong() ?: 0L
  }

  for (day in 1..maxDays) {
    val newFishes = mutableMapOf<Int, Long>()
    for (timer in 0..maxTimer) {
      newFishes[timer] = fishes[(timer + 1) % (maxTimer + 1)]!!
    }
    newFishes[6] = newFishes[6]!!.plus(fishes[0]!!)

    fishes = newFishes.toMutableMap()
  }

  println(fishes.values.reduce { acc, i -> acc + i })
}
