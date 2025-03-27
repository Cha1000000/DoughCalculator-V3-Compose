package com.easycook.doughcalculator.repository

import com.easycook.doughcalculator.models.DoughRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryRecipeRepository : DoughRecipeRepository {
    private val recipes = MutableStateFlow<List<DoughRecipe>>(emptyList())
    private var nextId = 1

    override fun getAllRecipes(): Flow<List<DoughRecipe>> = recipes.asStateFlow()

    override fun saveRecipe(recipe: DoughRecipe) {
        val newRecipe = recipe.copy(id = nextId++)
        recipes.value = recipes.value + newRecipe
    }

    override fun updateRecipe(recipe: DoughRecipe) {
        recipes.value = recipes.value.map { 
            if (it.id == recipe.id) recipe else it 
        }
    }

    override fun deleteRecipe(id: Int) {
        recipes.value = recipes.value.filter { it.id != id }
    }
} 