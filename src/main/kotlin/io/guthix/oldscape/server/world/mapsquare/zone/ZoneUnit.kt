/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.mapsquare.zone

import io.guthix.oldscape.server.world.mapsquare.MapsquareUnit
import io.guthix.oldscape.server.world.mapsquare.zone.tile.TileUnit
import kotlin.math.abs

fun abs(n: ZoneUnit) = abs(n.value).zones

val Int.zones get() = ZoneUnit(this)

inline class ZoneUnit(val value: Int): Comparable<ZoneUnit> {
    val inTiles get() = TileUnit(value * SIZE_TILE.value)
    val inMapsquares get() = MapsquareUnit(value / MapsquareUnit.SIZE_ZONE.value)
    val relativeMapSquare get() = ZoneUnit(value % MapsquareUnit.SIZE_ZONE.value)

    operator fun plus(other: ZoneUnit) = ZoneUnit(value + other.value)
    operator fun minus(other: ZoneUnit) = ZoneUnit(value - other.value)
    operator fun times(other: ZoneUnit) = ZoneUnit(value * other.value)
    operator fun div(other: ZoneUnit) = ZoneUnit(value / other.value)
    operator fun rem(other: ZoneUnit) = ZoneUnit(value % other.value)
    operator fun inc() = ZoneUnit(value + 1)
    operator fun dec() = ZoneUnit(value - 1)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = ZoneUnit(-value)
    operator fun rangeTo(other: ZoneUnit) = ZoneUnitRange(this, other)
    override fun compareTo(other: ZoneUnit): Int = when {
        value < other.value -> -1
        value > other.value -> 1
        else -> 0
    }

    companion object {
        val SIZE_TILE = TileUnit(8)
    }
}

infix fun ZoneUnit.until(to: ZoneUnit): ZoneUnitRange {
    return this..(to - 1.zones)
}

class ZoneUnitRange(
    start: ZoneUnit,
    endInclusive: ZoneUnit
) : ZoneUnitProgression(start, endInclusive, 1), ClosedRange<ZoneUnit> {
    override val start get() = first
    override val endInclusive get() = last
    override fun contains(value: ZoneUnit): Boolean = value.value in first.value..last.value
    override fun isEmpty() = first > last
    override fun equals(other: Any?): Boolean = other is ZoneUnitRange &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last)
    override fun hashCode() = if (isEmpty()) -1 else (31 * first.value + last.value)
    override fun toString() = "$first..$last"
}

open class ZoneUnitProgression(start: ZoneUnit, endInclusive: ZoneUnit, var step: Int = 1) : Iterable<ZoneUnit> {
    init {
        require(step != 0) { "Step must be non-zero." }
        require(step != Int.MIN_VALUE) {
            "Step must be greater than Int.MIN_VALUE to avoid overflow on negation."
        }
    }
    val first: ZoneUnit = start
    val last: ZoneUnit = getProgressionLastElement(start, endInclusive, step)
    override fun iterator() = ZoneUnitProgressionIterator(first, last, step)
    open fun isEmpty() = if (step > 0) first > last else first < last
    override fun equals(other: Any?) = other is ZoneUnitProgression &&
        (isEmpty() && other.isEmpty() || first == other.first && last == other.last && step == other.step)
    override fun hashCode() = if (isEmpty()) -1 else (31 * (31 * first.value + last.value) + step)
    override fun toString() = if (step > 0) "$first..$last step $step" else "$first downTo $last step ${-step}"
    infix fun step(step: TileUnit) = this.apply { this.step = step.inZones.value }
    infix fun step(step: ZoneUnit) = this.apply { this.step = step.value }
    infix fun step(step: MapsquareUnit) = this.apply { this.step = step.inZones.value }
}

class ZoneUnitProgressionIterator(first: ZoneUnit, last: ZoneUnit, private val step: Int) : Iterator<ZoneUnit> {
    private val finalElement = last
    private var hasNext = if (step > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement
    override fun hasNext() = hasNext
    override fun next(): ZoneUnit {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw NoSuchElementException()
            hasNext = false
        } else {
            next += step.zones
        }
        return value
    }
}

private fun getProgressionLastElement(start: ZoneUnit, end: ZoneUnit, step: Int): ZoneUnit = when {
    step > 0 -> if (start >= end) end else end - differenceModulo(end, start, step.zones)
    step < 0 -> if (start <= end) end else end + differenceModulo(start, end, (-step).zones)
    else -> throw IllegalArgumentException("Step is zero.")
}

private fun differenceModulo(a: ZoneUnit, b: ZoneUnit, c: ZoneUnit): ZoneUnit {
    val ac = a % c
    val bc = b % c
    return if (ac >= bc) ac - bc else ac - bc + c
}