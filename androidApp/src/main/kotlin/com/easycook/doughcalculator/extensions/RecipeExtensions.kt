package com.easycook.doughcalculator.extensions

import android.content.Context
import android.content.Intent
import com.easycook.doughcalculator.models.Recipe

fun Recipe.share(context: Context) {
    val shareText = buildString {
        appendLine("Рецепт: $name")
        appendLine()
        appendLine("Ингредиенты:")
        appendLine("Мука: ${String.format("%.1f", flour)} г")
        appendLine("Вода: ${String.format("%.1f", water)} г")
        appendLine("Соль: ${String.format("%.1f", salt)} г")
        appendLine("Дрожжи: ${String.format("%.1f", yeast)} г")
        appendLine()
        appendLine("Общий вес: ${String.format("%.1f", totalWeight)} г")
        appendLine("Гидратация: ${String.format("%.1f", hydration)}%")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Поделиться рецептом")
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(shareIntent)
} 