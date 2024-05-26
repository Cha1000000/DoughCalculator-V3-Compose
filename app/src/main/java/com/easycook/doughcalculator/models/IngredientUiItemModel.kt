package com.easycook.doughcalculator.models

data class IngredientUiItemModel(
    val name: String,
    val quantity: String,
    val percent: String,
    val correction: String,
    val isQuantityEditable: Boolean,
    val isPercentsEditable: Boolean,
    val isCorrectionEditable: Boolean = false
)
