package com.example

fun main() {
  val lines = readLinesString(10)

  val scoreTable =
      mapOf(
          ')' to 3,
          ']' to 57,
          '}' to 1197,
          '>' to 25137,
      )

  val openToCloseMappings =
      mapOf(
          '(' to ')',
          '[' to ']',
          '{' to '}',
          '<' to '>',
      )

  val incorrectClosings = mutableListOf<Char>()
  for (line in lines) {
    val stack = mutableListOf<Char>()
    for (i in line.indices) {
      val character = line[i]
      if (character in openToCloseMappings.keys) {
        stack.add(character)
      } else {
        val opening = stack.removeLast()
        if (openToCloseMappings[opening] != character) {
          incorrectClosings.add(character)
          break
        }
      }
    }
  }

  val totalSyntaxErrorScore = incorrectClosings.mapNotNull { scoreTable[it] }.sum()
  println(totalSyntaxErrorScore)
}
