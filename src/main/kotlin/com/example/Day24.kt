package com.example

import java.lang.IllegalStateException

fun main() {
  val instructions = optimizeInstructions(readLinesString(24))
  val allInstructionsPerDigit = mutableListOf<List<String>>()
  val instructionsPerDigit = mutableListOf<String>()
  for (instruction in instructions.drop(1)) {
    if (instruction == "inp w") {
      allInstructionsPerDigit.add(instructionsPerDigit.toList())
      instructionsPerDigit.clear()
    } else {
      instructionsPerDigit.add(instruction)
    }
  }
  allInstructionsPerDigit.add(instructionsPerDigit)

  val termsToCalcZ = allInstructionsPerDigit.map { parseTerm(it) }

  val requiredZRanges = mutableListOf(setOf(0L))
  for (i in termsToCalcZ.indices.reversed()) {
    val requiredZRange = mutableSetOf<Long>()
    for (z in 0..350_000) {
      for (w in 1..9) {
        val result =
            termsToCalcZ[i].calculate(mapOf("z" to Literal(z.toLong()), "w" to Literal(w.toLong())))
        if (result in requiredZRanges.last()) {
          requiredZRange.add(z.toLong())
        }
      }
    }
    requiredZRanges.add(requiredZRange)
  }

  val zRanges = requiredZRanges.reversed()
  // part1:
  var z = 0L
  var highestValidNumber = ""
  for (i in 0..13) {
    var newZ = 0L
    for (w in 9 downTo 1) {
      newZ = termsToCalcZ[i].calculate(mapOf("z" to Literal(z), "w" to Literal(w.toLong())))
      if (newZ in zRanges[i + 1]) {
        highestValidNumber += w
        break
      }
    }
    z = newZ
  }

  // part2:
  z = 0L
  var lowestValidNumber = ""
  for (i in 0..13) {
    var newZ = 0L
    for (w in 1..9) {
      newZ = termsToCalcZ[i].calculate(mapOf("z" to Literal(z), "w" to Literal(w.toLong())))
      if (newZ in zRanges[i + 1]) {
        lowestValidNumber += w
        break
      }
    }
    z = newZ
  }

  println("Part1: $highestValidNumber")
  println("Part2: $lowestValidNumber")
}

fun parseTerm(instructions: List<String>): Term {
  var result: Term = Variable("z")
  for (instruction in instructions.reversed()) {
    val (op, a, b) = instruction.split(" ")
    result =
        when (op) {
          "init" -> result.replaceAll(a, b.toTerm())
          else -> result.replaceAll(a, instruction.toTerm())
        }
  }
  return result
}

fun String.toTerm(): Term {
  return when {
    this.matches("^(-)?\\d+$".toRegex()) -> Literal(this.toLong())
    this.matches("^[a-zA-Z]+$".toRegex()) -> Variable(this)
    this.matches("^(add|mul|div|mod|eql|noteql|init) [a-z] ([a-z]|(-)?\\d+)$".toRegex()) -> {
      val (op, a, b) = this.split(" ")
      when (op) {
        "add" -> Addition(a.toTerm(), b.toTerm())
        "mul" -> Multiplication(a.toTerm(), b.toTerm())
        "div" -> Division(a.toTerm(), b.toTerm())
        "mod" -> Modulo(a.toTerm(), b.toTerm())
        "eql" -> Equality(a.toTerm(), b.toTerm())
        "noteql" -> NotEquality(a.toTerm(), b.toTerm())
        else -> throw IllegalArgumentException("unknown operation '$op'")
      }
    }
    else -> throw IllegalArgumentException("'$this' is not a term")
  }
}

sealed class Term {
  abstract fun calculate(variables: Map<String, Term>): Long

  abstract fun replaceAll(variableName: String, withTerm: Term): Term
}

data class Variable(val name: String) : Term() {
  override fun calculate(variables: Map<String, Term>): Long {
    val variable =
        variables[name] ?: throw IllegalStateException("variable '$name' not initialized")
    return variable.calculate(variables.filterNot { it.key == name })
  }

  override fun replaceAll(variableName: String, withTerm: Term) =
      if (variableName == name) {
        withTerm
      } else {
        this
      }

  override fun toString() = name
}

data class Literal(val value: Long) : Term() {
  override fun calculate(variables: Map<String, Term>): Long {
    return value
  }

  override fun replaceAll(variableName: String, withTerm: Term) = this

  override fun toString() = value.toString()
}

class Addition(val a: Term, val b: Term) : Term() {
  override fun calculate(variables: Map<String, Term>): Long {
    return a.calculate(variables) + b.calculate(variables)
  }

  override fun replaceAll(variableName: String, withTerm: Term) =
      Addition(a.replaceAll(variableName, withTerm), b.replaceAll(variableName, withTerm))

  override fun toString() = "($a+$b)"
}

class Multiplication(val a: Term, val b: Term) : Term() {
  override fun calculate(variables: Map<String, Term>): Long {
    return a.calculate(variables) * b.calculate(variables)
  }

  override fun replaceAll(variableName: String, withTerm: Term) =
      Multiplication(a.replaceAll(variableName, withTerm), b.replaceAll(variableName, withTerm))

  override fun toString() = "($a*$b)"
}

class Division(val a: Term, val b: Term) : Term() {
  override fun calculate(variables: Map<String, Term>): Long {
    val bResolved = b.calculate(variables)
    if (bResolved == 0L) throw IllegalArgumentException("tried div with b=0")
    return a.calculate(variables) / bResolved
  }

  override fun replaceAll(variableName: String, withTerm: Term) =
      Division(a.replaceAll(variableName, withTerm), b.replaceAll(variableName, withTerm))

  override fun toString() = "($a/$b)"
}

class Modulo(val a: Term, val b: Term) : Term() {
  override fun calculate(variables: Map<String, Term>): Long {
    val aResolved = a.calculate(variables)
    val bResolved = b.calculate(variables)
    if (aResolved < 0L) throw IllegalArgumentException("tried mod with a<0")
    if (bResolved <= 0L) throw IllegalArgumentException("tried mod with b<=0")
    return aResolved % bResolved
  }

  override fun replaceAll(variableName: String, withTerm: Term) =
      Modulo(a.replaceAll(variableName, withTerm), b.replaceAll(variableName, withTerm))

  override fun toString() = "($a%$b)"
}

class Equality(val a: Term, val b: Term) : Term() {
  override fun calculate(variables: Map<String, Term>): Long {
    return if (a.calculate(variables) == b.calculate(variables)) 1 else 0
  }

  override fun replaceAll(variableName: String, withTerm: Term) =
      Equality(a.replaceAll(variableName, withTerm), b.replaceAll(variableName, withTerm))

  override fun toString() = "($a==$b)"
}

class NotEquality(val a: Term, val b: Term) : Term() {
  override fun calculate(variables: Map<String, Term>): Long {
    return if (a.calculate(variables) != b.calculate(variables)) 1 else 0
  }

  override fun replaceAll(variableName: String, withTerm: Term) =
      NotEquality(a.replaceAll(variableName, withTerm), b.replaceAll(variableName, withTerm))

  override fun toString() = "($a!=$b)"
}

fun optimizeInstructions(instructions: List<String>): List<String> {
  val optimizedInstructions = mutableListOf<String>()
  var i = 0
  while (i < instructions.size) {
    val instruction = instructions[i]
    if (instruction.matches("^inp .$".toRegex())) {
      optimizedInstructions.add(instruction)
      i++
      continue
    }
    val (_, a, b) = instruction.split(" ")
    when {
      instruction.matches("^div . 1$".toRegex()) -> {} // useless division
      instruction.matches("^mul . 0$".toRegex()) -> { // effectively sets variable to 0
        if (i < instructions.lastIndex) {
          val nextInstruction = instructions[i + 1]
          val (_, _, b2) = nextInstruction.split(" ")
          if (nextInstruction.matches("^add $a .+$".toRegex())) {
            // effectively sets variable to b of next instruction
            optimizedInstructions.add("init $a $b2")
            i++
          } else {
            optimizedInstructions.add("init $a 0")
          }
        } else {
          optimizedInstructions.add("init $a 0")
        }
      }
      instruction.matches("^eql . .$".toRegex()) -> {
        if (i < instructions.lastIndex) {
          val nextInstruction = instructions[i + 1]
          if (nextInstruction.matches("^eql $a 0$".toRegex())) { // effectively check for not equal
            optimizedInstructions.add("noteql $a $b")
            i++
          } else {
            optimizedInstructions.add(instruction)
          }
        } else {
          optimizedInstructions.add(instruction)
        }
      }
      else -> optimizedInstructions.add(instruction)
    }
    i++
  }
  return optimizedInstructions
}
