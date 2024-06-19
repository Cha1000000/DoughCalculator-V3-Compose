package com.easycook.doughcalculator.common

import java.util.Locale

fun Int.toStringOrBlank(): String = if (this == 0) "" else this.toString()

fun Double.formatToStringOrBlank(): String =
    if (this == 0.0) "" else String.format(Locale.getDefault(), "%.0f", this)