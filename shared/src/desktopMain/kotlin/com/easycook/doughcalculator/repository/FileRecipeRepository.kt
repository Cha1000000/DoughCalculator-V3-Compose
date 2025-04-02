package com.easycook.doughcalculator.repository

import com.easycook.doughcalculator.models.DoughRecipe
import com.easycook.doughcalculator.resources.StringsDesktop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Репозиторий, сохраняющий рецепты в JSON файл в директории приложения.
 */
class FileRecipeRepository(private val dataFile: File) : DoughRecipeRepository {
    private val recipes = MutableStateFlow<List<DoughRecipe>>(listOf())
    private var nextId = 1
    
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    init {
        // Создаем директорию, если она не существует
        dataFile.parentFile?.mkdirs()
        // Загружаем рецепты из файла при создании репозитория
        loadFromFile()
    }
    
    override fun getAllRecipes(): Flow<List<DoughRecipe>> = recipes.asStateFlow()
    
    override fun saveRecipe(recipe: DoughRecipe) {
        val newId = recipe.id ?: nextId++
        val newRecipe = recipe.copy(id = newId)
        recipes.value = recipes.value + newRecipe
        saveToFile()
    }
    
    override fun updateRecipe(recipe: DoughRecipe) {
        recipes.value = recipes.value.map { 
            if (it.id == recipe.id) recipe else it 
        }
        saveToFile()
    }
    
    override fun deleteRecipe(id: Int) {
        recipes.value = recipes.value.filter { it.id != id }
        saveToFile()
    }
    
    private fun saveToFile() {
        try {
            val jsonString = json.encodeToString(recipes.value)
            dataFile.writeText(jsonString)
            println(StringsDesktop.Repository.RECIPES_SAVED.format(dataFile.absolutePath))
        } catch (e: Exception) {
            println(StringsDesktop.Repository.SAVE_ERROR.format(e.message))
        }
    }
    
    private fun loadFromFile() {
        try {
            if (dataFile.exists() && dataFile.length() > 0) {
                val jsonString = dataFile.readText()
                val loadedRecipes = json.decodeFromString<List<DoughRecipe>>(jsonString)
                recipes.value = loadedRecipes
                nextId = (loadedRecipes.maxOfOrNull { it.id ?: 0 } ?: 0) + 1
                
                println(StringsDesktop.Repository.RECIPES_LOADED.format(loadedRecipes.size, dataFile.absolutePath))
            } else {
                println(StringsDesktop.Repository.FILE_NOT_EXISTS.format(dataFile.absolutePath))
            }
        } catch (e: Exception) {
            println(StringsDesktop.Repository.LOAD_ERROR.format(e.message))
        }
    }
} 