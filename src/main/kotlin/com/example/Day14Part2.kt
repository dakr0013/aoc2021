package com.example

fun main() {
  val input = readLinesString(14)
  val polymerTemplate = input.first()
  val pairInsertionRules =
      input.subList(2, input.size).associate {
        val (pair, insertion) = it.split(" -> ")
        pair to insertion
      }

  val pairGeneratingRules =
      pairInsertionRules.mapValues { listOf(it.key.first() + it.value, it.value + it.key.last()) }

  val elementCounts =
      pairInsertionRules
          .values
          .reduce { acc, s -> acc + s }
          .toSet()
          .associateWith { 0L }
          .toMutableMap()
  var pairCounts = pairInsertionRules.mapValues { 0L }.toMutableMap()
  polymerTemplate.windowed(2, 1).forEach { pairCounts[it] = pairCounts[it]!!.inc() }
  polymerTemplate.forEach { elementCounts[it] = elementCounts[it]!!.inc() }

  for (step in 1..40) {
    val newPairCounts = mutableMapOf<String, Long>()
    for (pairCount in pairCounts) {
      newPairCounts.merge(pairCount.key, 0) { old, new -> old + new }

      val (generatedPair1, generatedPair2) = pairGeneratingRules[pairCount.key]!!
      newPairCounts.merge(generatedPair1, pairCount.value) { old, new -> old + new }
      newPairCounts.merge(generatedPair2, pairCount.value) { old, new -> old + new }

      val insertedElement = pairInsertionRules[pairCount.key]!!.first()
      elementCounts.merge(insertedElement, pairCount.value) { old, new -> old + new }
    }
    pairCounts = newPairCounts
  }

  val sortedCounts = elementCounts.values.sorted()
  println(sortedCounts.last() - sortedCounts.first())
}
