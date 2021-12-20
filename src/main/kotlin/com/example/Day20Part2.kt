package com.example

fun main() {
  val input = readLinesString(20)
  val imageEnhancement = input.first().toCharArray()
  val image = Image(input.drop(2))
  for (i in 1..50) {
    image.enhance(imageEnhancement)
  }
  println(image.lightPixelCount)
}
