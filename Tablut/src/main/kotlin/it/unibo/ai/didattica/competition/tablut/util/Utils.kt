package it.unibo.ai.didattica.competition.tablut.util

/**
 * Print any object calling it in the console.
 */
fun Any?.toConsole(): Unit = println(this)

/**
  * Returns the value if present, otherwise the given value.
  */
fun <T> T?.orElse(other: T): T = this ?: other

/**
 * Returns the value if present, otherwise throws an Illegal State Exception.
 */
fun <T> T?.orThrow(message: String = "Unexpected empty nullable value"): T = this ?: throw IllegalStateException(message)

/**
 * Runs a routine. If an exception is thrown, it prints the stack trace.
 */
fun tryOrPrint(routine: () -> Unit) =
    runCatching(routine).onFailure {
        it.printStackTrace()
    }.getOrDefault(Unit)

fun <T> Collection<T>.forEachSelf(routine: T.() -> Unit) = forEach(routine)