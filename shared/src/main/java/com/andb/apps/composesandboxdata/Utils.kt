package com.andb.apps.composesandboxdata

fun <T> List<T>.plusElement(element: T, index: Int): List<T> {
    val result = ArrayList<T>(size + 1)
    result.addAll(this)
    result.add(index, element)
    return result
}

fun <T, X, Y> List<T>.unzip(transform: (T) -> Pair<X, Y>): Pair<List<X>, List<Y>> {
    val xList = ArrayList<X>(size)
    val yList = ArrayList<Y>(size)
    this.forEach {
        val pair = transform(it)
        xList.add(pair.first)
        yList.add(pair.second)
    }
    return Pair(xList, yList)
}

fun String.toCamelCase() = filter { it != ' ' }.mapIndexed { index, c -> if (index == 0) c.toLowerCase() else c }.joinToString("")
fun String.toPascalCase() = filter { it != ' ' }
