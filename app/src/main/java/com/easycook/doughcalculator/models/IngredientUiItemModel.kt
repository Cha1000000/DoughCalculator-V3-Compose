package com.easycook.doughcalculator.models

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue
import com.easycook.doughcalculator.R

data class IngredientUiItemModel(
    val ingredient: IngredientType,
    var quantity: MutableState<TextFieldValue>,
    var percent: MutableState<TextFieldValue>,
    var correction: MutableState<TextFieldValue>,
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
