/*
 * This file is part of Guthix OldScape-Server.
 *
 * Guthix OldScape-Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape-Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.cache

import io.guthix.cache.js5.Js5Cache
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.LocationConfig
import io.guthix.oldscape.cache.config.NamedConfig
import io.guthix.oldscape.cache.config.NpcConfig
import io.guthix.oldscape.cache.config.ObjectConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path

class IdentifierGenerator : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.withType(JavaPlugin::class.java) {
            val sourceSets = target.properties["sourceSets"] as SourceSetContainer
            sourceSets.getByName("main").java.srcDir("src/main/generated")
        }
        val task = target.task("sourceGen") { task ->
            task.doFirst {
                val sourceRoot = createSourceTree(target)
                val ds = Js5DiskStore.open(File("${target.projectDir}/src/main/resources/cache").toPath())
                val cache = Js5Cache(ds)
                val configArchive = cache.readArchive(ConfigArchive.id)
                val objs = ObjectConfig.load(configArchive.readGroup(ObjectConfig.id))
                val npcs = NpcConfig.load(configArchive.readGroup(NpcConfig.id))
                val locs = LocationConfig.load(configArchive.readGroup(LocationConfig.id))
                sourceRoot.toFile().mkdirs()
                generateSource(sourceRoot, "LocId", locs)
                generateSource(sourceRoot, "NpcId", npcs)
                generateSource(sourceRoot, "ObjId", objs)
            }
        }
        val compileKotlinTask = target.tasks.getByName("compileKotlin")
        compileKotlinTask.dependsOn(task)
    }

    private fun createSourceTree(target: Project): Path {
        val srcDir = File("${target.projectDir}/src/main/generated").toPath()
        return srcDir.resolve(packageDir)
    }

    private fun generateSource(root: Path, name: String, configs: Map<Int, NamedConfig>) {
        val codeFile = root.resolve("$name.kt").toFile()
        codeFile.createNewFile()
        PrintWriter(codeFile).apply {
            println("/* This file is automatically generated by ${IdentifierGenerator::class.qualifiedName}. */")
            println("package $packageName")
            println()
            println("@Suppress(\"ObjectPropertyName\")")
            println("object $name {")
            for ((id, config) in configs) {
                val identifier = configNameToIdentifier(id, config.name)
                if (identifier.contains("null", ignoreCase = true)) continue
                println("    const val $identifier: Int = $id")
            }
            println("}")
        }.flush()
    }

    private fun configNameToIdentifier(id: Int, name: String): String {
        val normalizedName = name.toUpperCase().replace(' ', '_').replace(Regex("[^a-zA-Z\\d:]"), "").removeTags()
        val propName = if (normalizedName.isNotEmpty()) normalizedName + "_$id" else "$id"
        return if (propName.first().isDigit()) "`$propName`" else propName
    }

    fun String.removeTags(): String {
        val builder = StringBuilder(length)
        var inTag = false
        forEach {
            if (it == '<') {
                inTag = true
            } else if (it == '>') {
                inTag = false
            } else if (!inTag) {
                builder.append(it)
            }
        }
        return "$builder"
    }

    companion object {
        private const val packageName: String = "io.guthix.oldscape.server.id"

        private val packageDir: String = packageName.replace(".", "/")
    }
}