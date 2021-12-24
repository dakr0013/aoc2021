package com.example

fun main() {
  val instructions = readLinesString(24).map { Instruction.parse(it) }
  val optimized = optimize(instructions)

  println("num of instructions          : ${instructions.size}")
  println("optimized num of instructions: ${optimized.size}")
  //  val alu = ArithmeticLogicUnit()
  //  var largestModelNumber = 0L
  //  var time = System.currentTimeMillis()
  //  for (modelNumber in 9999_99999_99999L downTo 1111_11111_11111L) {
  //    if (System.currentTimeMillis() - time > 10 * 1000) {
  //      time = System.currentTimeMillis()
  //      println(modelNumber)
  //    }
  //    if (alu.validate(modelNumber, instructions)) {
  //      largestModelNumber = modelNumber
  //      break
  //    }
  //  }
  //  println(largestModelNumber)
}

fun optimize(instructions: List<Instruction>): List<Instruction> {
  val optimizedInstructions = mutableListOf<Instruction>()
  var i = 0
  while (i < instructions.size) {
    val instruction = instructions[i]
    when {
      instruction is Div &&
          instruction.variable2 is Value &&
          instruction.variable2.value == 1L -> {}
      instruction is Mul && instruction.variable2 is Value && instruction.variable2.value == 0L -> {

        if (i < instructions.lastIndex) {
          val nextInstruction = instructions[i + 1]
          if (nextInstruction is Add && instruction.variable1 == nextInstruction.variable1) {
            optimizedInstructions.add(Init(instruction.variable1, nextInstruction.variable2))
            i++
          } else {
            optimizedInstructions.add(Init(instruction.variable1, instruction.variable2))
          }
        } else {
          optimizedInstructions.add(Init(instruction.variable1, instruction.variable2))
        }
      }
      instruction is Eql -> {
        if (i < instructions.lastIndex) {
          val nextInstruction = instructions[i + 1]
          if (nextInstruction is Eql &&
              instruction.variable1 == nextInstruction.variable1 &&
              nextInstruction.variable2 is Value &&
              nextInstruction.variable2.value == 0L) {
            optimizedInstructions.add(NotEql(instruction.variable1, instruction.variable2))
            i++
          } else {
            optimizedInstructions.add(Init(instruction.variable1, instruction.variable2))
          }
        } else {
          optimizedInstructions.add(Init(instruction.variable1, instruction.variable2))
        }
      }
      else -> {
        optimizedInstructions.add(instruction)
      }
    }
    i++
  }
  return optimizedInstructions
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

  fun printFormula(monadProgram: List<Instruction>) {
    val modelNumberDigits = (1..14).map { "m[$it]" }.toMutableList()
    var zFormula = "z=0"
    for (instruction in monadProgram.reversed()) {
      when (instruction) {
        is Input -> {
          zFormula =
              zFormula.replace(
                  instruction.toVariable.name.toString(),
                  modelNumberDigits.removeLast(),
              )
          break
        }
        is Add -> {
          val a = instruction.variable1.toString()
          val b = instruction.variable2.toString()
          zFormula = zFormula.replace(a, "($a+$b)")
        }
        is Mul -> {
          val a = instruction.variable1.toString()
          val b = instruction.variable2.toString()
          zFormula = zFormula.replace(a, "($a*$b)")
        }
        is Div -> {
          val a = instruction.variable1.toString()
          val b = instruction.variable2.toString()
          zFormula = zFormula.replace(a, "($a/$b)")
        }
        is Mod -> {
          val a = instruction.variable1.toString()
          val b = instruction.variable2.toString()
          zFormula = zFormula.replace(a, "($a%$b)")
        }
        is Eql -> {
          val a = instruction.variable1.toString()
          val b = instruction.variable2.toString()
          zFormula = zFormula.replace(a, "($a==$b)")
        }
        is NotEql -> {
          val a = instruction.variable1.toString()
          val b = instruction.variable2.toString()
          zFormula = zFormula.replace(a, "($a!=$b)")
        }
        is Init -> {
          val a = instruction.variable1.toString()
          val b = instruction.variable2.toString()
          zFormula = zFormula.replace(a, b)
        }
      }
    }

    println(zFormula)
  }

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

abstract class Instruction {
  abstract fun execute(variables: MutableMap<Char, Long>)

  abstract fun asString(): String

  override fun toString() = asString()

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

class Input(val toVariable: Variable) : Instruction() {
  override fun execute(variables: MutableMap<Char, Long>) {
    val modelNumber = variables['m']!!.toString()
    val index = variables['i']!!.toInt()
    if (index > modelNumber.lastIndex)
        throw IndexOutOfBoundsException("index larger than model number")
    val nextInputDigit = modelNumber[index].digitToInt().toLong()
    variables[toVariable.name] = nextInputDigit
    variables['i'] = variables['i']!! + 1
  }

  override fun asString(): String {
    return "inp $toVariable"
  }
}

class Add(val variable1: Variable, val variable2: VariableOrValue) : Instruction() {
  override fun execute(variables: MutableMap<Char, Long>) {
    val a = variables[variable1.name]!!
    val b =
        when (variable2) {
          is Variable -> variables[variable2.name]!!
          is Value -> variable2.value
        }
    variables[variable1.name] = a + b
  }

  override fun asString(): String {
    return "add $variable1 $variable2"
  }
}

class Mul(val variable1: Variable, val variable2: VariableOrValue) : Instruction() {
  override fun execute(variables: MutableMap<Char, Long>) {
    val a = variables[variable1.name]!!
    val b =
        when (variable2) {
          is Variable -> variables[variable2.name]!!
          is Value -> variable2.value
        }
    variables[variable1.name] = a * b
  }

  override fun asString(): String {
    return "mul $variable1 $variable2"
  }
}

class Div(val variable1: Variable, val variable2: VariableOrValue) : Instruction() {
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

  override fun asString(): String {
    return "div $variable1 $variable2"
  }
}

class Mod(val variable1: Variable, val variable2: VariableOrValue) : Instruction() {
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

  override fun asString(): String {
    return "mod $variable1 $variable2"
  }
}

class Eql(val variable1: Variable, val variable2: VariableOrValue) : Instruction() {
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

  override fun asString(): String {
    return "eql $variable1 $variable2"
  }
}

class NotEql(val variable1: Variable, val variable2: VariableOrValue) : Instruction() {
  override fun execute(variables: MutableMap<Char, Long>) {
    val a = variables[variable1.name]!!
    val b =
        when (variable2) {
          is Variable -> variables[variable2.name]!!
          is Value -> variable2.value
        }
    variables[variable1.name] =
        if (a != b) {
          1
        } else {
          0
        }
  }

  override fun asString(): String {
    return "eql $variable1 $variable2"
  }
}

class Init(val variable1: Variable, val variable2: VariableOrValue) : Instruction() {
  override fun execute(variables: MutableMap<Char, Long>) {
    val b =
        when (variable2) {
          is Variable -> variables[variable2.name]!!
          is Value -> variable2.value
        }
    variables[variable1.name] = b
  }

  override fun asString(): String {
    return "init $variable1 $variable2"
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

@JvmInline
value class Variable(val name: Char) : VariableOrValue {
  override fun toString(): String {
    return name.toString()
  }
}

@JvmInline
value class Value(val value: Long) : VariableOrValue {
  override fun toString(): String {
    return value.toString()
  }
}
