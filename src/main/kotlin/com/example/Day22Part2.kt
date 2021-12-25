package com.example

fun main() {
  val input = readLinesString(22)
  val rebootSteps = input.map { RebootStep.parse(it) }
  val reactorCore = ReactorCore()
  reactorCore.reboot(rebootSteps)
  println(reactorCore.turnedOnCubesCount())
}
