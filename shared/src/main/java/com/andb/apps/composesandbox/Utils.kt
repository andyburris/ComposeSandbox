package com.andb.apps.composesandbox

fun <T> List<T>.plusElement(element: T, index: Int): List<T> {
    val result = ArrayList<T>(size + 1)
    result.addAll(this)
    result.add(index, element)
    return result
}
