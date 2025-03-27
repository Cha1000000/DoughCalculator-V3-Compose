package com.easycook.doughcalculator.utils

import com.easycook.doughcalculator.models.Recipe
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AutoSave {
    private const val AUTO_SAVE_INTERVAL = 30000L // 30 секунд
    private var autoSaveJob: dynamic? = null
    private var lastRecipe: Recipe? = null

    fun startAutoSave(
        scope: CoroutineScope,
        onSave: (Recipe) -> Unit
    ) {
        stopAutoSave()
        
        autoSaveJob = window.setInterval({
            scope.launch(Dispatchers.Default) {
                lastRecipe?.let { recipe ->
                    onSave(recipe)
                }
            }
        }, AUTO_SAVE_INTERVAL)
    }

    fun stopAutoSave() {
        autoSaveJob?.let {
            window.clearInterval(it)
            autoSaveJob = null
        }
    }

    fun updateLastRecipe(recipe: Recipe) {
        lastRecipe = recipe
    }
} 