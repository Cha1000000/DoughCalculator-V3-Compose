package com.easycook.doughcalculator.database

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с рецептами теста
 */
@Singleton
class DoughRecipeRepository @Inject constructor(
    private val database: DoughRecipesDatabase
) {
    /**
     * Получение всех рецептов
     */
    fun getAllRecipes(): Flow<List<DoughRecipeEntity>> {
        return database.dao.getAllRecipes()
    }

    /**
     * Добавление нового рецепта
     */
    fun insertRecipe(recipe: DoughRecipeEntity) {
        database.dao.insert(recipe)
    }

    /**
     * Обновление существующего рецепта
     */
    fun updateRecipe(recipe: DoughRecipeEntity) {
        database.dao.update(recipe)
    }

    /**
     * Удаление рецепта
     */
    fun deleteRecipe(recipe: DoughRecipeEntity) {
        database.dao.delete(recipe)
    }

    /**
     * Получение рецепта по ID
     */
    fun getRecipeById(id: Long): DoughRecipeEntity {
        return database.dao.getById(id)
    }

    /**
     * Получение рецепта по названию
     */
    fun getRecipeByTitle(title: String): DoughRecipeEntity {
        return database.dao.getByTitle(title)
    }

    /**
     * Удаление рецепта по ID
     */
    fun deleteRecipeById(id: Long) {
        database.dao.deleteById(id)
    }

    /**
     * Очистка всех рецептов
     */
    fun clearAllRecipes() {
        database.dao.clear()
    }
} 