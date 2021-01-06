package com.andb.apps.composesandboxdata

fun <T> List<T>.plusElement(element: T, index: Int): List<T> {
    val result = ArrayList<T>(size + 1)
    result.addAll(this)
    result.add(index, element)
    return result
}

fun String.toCamelCase() = filter { it != ' ' }.mapIndexed { index, c -> if (index == 0) c.toLowerCase() else c }.joinToString("")
fun String.toPascalCase() = filter { it != ' ' }
