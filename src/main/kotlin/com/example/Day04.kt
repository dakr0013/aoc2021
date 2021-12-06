package com.example

fun main() {
  val lines = readFileString(4)
  val inputNumbers = lines.first().split(",").map(Integer::parseInt)

  val boards = mutableListOf<Board>()
  var tempMatrix = Array(5) { IntArray(5) }
  var tempIndex = 0
  for (line in lines.drop(2)) {
    if (line.isBlank()) {
      boards.add(Board(tempMatrix))
      tempMatrix = Array(5) { IntArray(5) }
      tempIndex = 0
    } else {
      tempMatrix[tempIndex++] =
          line.trim().split("\\s+".toRegex()).map(Integer::parseInt).toIntArray()
    }
  }

  val boardRank = mutableListOf<Int>()

  for (inputNumber in inputNumbers) {
    val winningBoards = mutableListOf<Board>()
    for (board in boards) {
      board.mark(inputNumber)
      if (board.hasBingo()) {
        val score = board.sumOfUnmarked() * inputNumber
        boardRank.add(score)
        winningBoards.add(board)
      }
    }
    boards.removeAll(winningBoards)
  }

  println("Score of first winning board: ${boardRank.first()}")
  println("Score of last  winning board: ${boardRank.last()}")
}

private class Board(private val board: Array<IntArray>) {
  private val marks = Array(board.size) { BooleanArray(board.size) }

  fun mark(number: Int) {
    for (row in board.indices) {
      for (col in board[row].indices) {
        if (board[row][col] == number) {
          marks[row][col] = true
        }
      }
    }
  }

  fun sumOfUnmarked(): Int {
    var sum = 0
    for (row in board.indices) {
      for (col in board[row].indices) {
        if (!marks[row][col]) {
          sum += board[row][col]
        }
      }
    }
    return sum
  }

  fun hasBingo(): Boolean {
    val anyRowMarked = marks.any { row -> row.all { marked -> marked } }
    if (anyRowMarked) return true

    for (col in board.indices) {
      var colMarked = true
      for (row in board[col].indices) {
        if (!marks[row][col]) {
          colMarked = false
          break
        }
      }
      if (colMarked) return true
    }

    return false
  }

  override fun toString(): String {
    return "Board(${board.contentDeepToString()})"
  }
}
