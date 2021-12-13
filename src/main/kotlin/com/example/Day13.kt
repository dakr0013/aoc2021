package com.example

fun main() {
  val input = readLinesString(13)
  val separatorIndex = input.indexOfFirst { it.isBlank() }
  val dots = input.subList(0, separatorIndex)

  val foldInstructions =
      input.subList(separatorIndex + 1, input.size).map {
        val (axis, position) = it.split(" ").last().split("=")
        FoldInstruction(axis, position.toInt())
      }

  val paper = TransparentPaper(dots)
  paper.fold(foldInstructions.first())
  val dotCountAfterFirstFold = paper.dotCount()

  for (instruction in foldInstructions.drop(1)) {
    paper.fold(instruction)
  }

  println("Part1: $dotCountAfterFirstFold")
  println("Part2:")
  paper.print()
}

data class FoldInstruction(val axis: String, val position: Int)

class TransparentPaper(dots: List<String>) {
  private var paper: Array<CharArray>

  init {
    var xMax = 0
    var yMax = 0
    for (dot in dots) {
      val (x, y) = dot.split(",").map(Integer::parseInt)
      if (x > xMax) xMax = x
      if (y > yMax) yMax = y
    }

    paper = Array(yMax + 1) { CharArray(xMax + 1) { '.' } }
    for (dot in dots) {
      val (x, y) = dot.split(",").map(Integer::parseInt)
      paper[y][x] = '#'
    }
  }

  fun print() {
    for (y in paper.indices) {
      for (x in paper[y].indices) {
        print(paper[y][x])
      }
      println()
    }
  }

  fun dotCount(): Int {
    var count = 0
    for (y in paper.indices) {
      for (x in paper[y].indices) {
        if (paper[y][x] == '#') {
          count++
        }
      }
    }
    return count
  }

  fun fold(instruction: FoldInstruction) {
    when (instruction.axis) {
      "y" -> foldHorizontal(instruction.position)
      else -> foldVertical(instruction.position)
    }
  }

  private fun foldHorizontal(yFold: Int) {
    val newPaper = Array(yFold) { CharArray(paper[0].size) { '.' } }
    for (y in newPaper.indices) {
      for (x in newPaper[y].indices) {
        val yDiff = yFold - y
        if (paper[y][x] == '#' || (yFold + yDiff) < paper.size && paper[yFold + yDiff][x] == '#') {
          newPaper[y][x] = '#'
        }
      }
    }
    paper = newPaper
  }

  private fun foldVertical(xFold: Int) {
    val newPaper = Array(paper.size) { CharArray(xFold) { '.' } }
    for (y in newPaper.indices) {
      for (x in newPaper[y].indices) {
        val xDiff = xFold - x
        if (paper[y][x] == '#' ||
            (xFold + xDiff) < paper[0].size && paper[y][xFold + xDiff] == '#') {
          newPaper[y][x] = '#'
        }
      }
    }
    paper = newPaper
  }
}
