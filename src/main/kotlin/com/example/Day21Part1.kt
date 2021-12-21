package com.example

import kotlin.math.min

fun main() {
  val (player1StartingPos, player2StartingPos) =
      readLinesString(21).map { it.split(" ").last().toInt() }

  val dice = DeterministicDice(100)
  val player1 = Player(player1StartingPos)
  val player2 = Player(player2StartingPos)
  val game = DiracDiceGame(dice, player1, player2)
  game.playGame()

  val losingPlayerScore = min(player1.totalScore, player2.totalScore)
  println(dice.rollCount * losingPlayerScore)
}

class Player(initialPosition: Int) {
  var totalScore = 0
    private set
  var position = initialPosition
    private set

  fun move(spaces: Int) {
    position = ((position + spaces - 1) % 10) + 1
    totalScore += position
  }

  fun hasWon() = totalScore >= 1000
}

class DiracDiceGame(val dice: DeterministicDice, val player1: Player, val player2: Player) {
  private var isPlayer1Turn = true

  fun playGame() {
    while (!(player1.hasWon() || player2.hasWon())) {
      nextTurn()
    }
  }

  fun nextTurn() {
    val result = dice.roll(3)
    val currentPlayer =
        if (isPlayer1Turn) {
          player1
        } else {
          player2
        }
    currentPlayer.move(result)
    isPlayer1Turn = !isPlayer1Turn
  }
}

class DeterministicDice(val numSides: Int) {
  var rollCount = 0
    private set
  var nextNumber = 1
    private set

  fun roll(): Int {
    rollCount++
    return nextNumber.also { nextNumber = (nextNumber % numSides) + 1 }
  }

  fun roll(times: Int): Int {
    var sum = 0
    for (i in 1..times) {
      sum += roll()
    }
    return sum
  }
}
