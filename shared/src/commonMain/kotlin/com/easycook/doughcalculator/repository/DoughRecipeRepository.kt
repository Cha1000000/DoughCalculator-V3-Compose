package com.easycook.doughcalculator.repository

import com.easycook.doughcalculator.models.DoughRecipe
import kotlinx.coroutines.flow.Flow

interface DoughRecipeRepository {
    fun getAllRecipes(): Flow<List<DoughRecipe>>
    fun saveRecipe(recipe: DoughRecipe)
    fun updateRecipe(recipe: DoughRecipe)
    fun deleteRecipe(id: Int)
} 