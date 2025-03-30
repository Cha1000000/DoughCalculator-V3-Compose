package com.easycook.doughcalculator.resources

/**
 * Общие текстовые ресурсы для всех платформ
 */
object Strings {
    object Navigation {
        const val MY_RECIPES = "Мои рецепты"
        const val NEW_RECIPE = "Новый рецепт"
    }

    object Calculator {
        const val INGREDIENT = "Ингредиент"
        const val GRAMS = "Граммы"
        const val PERCENTS = "Проценты"
        const val CORRECTION = "Коррекция"
        const val MODE = "Режим расчета:"
        const val MODE_MOBILE = "Считать по"
        const val BY_WEIGHT = "По весу"
        const val BY_PERCENTS = "По процентам"
    }

    object Ingredients {
        const val FLOUR = "Мука"
        const val WATER = "Вода"
        const val SALT = "Соль"
        const val SUGAR = "Сахар"
        const val BUTTER = "Масло"
        const val YEAST = "Дрожжи"
        const val MILK = "Молоко"
        const val EGGS = "Яйца"
    }

    object Validation {
        const val WATER_WARNING = "⚠️ Рекомендуемая норма воды: 60–80%"
        const val SALT_ERROR = "❗️ Допустимая норма соли - не более 2,5%"
    }

    object Recipe {
        const val SAVE_RECIPE = "Сохранить рецепт"
        const val UPDATE_RECIPE = "Обновить рецепт"
        const val SAVE = "Сохранить"
        const val UPDATE = "Обновить"
        const val NAME = "Название рецепта"
        const val DESCRIPTION = "Описание (необязательно)"
    }

    object Buttons {
        const val YES = "Да"
        const val NO = "Нет"
        const val CANCEL = "Отмена"
        const val DELETE = "Удалить"
    }

    object AlertDialogs {
        const val WARNING = "Предупреждение"
        const val CONFIRM = "Подтверждение"
        const val DELETE_RECIPE_CONFIRMATION = "Вы уверены, что хотите удалить рецепт '%s'?"
    }
} 