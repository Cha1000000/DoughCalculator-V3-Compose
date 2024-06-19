package com.easycook.doughcalculator.models

import androidx.compose.runtime.MutableState

data class IngredientUiItemModel(
    val name: String,
    var quantity: MutableState<String>,
    var percent: MutableState<String>,
    var correction: MutableState<String>,
)
