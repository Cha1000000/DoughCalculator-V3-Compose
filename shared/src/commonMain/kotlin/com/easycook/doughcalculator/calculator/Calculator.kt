package com.easycook.doughcalculator.calculator

class Calculator {
    companion object {
        private const val MIN_WATER_PERCENT = 50.0
        private const val MAX_WATER_PERCENT = 75.0
        private const val MIN_SALT_PERCENT = 1.0
        private const val MAX_SALT_PERCENT = 2.5
    }

    /**
     * Рассчитывает процент ингредиента относительно муки
     */
    fun calculateIngredientPercent(ingredientGram: Int, flourGram: Int): Double {
        return (ingredientGram.toDouble() / flourGram.toDouble()) * 100.0
    }

    /**
     * Рассчитывает вес ингредиента в граммах на основе процента от муки
     */
    fun calculateIngredientGram(ingredientPercent: Double, flourGram: Int): Int {
        return ((ingredientPercent / 100.0) * flourGram).toInt()
    }

    /**
     * Пересчитывает вес ингредиента в граммах с учетом корректировки муки
     */
    fun recalculateIngredientGram(ingredientPercent: Double, flourGramCorrection: Int): Int {
        return ((ingredientPercent / 100.0) * flourGramCorrection).toInt()
    }

    /**
     * Проверяет, находится ли процент воды в рекомендуемом диапазоне
     */
    fun isWaterPercentValid(waterPercent: Double): Boolean {
        return waterPercent in MIN_WATER_PERCENT..MAX_WATER_PERCENT
    }

    /**
     * Проверяет, находится ли процент соли в допустимом диапазоне
     */
    fun isSaltPercentValid(saltPercent: Double): Boolean {
        return saltPercent in MIN_SALT_PERCENT..MAX_SALT_PERCENT
    }
} 