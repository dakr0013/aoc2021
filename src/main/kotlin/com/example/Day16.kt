package com.example

fun main() {
  val hexInput = readLinesString(16).first()
  val binaryInput = hexInput.map { it.toBinary() }.joinToString("")
  val buffer = BinaryBuffer(binaryInput)
  val packet = decode(buffer)
  println("Part1: ${packet.versionSum()}")
  println("Part2: ${packet.value()}")
}

fun decode(buffer: BinaryBuffer): Packet {
  val version = buffer.number(3)
  when (val typeId = buffer.number(3)) {
    4 -> {
      val value = buffer.literalValue()
      return LiteralValuePacket(version, typeId, value)
    }
    else -> {
      val lengthTypeId = buffer.number(1)
      val packet = OperatorPacket(version, typeId, mutableListOf())
      if (lengthTypeId == 1) {
        val numberOfSubPackets = buffer.number(11)
        for (i in 1..numberOfSubPackets) {
          packet.subPackets.add(decode(buffer))
        }
      } else {
        var overallSizeOfSubPackets = buffer.number(15)
        while (overallSizeOfSubPackets > 0) {
          val offsetBegin = buffer.offset
          packet.subPackets.add(decode(buffer))
          val offsetEnd = buffer.offset
          val packetSize = offsetEnd - offsetBegin
          overallSizeOfSubPackets -= packetSize
        }
      }
      return packet
    }
  }
}

abstract class Packet(val version: Int, val typeId: Int) {
  abstract fun versionSum(): Int
  abstract fun value(): Long
}

class LiteralValuePacket(version: Int, typeId: Int, val value: Long) : Packet(version, typeId) {
  override fun versionSum() = version
  override fun value() = value

  override fun toString(): String {
    return "LiteralValuePacket(value=$value)"
  }
}

class OperatorPacket(version: Int, typeId: Int, val subPackets: MutableList<Packet>) :
    Packet(version, typeId) {
  override fun versionSum() = subPackets.sumOf { it.versionSum() } + version
  override fun value(): Long {
    return when (typeId) {
      0 -> subPackets.sumOf { it.value() }
      1 -> subPackets.map { it.value() }.reduce { acc, l -> acc * l }
      2 -> subPackets.minOf { it.value() }
      3 -> subPackets.maxOf { it.value() }
      5 ->
          if (subPackets.first().value() > subPackets.last().value()) {
            1
          } else {
            0
          }
      6 ->
          if (subPackets.first().value() < subPackets.last().value()) {
            1
          } else {
            0
          }
      7 ->
          if (subPackets.first().value() == subPackets.last().value()) {
            1
          } else {
            0
          }
      else -> throw IllegalStateException("Not reachable")
    }
  }
  override fun toString(): String {
    return "OperatorPacket(subpackets=$subPackets)"
  }
}

class BinaryBuffer(private val input: String) {
  var offset = 0
    private set

  fun number(bitLength: Int): Int {
    if (bitLength in 1..31) {
      val newOffset = offset + bitLength
      val binary = input.substring(offset, newOffset)
      offset = newOffset
      return binary.toInt(2)
    } else {
      throw IllegalArgumentException("bit length too big")
    }
  }

  fun literalValue(): Long {
    var isLastGroup = false
    var binary = ""
    while (!isLastGroup) {
      if (input[offset++] == '0') {
        isLastGroup = true
      }
      val newOffset = offset + 4
      val group = input.substring(offset, newOffset)
      binary += group
      offset = newOffset
    }
    return binary.toLong(2)
  }
}

fun Char.toBinary(): String {
  return when (this) {
    '0' -> "0000"
    '1' -> "0001"
    '2' -> "0010"
    '3' -> "0011"
    '4' -> "0100"
    '5' -> "0101"
    '6' -> "0110"
    '7' -> "0111"
    '8' -> "1000"
    '9' -> "1001"
    'A' -> "1010"
    'B' -> "1011"
    'C' -> "1100"
    'D' -> "1101"
    'E' -> "1110"
    'F' -> "1111"
    else -> throw IllegalArgumentException("char is not a hexadecimal char")
  }
}
