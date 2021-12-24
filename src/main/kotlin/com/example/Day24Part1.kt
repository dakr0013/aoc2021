package com.example

fun main() {
  val instructions = readLinesString(24).map { Instruction.parse(it) }
  val alu = ArithmeticLogicUnit()

  var largestModelNumber = 0L
  var time = System.currentTimeMillis()
  for (modelNumber in 9999_99999_99999L downTo 1111_11111_11111L) {
    if(System.currentTimeMillis() - time > 10* 1000) {
      time = System.currentTimeMillis()
      println(modelNumber)
    }
    if (alu.validate(modelNumber, instructions)) {
      largestModelNumber = modelNumber
      break
    }
  }
  println(largestModelNumber)
}

class ArithmeticLogicUnit {
  private val variables =
      mutableMapOf(
          'm' to 0L,
          'i' to 0L,
          'w' to 0L,
          'x' to 0L,
          'y' to 0L,
          'z' to 0L,
      )

  fun validate(modelNumber: Long, monadProgram: List<Instruction>): Boolean {
    if (modelNumber.toString().any { it !in '1'..'9' }) return false
    resetVariables()
    variables['m'] = modelNumber
    run(monadProgram)
    return variables['z'] == 0L
  }

  private fun resetVariables() {
    variables['m'] = 0
    variables['i'] = 0
    variables['w'] = 0
    variables['x'] = 0
    variables['y'] = 0
    variables['z'] = 0
  }

  private fun run(instructions: List<Instruction>) {
    for (instruction in instructions) {
      instruction.execute(variables)
    }
  }

  fun printVariables() {
    println("m=${variables['m']}")
    println("i=${variables['i']}")
    println("w=${variables['w']},x=${variables['x']},y=${variables['y']},z=${variables['z']}")
  }
}

interface Instruction {
  fun execute(variables: MutableMap<Char, Long>)

  companion object {
    fun parse(input: String): Instruction {
      val instruction = input.split(" ").first()
      val firstVariable = Variable(input.split(" ")[1].toCharArray().first())
      return when (instruction) {
        "inp" -> {
          Input(firstVariable)
        }
        else -> {
          val secondVariableOrValue = VariableOrValue.create(input.split(" ").last())
          when (instruction) {
            "add" -> Add(firstVariable, secondVariableOrValue)
            "mul" -> Mul(firstVariable, secondVariableOrValue)
            "div" -> Div(firstVariable, secondVariableOrValue)
            "mod" -> Mod(firstVariable, secondVariableOrValue)
            "eql" -> Eql(firstVariable, secondVariableOrValue)
            else -> throw IllegalArgumentException("unknown instruction")
          }
        }
      }
    }
  }
}

data class Input(val toVariable: Variable) : Instruction {
  override fun execute(variables: MutableMap<Char, Long>) {
    val modelNumber = variables['m']!!.toString()
    val index = variables['i']!!.toInt()
    if (index > modelNumber.lastIndex)
        throw IndexOutOfBoundsException("index larger than model number")
    val nextInputDigit = modelNumber[index].digitToInt().toLong()
    variables[toVariable.name] = nextInputDigit
    variables['i'] = variables['i']!! + 1
  }
}

data class Add(val variable1: Variable, val variable2: VariableOrValue) : Instruction {
  override fun execute(variables: MutableMap<Char, Long>) {
    val a = variables[variable1.name]!!
    val b =
        when (variable2) {
          is Variable -> variables[variable2.name]!!
          is Value -> variable2.value
        }
    variables[variable1.name] = a + b
  }
}

data class Mul(val variable1: Variable, val variable2: VariableOrValue) : Instruction {
  override fun execute(variables: MutableMap<Char, Long>) {
    val a = variables[variable1.name]!!
    val b =
        when (variable2) {
          is Variable -> variables[variable2.name]!!
          is Value -> variable2.value
        }
    variables[variable1.name] = a * b
  }
}

data class Div(val variable1: Variable, val variable2: VariableOrValue) : Instruction {
  override fun execute(variables: MutableMap<Char, Long>) {
    val a = variables[variable1.name]!!
    val b =
        when (variable2) {
          is Variable -> variables[variable2.name]!!
          is Value -> variable2.value
        }
    if (b == 0L) throw IllegalArgumentException("tried executing div with  b=0")
    variables[variable1.name] = a / b
  }
}

data class Mod(val variable1: Variable, val variable2: VariableOrValue) : Instruction {
  override fun execute(variables: MutableMap<Char, Long>) {
    val a = variables[variable1.name]!!
    val b =
        when (variable2) {
          is Variable -> variables[variable2.name]!!
          is Value -> variable2.value
        }
    if (a < 0L) throw IllegalArgumentException("tried executing mod with a<0")
    if (b <= 0L) throw IllegalArgumentException("tried executing mod with b<=0")
    variables[variable1.name] = a % b
  }
}

data class Eql(val variable1: Variable, val variable2: VariableOrValue) : Instruction {
  override fun execute(variables: MutableMap<Char, Long>) {
    val a = variables[variable1.name]!!
    val b =
        when (variable2) {
          is Variable -> variables[variable2.name]!!
          is Value -> variable2.value
        }
    variables[variable1.name] =
        if (a == b) {
          1
        } else {
          0
        }
  }
}

sealed interface VariableOrValue {
  companion object {
    fun create(input: String): VariableOrValue {
      return if (input.matches("^(-)?\\d+$".toRegex())) {
        Value(input.toLong())
      } else {
        Variable(input.toCharArray().first())
      }
    }
  }
}

@JvmInline value class Variable(val name: Char) : VariableOrValue

@JvmInline value class Value(val value: Long) : VariableOrValue
