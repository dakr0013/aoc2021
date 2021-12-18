package com.example

import kotlin.math.ceil
import kotlin.math.floor

fun main() {
  val snailfishNumbers = readLinesString(18).map { SnailfishNumberParser().parse(it) }
  var finalSum = snailfishNumbers.first()
  for (num in snailfishNumbers.drop(1)) {
    finalSum += num
  }
  println(finalSum)
  println(finalSum.magnitude())
}

data class SnailfishNumber(var allRegularNumbers: MutableList<RegularNumber>) {
  fun reduce(): SnailfishNumber {
    do {
      var reduceActionApplied = false
      val newRegularNumbers = mutableListOf<RegularNumber>()
      var i = 0
      while (i in 0..allRegularNumbers.lastIndex) {
        val currentNumber = allRegularNumbers[i]
        if (!reduceActionApplied) {
          if (currentNumber.nestingLevel == 4) {
            reduceActionApplied = true
            if (i > 0) {
              newRegularNumbers[i - 1] =
                  RegularNumber(
                      newRegularNumbers[i - 1].nestingLevel,
                      newRegularNumbers[i - 1].value + currentNumber.value)
            }
            newRegularNumbers.add(RegularNumber(currentNumber.nestingLevel - 1, 0))
            val rightPair = allRegularNumbers[++i]
            if (++i <= allRegularNumbers.lastIndex) {
              newRegularNumbers.add(
                  RegularNumber(
                      allRegularNumbers[i].nestingLevel,
                      allRegularNumbers[i].value + rightPair.value))
            }
          } else {
            newRegularNumbers.add(currentNumber)
          }
        } else {
          newRegularNumbers.add(currentNumber)
        }
        i++
      }
      if (!reduceActionApplied) {
        i = 0
        newRegularNumbers.clear()
        while (i in 0..allRegularNumbers.lastIndex) {
          val currentNumber = allRegularNumbers[i]
          if (!reduceActionApplied) {
            if (currentNumber.value >= 10) {
              reduceActionApplied = true
              newRegularNumbers.add(
                  RegularNumber(
                      currentNumber.nestingLevel + 1, floor(currentNumber.value / 2.0).toInt()))
              newRegularNumbers.add(
                  RegularNumber(
                      currentNumber.nestingLevel + 1, ceil(currentNumber.value / 2.0).toInt()))
            } else {
              newRegularNumbers.add(currentNumber)
            }
          } else {
            newRegularNumbers.add(currentNumber)
          }
          i++
        }
      }
      allRegularNumbers = newRegularNumbers
    } while (reduceActionApplied)
    return this
  }

  operator fun plus(other: SnailfishNumber): SnailfishNumber {
    val newNumbers =
        this.allRegularNumbers.plus(other.allRegularNumbers).map {
          RegularNumber(it.nestingLevel + 1, it.value)
        }
    return SnailfishNumber(newNumbers.toMutableList()).reduce()
  }

  fun magnitude(): Int {
    var i = 0
    fun magnitudeRec(level: Int = -1): Int {
      val num = allRegularNumbers[i]
      return if (level == num.nestingLevel) {
        i++
        num.value
      } else {
        3 * magnitudeRec(level + 1) + 2 * magnitudeRec(level + 1)
      }
    }
    return magnitudeRec()
  }

  override fun toString(): String {
    var i = 0
    fun toStringRec(level: Int = -1): String {
      val num = allRegularNumbers[i]
      return if (level == num.nestingLevel) {
        i++
        num.value.toString()
      } else {
        "[${toStringRec(level+1)},${toStringRec(level+1)}]"
      }
    }
    return toStringRec()
  }
}

data class RegularNumber(val nestingLevel: Int, val value: Int)

class SnailfishNumberParser {
  fun parse(line: String): SnailfishNumber {
    val allRegularNumbers = mutableListOf<RegularNumber>()
    var currentNestingLevel = -1
    for (char in line) {
      when (char) {
        '[' -> currentNestingLevel++
        ']' -> currentNestingLevel--
        ',' -> {}
        else -> {
          allRegularNumbers.add(RegularNumber(currentNestingLevel, char.digitToInt()))
        }
      }
    }
    return SnailfishNumber(allRegularNumbers)
  }
}
