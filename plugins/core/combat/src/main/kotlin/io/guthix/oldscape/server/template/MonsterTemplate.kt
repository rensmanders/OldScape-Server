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
package io.guthix.oldscape.server.template

import io.guthix.oldscape.server.Property
import io.guthix.oldscape.server.template.type.NpcTemplate
import io.guthix.oldscape.server.world.entity.Npc

public val Npc.lvl: Int? get() = monsterTemplate?.lvl

public val Npc.maxHit: Int? get() = monsterTemplate?.maxHit

public val Npc.attackType: AttackType? get() = monsterTemplate?.attackType

public val Npc.isAggressive: Boolean? get() = monsterTemplate?.isAggressive

public val Npc.isPoisonous: Boolean? get() = monsterTemplate?.isPoisonous

public val Npc.isImmumePoison: Boolean? get() = monsterTemplate?.isImmumePoison

public val Npc.isImmuneVenom: Boolean? get() = monsterTemplate?.isImmuneVenom

public val Npc.attackSpeed: Int? get() = monsterTemplate?.attackSpeed

public val Npc.sequences: CombatSequences? get() = monsterTemplate?.sequences

public val Npc.stats: CombatStats? get() = monsterTemplate?.stats

public val Npc.attackBonus: CombatBonus? get() = monsterTemplate?.attackBonus

public val Npc.strengthBonus: CombatBonus? get() = monsterTemplate?.strengthBonus

public val Npc.defensiveStats: StyleBonus? get() = monsterTemplate?.defensiveStats

internal val Npc.monsterTemplate: MonsterTemplate? get() = template.monster

internal val NpcTemplate.monster: MonsterTemplate? by Property { null }

data class MonsterTemplate(
    val lvl: Int,
    val maxHit: Int?,
    val attackType: AttackType?,
    val isAggressive: Boolean,
    val isPoisonous: Boolean,
    val isImmumePoison: Boolean,
    val isImmuneVenom: Boolean,
    val attackSpeed: Int?,
    val sequences: CombatSequences?,
    val stats: CombatStats,
    val attackBonus: CombatBonus,
    val strengthBonus: CombatBonus,
    val defensiveStats: StyleBonus
)

data class CombatStats(
    val health: Int,
    val attack: Int,
    val strength: Int,
    val defence: Int,
    val range: Int,
    val magic: Int
)

data class CombatSequences(
    val spawn: Int? = null,
    val attack: Int,
    val defence: Int,
    val death: Int
)

enum class AttackType { STAB, SLASH, CRUSH, RANGED, MAGIC, NONE }