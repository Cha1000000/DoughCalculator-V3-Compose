package com.easycook.doughcalculator.models

data class IngredientUiModel(
    val ingredient: IngredientType,
    val quantity: String = "",
    val percent: String = "",
    val correction: String = ""
) {
    companion object {
        fun createDefault(ingredient: IngredientType): IngredientUiModel {
            return when (ingredient) {
                IngredientType.Flour -> IngredientUiModel(
                    ingredient = ingredient,
                    percent = "100"
                )
                else -> IngredientUiModel(ingredient = ingredient)
            }
        }
    }
} 