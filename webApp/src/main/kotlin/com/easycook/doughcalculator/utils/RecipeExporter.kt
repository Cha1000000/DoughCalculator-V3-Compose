package com.easycook.doughcalculator.utils

import com.easycook.doughcalculator.models.Recipe
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RecipeExporter {
    fun exportAsJson(recipe: Recipe) {
        val json = Json.encodeToString(recipe)
        downloadFile("${recipe.name}.json", json, "application/json")
    }

    fun exportAsText(recipe: Recipe) {
        val text = """
            Рецепт: ${recipe.name}
            Мука: ${recipe.flour} г
            Вода: ${recipe.water} г
            Соль: ${recipe.salt} г
            Дрожжи: ${recipe.yeast} г
            Общий вес: ${recipe.totalWeight} г
            Гидратация: ${recipe.hydration}%
        """.trimIndent()
        downloadFile("${recipe.name}.txt", text, "text/plain")
    }

    fun exportAsMarkdown(recipe: Recipe) {
        val markdown = """
            # ${recipe.name}

            ## Ингредиенты
            - Мука: ${recipe.flour} г
            - Вода: ${recipe.water} г
            - Соль: ${recipe.salt} г
            - Дрожжи: ${recipe.yeast} г

            ## Характеристики
            - Общий вес: ${recipe.totalWeight} г
            - Гидратация: ${recipe.hydration}%
        """.trimIndent()
        downloadFile("${recipe.name}.md", markdown, "text/markdown")
    }

    private fun downloadFile(filename: String, content: String, mimeType: String) {
        val blob = window.Blob(arrayOf(content), object {
            val type = mimeType
        })
        val url = window.URL.createObjectURL(blob)
        val link = document.createElement("a")
        link.href = url
        link.download = filename
        document.body?.appendChild(link)
        link.click()
        document.body?.removeChild(link)
        window.URL.revokeObjectURL(url)
    }
} 