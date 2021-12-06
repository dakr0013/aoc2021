package com.example

fun main() {
  val sums = readFileInt(1).windowed(3, 1).map { it.reduce { acc, i -> acc + i } }
  val changes = BooleanArray(sums.size)
  for (i in sums.indices.drop(1)) {
    changes[i] = sums[i] > sums[i - 1]
  }
  val result = changes.filter { it }.size
  println(result)
}
