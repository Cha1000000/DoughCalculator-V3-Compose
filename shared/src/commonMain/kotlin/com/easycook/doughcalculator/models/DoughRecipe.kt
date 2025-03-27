package com.easycook.doughcalculator.models

import kotlinx.serialization.Serializable

@Serializable
data class DoughRecipe(
    val id: Int? = null,
    var name: String = "",
    var description: String = "",
    var isFavorite: Boolean = false,
    
    // Основные ингредиенты в граммах
    var flourGram: Int = 0,
    var waterGram: Int = 0,
    var saltGram: Int = 0,
    var sugarGram: Int = 0,
    var butterGram: Int = 0,
    var yeastGram: Int = 0,
    var milkGram: Int = 0,
    var eggGram: Int = 0,
    
    // Проценты ингредиентов
    var waterPercent: Double = 0.0,
    var saltPercent: Double = 0.0,
    var sugarPercent: Double = 0.0,
    var butterPercent: Double = 0.0,
    var yeastPercent: Double = 0.0,
    var milkPercent: Double = 0.0,
    var eggPercent: Double = 0.0,
    
    // Корректировки в граммах
    var flourGramCorrection: Int = 0,
    var waterGramCorrection: Int = 0,
    var saltGramCorrection: Int = 0,
    var sugarGramCorrection: Int = 0,
    var butterGramCorrection: Int = 0,
    var yeastGramCorrection: Int = 0,
    var milkGramCorrection: Int = 0,
    var eggGramCorrection: Int = 0
) 