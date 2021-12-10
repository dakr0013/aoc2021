package com.example

fun main() {
  val binaryNumbers = readLinesString(3)

  var gammaRate = ""
  var epsilonRate = ""
  for (column in 0..11) {
    var countOnes = 0
    for (row in binaryNumbers.indices) {
      val num = binaryNumbers[row][column]
      if (num == '1') {
        countOnes++
      }
    }

        if (countOnes > 500) {
          gammaRate +="1"
          epsilonRate +="0"
        } else {
          gammaRate +="0"
          epsilonRate +="1"
        }
  }

  val gamma = Integer.parseInt(gammaRate,2)
  val epsilon = Integer.parseInt(epsilonRate,2)
  println(gamma)
  println(epsilon)
  println(gamma*epsilon)
}
