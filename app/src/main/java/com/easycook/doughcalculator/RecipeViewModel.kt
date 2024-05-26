package com.easycook.doughcalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easycook.doughcalculator.database.DoughRecipeEntity
import com.easycook.doughcalculator.database.DoughRecipesDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(private val database: DoughRecipesDatabase) : ViewModel() {

    val recipes = database.dao.getAllRecipes()
    //var recipeEntity: DoughRecipeEntity? = null
    var recipeEntity by mutableStateOf<DoughRecipeEntity?>(null)

    fun insertRecipe(recipe: DoughRecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        database.dao.insert(recipe)
    }

    fun updateRecipe(recipe: DoughRecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        database.dao.update(recipe)
    }

    fun deleteRecipe(recipe: DoughRecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        database.dao.delete(recipe)
    }
}