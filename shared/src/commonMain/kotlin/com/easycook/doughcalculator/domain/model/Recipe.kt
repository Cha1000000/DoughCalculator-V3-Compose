package com.easycook.doughcalculator.domain.model

import com.easycook.doughcalculator.domain.IngredientType

/**
 * Модель рецепта теста
 */
data class Recipe(
    val id: Long,
    val name: String,
    val description: String,
    val hydration: Double,
    val ingredients: Map<IngredientType, Double>,
    val notes: String = ""
) 