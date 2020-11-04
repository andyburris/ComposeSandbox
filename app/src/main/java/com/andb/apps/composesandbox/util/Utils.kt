package com.andb.apps.composesandbox.util

fun <T> List<T>.plusElement(element: T, index: Int): List<T> {
    val result = ArrayList<T>(size + 1)
    result.addAll(this)
    result.add(index, element)
    return result
}

fun String.prependIndentLevel(level: Int): String {
    val indents = "    ".repeat(level)
    return this.prependIndent(indents)
}