package com.example

fun main() {
  val binaryNumbers = readLinesString(3)

  val oxygen = findOxygen(binaryNumbers, 0)
  val co2 = findCO2(binaryNumbers, 0)

  println(oxygen)
  println(co2)
  println(oxygen*co2)
}

fun findOxygen(numbers: List<String>, columnIndex: Int): Int {
  if (numbers.size == 1) {
    return Integer.parseInt(numbers.first(),2)
  } else {
    val ones = mutableListOf<String>()
    val zeros = mutableListOf<String>()
    for (row in numbers.indices) {
      val num = numbers[row][columnIndex]
      if (num == '1') {
        ones.add(numbers[row])
      } else {
        zeros.add(numbers[row])
      }
    }

    return if (ones.size >= zeros.size) {
      findOxygen(ones, columnIndex + 1)
    } else {
      findOxygen(zeros, columnIndex + 1)
    }
  }
}

fun findCO2(numbers: List<String>, columnIndex: Int): Int {
  if (numbers.size == 1) {
    return Integer.parseInt(numbers.first(),2)
  } else {
    val ones = mutableListOf<String>()
    val zeros = mutableListOf<String>()
    for (row in numbers.indices) {
      val num = numbers[row][columnIndex]
      if (num == '1') {
        ones.add(numbers[row])
      } else {
        zeros.add(numbers[row])
      }
    }

    return if (ones.size < zeros.size) {
      findCO2(ones, columnIndex + 1)
    } else {
      findCO2(zeros, columnIndex + 1)
    }
  }
}
