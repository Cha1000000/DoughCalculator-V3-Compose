package com.easycook.doughcalculator.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.easycook.doughcalculator.MainActivity
import com.easycook.doughcalculator.R
import com.easycook.doughcalculator.data.SettingsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecipeWidget : AppWidgetProvider(), KoinComponent {
    private val settingsRepository: SettingsRepository by inject()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_recipe)
        val lastRecipe = settingsRepository.getLastRecipe()

        if (lastRecipe != null) {
            views.setTextViewText(R.id.widget_recipe_name, lastRecipe.name)
            views.setTextViewText(
                R.id.widget_recipe_hydration,
                "Гидратация: ${String.format("%.1f", lastRecipe.hydration)}%"
            )
            views.setTextViewText(
                R.id.widget_recipe_weight,
                "Общий вес: ${String.format("%.1f", lastRecipe.totalWeight)} г"
            )
        } else {
            views.setTextViewText(R.id.widget_recipe_name, "Нет сохраненного рецепта")
            views.setTextViewText(R.id.widget_recipe_hydration, "")
            views.setTextViewText(R.id.widget_recipe_weight, "")
        }

        // Добавляем обработчик нажатия для открытия приложения
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_recipe_name, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
} 