/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.net.state.login

import io.guthix.oldscape.server.net.PacketInboundHandler
import io.guthix.oldscape.server.net.StatusEncoder
import io.guthix.oldscape.server.net.StatusResponse
import io.guthix.oldscape.server.world.World
import io.netty.channel.ChannelHandlerContext

class LoginHandler(val world: World, val sessionId: Long) : PacketInboundHandler<LoginRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: LoginRequest) {
        if(msg.sessionId != sessionId) {
            ctx.writeAndFlush(StatusResponse.BAD_SESSION_ID)
            return
        }
        if(world.isFull) {
            ctx.writeAndFlush(StatusResponse.SERVER_FULL)
        }
        ctx.write(StatusResponse.NORMAL)
        ctx.pipeline().replace(StatusEncoder::class.qualifiedName, LoginEncoder::class.qualifiedName, LoginEncoder())
        world.loginQueue.add(msg)
    }
}