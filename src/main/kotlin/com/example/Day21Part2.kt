package com.example

import kotlin.math.max

var player1WinCount = 0UL
var player2WinCount = 0UL
val rollSumCount = calcRollSumCount()

fun main() {
  val (player1StartingPos, player2StartingPos) =
      readLinesString(21).map { it.split(" ").last().toInt() }

  playGame(player1StartingPos, player2StartingPos)
  println(max(player1WinCount, player2WinCount))
}

fun calcRollSumCount(): Map<Int, ULong> {
  val rollSumCount = mutableMapOf<Int, ULong>()
  for (roll1 in 1..3) {
    for (roll2 in 1..3) {
      for (roll3 in 1..3) {
        val sum = roll1 + roll2 + roll3
        rollSumCount.merge(sum, 1UL) { old, new -> old + new }
      }
    }
  }
  return rollSumCount
}

data class PlayerState(val position: Int, val totalScore: Int = 0) {
  fun hasWon() = totalScore >= 21

  fun turn(sum: Int): PlayerState {
    val newPosition = ((position + sum - 1) % 10) + 1
    return PlayerState(newPosition, totalScore + newPosition)
  }
}

data class GameState(
    val isPlayer1Turn: Boolean,
    val player1State: PlayerState,
    val player2State: PlayerState,
    val winMultiplier: ULong = 1UL,
)

fun playGame(player1InitPos: Int, player2InitPos: Int) {
  val initialState =
      GameState(
          isPlayer1Turn = true,
          player1State = PlayerState(player1InitPos),
          player2State = PlayerState(player2InitPos),
      )
  continueGame(initialState)
}

fun continueGame(state: GameState) {
  if (state.player1State.hasWon()) {
    player1WinCount += state.winMultiplier
    return
  }
  if (state.player2State.hasWon()) {
    player2WinCount += state.winMultiplier
    return
  }

  for ((rollOutcome, outcomeCount) in rollSumCount) {
    if (state.isPlayer1Turn) {
      val newPlayerState = state.player1State.turn(rollOutcome)
      val newWinMultiplier = state.winMultiplier * outcomeCount
      continueGame(
          state.copy(
              isPlayer1Turn = false,
              player1State = newPlayerState,
              winMultiplier = newWinMultiplier,
          ))
    } else {
      val newPlayerState = state.player2State.turn(rollOutcome)
      val newWinMultiplier = state.winMultiplier * outcomeCount
      continueGame(
          state.copy(
              isPlayer1Turn = true,
              player2State = newPlayerState,
              winMultiplier = newWinMultiplier,
          ))
    }
  }
}
