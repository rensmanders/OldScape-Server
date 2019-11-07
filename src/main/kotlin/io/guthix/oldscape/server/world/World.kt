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
package io.guthix.oldscape.server.world

import io.guthix.oldscape.server.net.StatusResponse
import io.guthix.oldscape.server.net.state.login.LoginRequest
import io.guthix.oldscape.server.net.state.login.LoginResponse
import io.guthix.oldscape.server.world.entity.player.PlayerList
import java.util.*
import java.util.concurrent.*

class World : TimerTask() {
    internal val loginQueue = SynchronousQueue<LoginRequest>()

    internal val players = PlayerList(MAX_PLAYERS)

    val isFull get(): Boolean = players.freeSpace + loginQueue.size >= MAX_PLAYERS

    override fun run() {
        processLogins()
    }

    private fun processLogins() {
        while(loginQueue.isNotEmpty()) {
            val request = loginQueue.poll()
            val player= players.create(request)
            request.ctx.writeAndFlush(LoginResponse(player.index, player.rights))
        }
    }

    companion object {
        const val MAX_PLAYERS = 2048
    }
}