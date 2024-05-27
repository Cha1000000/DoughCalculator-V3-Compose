package com.easycook.doughcalculator.models

data class IngredientUiItemModel(
    val name: String,
    var quantity: String,
    var percent: String,
    var correction: String,
    var isQuantityEditable: Boolean,
    var isPercentsEditable: Boolean,
    var isCorrectionEditable: Boolean = false
)
