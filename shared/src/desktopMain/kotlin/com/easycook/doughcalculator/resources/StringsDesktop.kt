package com.easycook.doughcalculator.resources

/**
 * Строковые ресурсы, специфичные для десктоп платформы
 */
object StringsDesktop {
    object StorageInfo {
        const val DIALOG_TITLE = "Информация о хранении"
        const val RECIPES_SAVED_TO_FILE = "Ваши рецепты сохраняются между запусками приложения в файл:"
        const val BACKUP_INFO = "Вы можете скопировать этот файл для создания резервной копии рецептов."
        const val COPY_PATH = "Копировать путь"
        const val PATH_COPIED = "Путь скопирован в буфер обмена"
    }
    
    object IconsDescriptions {
        const val STORAGE_INFO = "Информация о хранении"
        const val FAVORITE = "Избранное"
        const val DELETE = "Удалить"
    }
    
    object Console {
        const val SEPARATOR = "=================================================="
        const val RECIPES_FILE_PATH = "Рецепты сохраняются в файл: %s"
    }
    
    object Repository {
        const val RECIPES_SAVED = "Рецепты успешно сохранены в: %s"
        const val SAVE_ERROR = "Ошибка при сохранении рецептов: %s"
        const val RECIPES_LOADED = "Загружено %d рецептов из: %s"
        const val FILE_NOT_EXISTS = "Файл рецептов не существует или пуст: %s"
        const val LOAD_ERROR = "Ошибка при загрузке рецептов: %s"
    }
} 