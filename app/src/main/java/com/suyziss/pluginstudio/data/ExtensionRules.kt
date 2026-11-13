package com.suyziss.pluginstudio.data

object ExtensionRules {
    fun normalize(value: String): String = value.trim().let { if (it.startsWith(".")) it else ".$it" }
    fun isValid(value: String): Boolean {
        val ext = normalize(value)
        return ext.length in 2..32 && ext.drop(1).matches(Regex("[A-Za-z0-9][A-Za-z0-9._+-]*")) && !ext.contains("..") && !ext.endsWith(".")
    }
    fun fileName(name: String, extension: String): String = name.trim().substringBeforeLast('.', name.trim()) + normalize(extension)
}
