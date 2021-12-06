package com.example

fun main() {
  val numbers = readFileInt(1)
  val changes = BooleanArray(numbers.size)
  for (i in numbers.indices.drop(1)) {
    changes[i] = numbers[i] > numbers[i - 1]
  }
  val result = changes.filter { it }.size
  println(result)
}
