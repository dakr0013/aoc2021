package com.example

fun main() {
  val outputDigits = readFileString(8).map { it.split("|")[1].trim().split(" ") }.flatten()
  println(outputDigits.count { it.length in listOf(2, 4, 3, 7) })
}
