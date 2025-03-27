package com.easycook.doughcalculator.utils

import com.easycook.doughcalculator.models.Recipe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter

object RecipeExporter {
    fun exportToCsv(recipe: Recipe, file: File) {
        FileWriter(file).use { writer ->
            writer.appendLine("Название,Мука,Вода,Соль,Дрожжи,Общий вес,Гидратация")
            writer.appendLine(
                "${recipe.name}," +
                "${recipe.flour}," +
                "${recipe.water}," +
                "${recipe.salt}," +
                "${recipe.yeast}," +
                "${recipe.totalWeight}," +
                "${recipe.hydration}"
            )
        }
    }

    fun exportToJson(recipe: Recipe, file: File) {
        val json = Json { 
            prettyPrint = true 
            encodeDefaults = true
        }.encodeToString(recipe)
        file.writeText(json)
    }
} 