package com.monta

import java.nio.file.Paths

fun String.loadResource(): List<String> {
    return Paths.get(ClassLoader.getSystemResource(this).toURI()).toFile()!!.readLines()
}

fun List<String>.filterNotBlank() = this.filterNot { it.isBlank() }