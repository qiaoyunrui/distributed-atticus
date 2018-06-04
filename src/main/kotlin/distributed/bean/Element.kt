package distributed.bean

import distributed.*

data class Element(var operation: String, var data: String = "")

fun createControlUnit() = gson.toJson(Element(KEY_CONTROL))

fun createResultUnit(data: String) = gson.toJson(Element(KEY_RESULT, data))

fun createStartUnit(data: String) = gson.toJson(Element(KEY_START, data))

fun createCalculateUnit(data: String) = gson.toJson(Element(KEY_CALCULATE, data))