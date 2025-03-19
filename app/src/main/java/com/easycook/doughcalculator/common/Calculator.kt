package com.easycook.doughcalculator.common

import kotlin.math.roundToInt

/**
 * Класс для расчетов ингредиентов теста
 */
class Calculator {

    /**
     * Рассчитывает процент ингредиента относительно муки
     * 
     * @param gramIngredient вес ингредиента в граммах
     * @param gramFlour вес муки в граммах
     * @return процент ингредиента
     */
    fun calculateIngredientPercent(gramIngredient: Int, gramFlour: Int): Double =
        gramIngredient * 100.00 / gramFlour

    /**
     * Рассчитывает вес ингредиента в граммах на основе процента
     * 
     * @param percentIngredient процент ингредиента
     * @param gramFlour вес муки в граммах
     * @return вес ингредиента в граммах
     */
    fun calculateIngredientGram(percentIngredient: Double, gramFlour: Int): Int =
        (gramFlour * percentIngredient / 100.0).roundToInt()

    /**
     * Пересчитывает граммы ингредиента с учетом корректировки муки
     * 
     * @param percentIngredient процент ингредиента
     * @param correctionFlour корректировка муки в граммах
     * @return рассчитанный вес ингредиента в граммах
     */
    fun recalculateIngredientGram(percentIngredient: Double, correctionFlour: Int): Int =
        (correctionFlour * percentIngredient / 100.00).toInt()

    /**
     * Проверяет, находится ли процент воды в допустимом диапазоне
     * 
     * @param waterPercent процент воды
     * @return true если процент воды в допустимом диапазоне
     */
    fun isWaterPercentValid(waterPercent: Double): Boolean =
        waterPercent in MIN_WATER_PERCENT..MAX_WATER_PERCENT

    /**
     * Проверяет, не превышает ли процент соли максимально допустимое значение
     * 
     * @param saltPercent процент соли
     * @return true если процент соли не превышает максимум
     */
    fun isSaltPercentValid(saltPercent: Double): Boolean = saltPercent <= MAX_SALT_PERCENT
} 