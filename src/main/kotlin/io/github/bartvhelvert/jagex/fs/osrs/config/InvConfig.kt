package io.github.bartvhelvert.jagex.fs.osrs.config

import io.github.bartvhelvert.jagex.fs.io.uByte
import io.github.bartvhelvert.jagex.fs.io.uShort
import io.github.bartvhelvert.jagex.fs.osrs.Config
import io.github.bartvhelvert.jagex.fs.osrs.ConfigCompanion
import java.nio.ByteBuffer

class InvConfig @ExperimentalUnsignedTypes constructor(id: Int, val capacity: UShort) : Config(id) {
    @ExperimentalUnsignedTypes
    override fun encode(): ByteBuffer = if(capacity.toInt() != 0) {
        ByteBuffer.allocate(4).apply {
            put(2)
            putShort(capacity.toShort())
            put(0)
        }
    } else {
        ByteBuffer.allocate(1).apply {
            put(0)
        }
    }

    companion object : ConfigCompanion<InvConfig>() {
        override val id = 5

        @ExperimentalUnsignedTypes
        override fun decode(id: Int, buffer: ByteBuffer): InvConfig {
            var capacity: UShort = 0u
            decoder@ while (true) {
                val opcode = buffer.uByte.toInt()
                when (opcode) {
                    0 -> break@decoder
                    2 -> capacity = buffer.uShort
                }
            }
            return InvConfig(id, capacity)
        }
    }
}