package com.example

fun main() {
  val lines = readLinesString(10)

  val scoreTable =
      mapOf(
          ')' to 1,
          ']' to 2,
          '}' to 3,
          '>' to 4,
      )

  val openToCloseMappings =
      mapOf(
          '(' to ')',
          '[' to ']',
          '{' to '}',
          '<' to '>',
      )

  val scores = mutableListOf<Long>()
  for (line in lines) {
    val stack = mutableListOf<Char>()
    var isCorrupted = false
    for (i in line.indices) {
      val character = line[i]
      if (character in openToCloseMappings.keys) {
        stack.add(character)
      } else {
        val opening = stack.removeLast()
        if (openToCloseMappings[opening] != character) {
          isCorrupted = true
          break
        }
      }
    }
    if (!isCorrupted) {
      var score = 0L
      for (opening in stack.reversed()) {
        val missingClosing = openToCloseMappings[opening]!!
        score = score * 5 + scoreTable[missingClosing]!!
      }
      scores.add(score)
    }
  }

  val middleScore = scores.sorted()[scores.size / 2]
  println(middleScore)
}
