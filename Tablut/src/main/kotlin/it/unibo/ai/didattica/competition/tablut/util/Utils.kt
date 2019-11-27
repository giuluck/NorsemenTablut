package it.unibo.ai.didattica.competition.tablut.util

import java.io.File

/**
 * Print any object to the console.
 */
fun Any?.toConsole(): Unit = println(this)

/**
 * Write a string into the given file.
 */
fun String.toFile(file: String): Unit = File(file).run {
    parentFile.mkdirs()
    writeText(this@toFile)
}

/**
  * Returns the value if present, otherwise the given value.
  */
fun <T> T?.orElse(other: T): T = this ?: other

/**
 * Returns the value if present, otherwise throws an Illegal State Exception.
 */
fun <T> T?.orThrow(message: String = "Unexpected empty nullable value"): T = this ?: throw IllegalStateException(message)

/**
 * For each method using lambda with receiver.
 */
fun <T> Collection<T>.forEachSelf(routine: T.() -> Unit) = forEach(routine)