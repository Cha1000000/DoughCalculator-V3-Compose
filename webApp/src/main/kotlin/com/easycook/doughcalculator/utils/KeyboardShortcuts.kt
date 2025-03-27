package com.easycook.doughcalculator.utils

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

object KeyboardShortcuts {
    fun setupShortcuts(
        onSave: () -> Unit,
        onShare: () -> Unit,
        onImport: () -> Unit
    ) {
        val handleKeyPress = { e: KeyboardEvent ->
            // Ctrl/Cmd + S для сохранения
            if ((e.ctrlKey || e.metaKey) && e.key == "s") {
                e.preventDefault()
                onSave()
            }
            // Ctrl/Cmd + Shift + S для экспорта
            else if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key == "s") {
                e.preventDefault()
                onShare()
            }
            // Ctrl/Cmd + I для импорта
            else if ((e.ctrlKey || e.metaKey) && e.key == "i") {
                e.preventDefault()
                onImport()
            }
        }

        document.addEventListener("keydown", handleKeyPress)
    }
} 