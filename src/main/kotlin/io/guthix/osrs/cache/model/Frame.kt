/*
 * Copyright (C) 2019 Guthix
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.guthix.osrs.cache.model

import io.guthix.cache.js5.io.skip
import io.guthix.cache.js5.io.smallSmart
import io.guthix.cache.js5.io.uByte
import io.guthix.cache.js5.io.uShort
import java.nio.ByteBuffer


// Unfinished
class Frame(
    val frameMapId: Int,
    val translateX: IntArray,
    val translateY: IntArray,
    val translateZ: IntArray,
    val translateCount: Int?,
    val indexFrameIds: IntArray,
    val showing: Boolean
) {
    companion object {
        @ExperimentalUnsignedTypes
        fun decode(frameMaps: FrameMap, buffer: ByteBuffer): Frame {
            val data = buffer.duplicate()
            val frameMapId = buffer.uShort
            val length = buffer.uByte.toInt()
            data.skip(3 + length)
            val indexFrameIds = IntArray(500)
            val translatorX = IntArray(500)
            val translatorY = IntArray(500)
            val translatorZ = IntArray(500)

            var lastI = -1
            var index = 0
            var showing = false
            for (i in 0 until length) {
                val opcode = buffer.uByte.toInt()

                if (opcode <= 0) {
                    continue
                }

                indexFrameIds[index] = i
                if (frameMaps.types[i].toInt() != 0) {
                    for(j in i - 1 downTo lastI + 1) {
                        if (frameMaps.types[j].toInt() == 0) {
                            indexFrameIds[index] = j
                            translatorX[index] = 0
                            translatorY[index] = 0
                            translatorZ[index] = 0
                            index++
                            break
                        }

                    }
                }

                var var11 = 0
                if (frameMaps.types[i].toInt() == 3) {
                    var11 = 128
                }

                if (opcode and 1 != 0) {
                    translatorX[index] = data.smallSmart.toInt()
                } else {
                    translatorX[index] = var11
                }

                if (opcode and 2 != 0) {
                    translatorY[index] = data.smallSmart.toInt()
                } else {
                    translatorY[index] = var11
                }

                if (opcode and 4 != 0) {
                    translatorZ[index] = data.smallSmart.toInt()
                } else {
                    translatorZ[index] = var11
                }
                if (frameMaps.types[i].toInt() == 5) {
                    showing = true
                }
                lastI = i
                index++
            }
            return Frame(frameMapId.toInt(), translatorX, translatorY, translatorZ, length, indexFrameIds, showing)
        }
    }
}