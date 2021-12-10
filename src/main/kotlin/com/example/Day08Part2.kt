package com.example

fun main() {
  val allSignalPatterns = readLinesString(8).map { it.split("|")[0].trim().split(" ") }
  val allOutputDigits = readLinesString(8).map { it.split("|")[1].trim().split(" ") }

  var sum = 0
  for (i in allSignalPatterns.indices) {
    val outputDigits = allOutputDigits[i]
    val signalPatterns = allSignalPatterns[i]

    val reducedPatterns =
        signalPatterns.groupBy { it.length }.mapValues {
          it.value.reduce { acc, s -> acc.toList().intersect(s.toList()).joinToString("") }
        }
    val configBuilder = ConfigBuilder()
    for (pattern in reducedPatterns) {
      configBuilder.addSignalPattern(pattern.key, pattern.value)
    }

    val display = SevenSegmentDisplay(configBuilder.build())
    var outputNumberString = ""
    for (pattern in outputDigits) {
      outputNumberString += display.getNumber(pattern)
    }
    sum += Integer.parseInt(outputNumberString)
  }
  println(sum)
}

class SevenSegmentDisplay(private val configuration: Map<String, Int>) {
  fun getNumber(signalPattern: String): String {
    val alphabeticallySortedSignalPattern = signalPattern.toCharArray().sorted().joinToString("")
    return configuration[alphabeticallySortedSignalPattern].toString()
  }
}

class ConfigBuilder {
  private val segments = Array(7) { ('a'..'g').toMutableList() }
  private val finalSegments = CharArray(7)

  fun addSignalPattern(length: Int, pattern: String) {
    when (length) {
      2 -> subFromSegments(1.invertedSegments(), pattern)
      3 -> subFromSegments(7.invertedSegments(), pattern)
      4 -> subFromSegments(4.invertedSegments(), pattern)
      5 -> subFromSegments(listOf(1, 2, 4, 5), pattern)
      6 -> subFromSegments(listOf(2, 3, 4), pattern)
      7 -> subFromSegments(8.invertedSegments(), pattern)
    }
  }

  private fun subFromSegments(indices: List<Int>, pattern: String) {
    for (index in indices) {
      segments[index].removeIf { pattern.contains(it) }
    }
  }

  fun build(): Map<String, Int> {
    for (unused in 1..7) {
      val charsToRemove = mutableListOf<Char>()
      for (i in segments.indices) {
        val chars = segments[i]
        if (chars.size == 1) {
          charsToRemove.add(chars.first())
          finalSegments[i] = chars.first()
        }
      }
      for (chars in segments) {
        chars.removeAll(charsToRemove)
      }
    }
    return mutableMapOf(
        getPattern(0) to 0,
        getPattern(1) to 1,
        getPattern(2) to 2,
        getPattern(3) to 3,
        getPattern(4) to 4,
        getPattern(5) to 5,
        getPattern(6) to 6,
        getPattern(7) to 7,
        getPattern(8) to 8,
        getPattern(9) to 9,
    )
  }

  private fun getPattern(number: Int) =
      number.segments().map { finalSegments[it] }.sorted().joinToString("")
}

fun Int.invertedSegments(): List<Int> {
  val allSegments = (0..6).toList()
  val numberSegmentsMapping =
      mapOf(
          0 to allSegments.minus(listOf(0, 1, 2, 4, 5, 6)),
          1 to allSegments.minus(listOf(2, 5)),
          2 to allSegments.minus(listOf(0, 2, 3, 4, 6)),
          3 to allSegments.minus(listOf(0, 2, 3, 5, 6)),
          4 to allSegments.minus(listOf(1, 2, 3, 5)),
          5 to allSegments.minus(listOf(0, 1, 3, 5, 6)),
          6 to allSegments.minus(listOf(0, 1, 3, 4, 5, 6)),
          7 to allSegments.minus(listOf(0, 2, 5)),
          8 to allSegments.minus(listOf(0, 1, 2, 3, 4, 5, 6)),
          9 to allSegments.minus(listOf(0, 1, 2, 3, 5, 6)),
      )
  return numberSegmentsMapping[this] ?: emptyList()
}

fun Int.segments(): List<Int> {
  val numberSegmentsMapping =
      mapOf(
          0 to listOf(0, 1, 2, 4, 5, 6),
          1 to listOf(2, 5),
          2 to listOf(0, 2, 3, 4, 6),
          3 to listOf(0, 2, 3, 5, 6),
          4 to listOf(1, 2, 3, 5),
          5 to listOf(0, 1, 3, 5, 6),
          6 to listOf(0, 1, 3, 4, 5, 6),
          7 to listOf(0, 2, 5),
          8 to listOf(0, 1, 2, 3, 4, 5, 6),
          9 to listOf(0, 1, 2, 3, 5, 6),
      )
  return numberSegmentsMapping[this] ?: emptyList()
}
