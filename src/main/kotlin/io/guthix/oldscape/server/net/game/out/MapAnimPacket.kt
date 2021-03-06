/*
 * Copyright 2018-2020 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape.server.net.game.out

import io.guthix.buffer.writeByteAdd
import io.guthix.buffer.writeShortAdd
import io.guthix.oldscape.server.net.game.FixedSize
import io.guthix.oldscape.server.net.game.ZoneOutGameEvent
import io.guthix.oldscape.server.world.map.dim.FloorUnit
import io.guthix.oldscape.server.world.map.dim.TileUnit
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class MapAnimPacket(
    private val id: Int,
    private val delay: Int,
    private val floor: FloorUnit,
    localX: TileUnit,
    localY: TileUnit
) : ZoneOutGameEvent(localX, localY) {
    override val opcode: Int = 80

    override val enclOpcode: Int = 3

    override val size: FixedSize = FixedSize(STATIC_SIZE)

    override fun encode(ctx: ChannelHandlerContext): ByteBuf {
        val buf = ctx.alloc().buffer(STATIC_SIZE)
        buf.writeByteAdd(floor.value)
        buf.writeShort(id)
        buf.writeByteAdd(posBitPack)
        buf.writeShortAdd(delay)
        return buf
    }

    companion object {
        const val STATIC_SIZE: Int = Byte.SIZE_BYTES + Short.SIZE_BYTES + Short.SIZE_BYTES + Byte.SIZE_BYTES
    }
}