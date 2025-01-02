package com.easycook.doughcalculator.models

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import com.easycook.doughcalculator.R

data class IngredientUiItemModel(
    val ingredient: IngredientType,
    var quantity: MutableState<String>,
    var percent: MutableState<String>,
    var correction: MutableState<String>,
)

enum class IngredientType(@StringRes val title: Int) {
    Flour(R.string.flour),
    Water(R.string.water),
    Salt(R.string.salt),
    Sugar(R.string.sugar),
    Butter(R.string.butter),
    Yeast(R.string.yeast),
    Milk(R.string.milk),
    Egg(R.string.egg),
}
