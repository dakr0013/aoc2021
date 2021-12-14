package com.example

fun main() {
  val input = readLinesString(14)
  val polymerTemplate = input.first()
  val pairInsertionRules =
      input.subList(2, input.size).associate {
        val (pair, insertion) = it.split(" -> ")
        pair to insertion
      }

  var polymer = polymerTemplate.toCharArray().toMutableList().map { it.toString() }
  for (step in 1..10) {
    val newPolymer = mutableListOf<String>()
    for (i in polymer.dropLast(1).indices) {
      val leftElement = polymer[i]
      val rightElement = polymer[i + 1]
      val pair = leftElement + rightElement
      val insertion = pairInsertionRules[pair]!!
      newPolymer.add(leftElement)
      newPolymer.add(insertion)
    }
    newPolymer.add(polymer.last())
    polymer = newPolymer
  }
  val elementCount = polymer.groupBy { it }.map { it.value.size }.sorted()
  println(elementCount.last() - elementCount.first())
}
