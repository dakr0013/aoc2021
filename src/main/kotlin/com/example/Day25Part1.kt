package com.example

fun main() {
  var state = readLinesString(25).map { it.toCharArray() }.toTypedArray()

  var step = 1
  var isEastMoving = true
  var stoppedMovingEast = true
  var stoppedMovingSouth = true
  do {
    if (isEastMoving) {
      stoppedMovingEast = true
    } else {
      stoppedMovingSouth = true
    }
    val newState = Array(state.size) { CharArray(state[0].size) { '.' } }

    for (y in state.indices) {
      for (x in state[y].indices) {
        if (isEastMoving) {
          if (state[y][x] == '>') {
            if (state[y][(x + 1) % state[y].size] == '.') {
              newState[y][(x + 1) % state[y].size] = '>'
              stoppedMovingEast = false
            } else {
              newState[y][x] = '>'
            }
          } else if (state[y][x] == 'v') {
            newState[y][x] = 'v'
          }
        } else {
          if (state[y][x] == 'v') {
            if (state[(y + 1) % state.size][x] == '.') {
              newState[(y + 1) % state.size][x] = 'v'
              stoppedMovingSouth = false
            } else {
              newState[y][x] = 'v'
            }
          } else if (state[y][x] == '>') {
            newState[y][x] = '>'
          }
        }
      }
    }

    if (!isEastMoving) step++
    isEastMoving = !isEastMoving
    state = newState
  } while (!(stoppedMovingEast && stoppedMovingSouth))

  //  printSeaFloor(state)
  println(step)
}

fun printSeaFloor(state: Array<CharArray>) {
  for (line in state) {
    println(line.concatToString())
  }
  println()
}
