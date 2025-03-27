package com.easycook.doughcalculator.utils

import com.easycook.doughcalculator.models.Recipe
import kotlinx.browser.localStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object WebStorage {
    private const val RECIPES_KEY = "recipes"
    private const val LAST_RECIPE_KEY = "last_recipe"

    fun saveRecipes(recipes: List<Recipe>) {
        localStorage.setItem(RECIPES_KEY, Json.encodeToString(recipes))
    }

    fun loadRecipes(): List<Recipe> {
        val json = localStorage.getItem(RECIPES_KEY)
        return if (json != null) {
            try {
                Json.decodeFromString(json)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun saveLastRecipe(recipe: Recipe) {
        localStorage.setItem(LAST_RECIPE_KEY, Json.encodeToString(recipe))
    }

    fun loadLastRecipe(): Recipe? {
        val json = localStorage.getItem(LAST_RECIPE_KEY)
        return if (json != null) {
            try {
                Json.decodeFromString<Recipe>(json)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    fun clearStorage() {
        localStorage.removeItem(RECIPES_KEY)
        localStorage.removeItem(LAST_RECIPE_KEY)
    }
} 