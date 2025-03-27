package com.easycook.doughcalculator.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.Serializable

/**
 * Конфигурации для навигации между главными экранами приложения
 */
@Serializable
sealed interface Config : Parcelable {
    @Parcelize
    @Serializable
    object Calculator : Config
    
    @Parcelize
    @Serializable
    object RecipeList : Config
    
    @Parcelize
    @Serializable
    data class RecipeDetails(val recipeId: Long) : Config
} 