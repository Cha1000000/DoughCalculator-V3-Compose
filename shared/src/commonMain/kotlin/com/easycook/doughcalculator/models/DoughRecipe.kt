package com.easycook.doughcalculator.models

import kotlinx.serialization.Serializable

@Serializable
data class DoughRecipe(
    val id: Int? = null,
    var name: String = "",
    var description: String = "",
    var isFavorite: Boolean = false,
    
    // Основные ингредиенты в граммах
    var flourGram: Double = 0.0,
    var waterGram: Double = 0.0,
    var saltGram: Double = 0.0,
    var sugarGram: Double = 0.0,
    var butterGram: Double = 0.0,
    var yeastGram: Double = 0.0,
    var milkGram: Double = 0.0,
    var eggGram: Double = 0.0,
    
    // Проценты ингредиентов
    var waterPercent: Double = 0.0,
    var saltPercent: Double = 0.0,
    var sugarPercent: Double = 0.0,
    var butterPercent: Double = 0.0,
    var yeastPercent: Double = 0.0,
    var milkPercent: Double = 0.0,
    var eggPercent: Double = 0.0,
    
    // Корректировки в граммах
    var flourGramCorrection: Double = 0.0,
    var waterGramCorrection: Double = 0.0,
    var saltGramCorrection: Double = 0.0,
    var sugarGramCorrection: Double = 0.0,
    var butterGramCorrection: Double = 0.0,
    var yeastGramCorrection: Double = 0.0,
    var milkGramCorrection: Double = 0.0,
    var eggGramCorrection: Double = 0.0
) 