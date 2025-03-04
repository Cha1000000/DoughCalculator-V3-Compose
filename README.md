# DoughCalculator V3 Compose

Android mobile App for calculation dough ingredients ratios. Writes on JetPack Compose.

**DoughCalculator** — это Android-приложение на базе Jetpack Compose, позволяющее:

1. Создавать и сохранять рецепты теста.

2. Переключаться между расчётом ингредиентов в граммах и процентах.

3. Гибко корректировать значения муки и других ингредиентов.

4. Хранить рецепты в локальной базе данных и быстро к ним возвращаться.



**Основные возможности**

1. **Расчёт ингредиентов в граммах или процентах**

• _Calculate By Weight_: вводите граммы муки, воды, соли и т.д. — проценты рассчитываются автоматически.

• _Calculate By Percent_: вводите проценты для ингредиентов — приложение пересчитывает их в граммы, исходя из массы муки.

2. **Сохранение и управление рецептами**

• Новый рецепт: быстро обнулить все поля и ввести данные заново.

• Открыть сохранённые рецепты: переходите на экран со списком, выбирайте нужный рецепт.

• Сохранение и обновление: приложение использует базу данных (Room), чтобы сохранять рецепты.

3. **Коррекция муки и связанных ингредиентов**

• Введите коррекцию для муки (например, если решили добавить ещё 50 г), и приложение пересчитает массу воды, соли, дрожжей и других ингредиентов пропорционально.

4. **Валидация**

• Предупреждение при слишком низком/высоком содержании воды.

• Ошибки, если поля муки, воды или соли не заполнены.

5. **Удобный UI на Jetpack Compose**

• Современный дизайн с использованием Material 3, LazyVerticalGrid, OutlinedTextField и т.д.

• Поддержка тёмной темы и адаптивный интерфейс для разных экранов.



**Технологии и архитектура**

• **Язык**: [Kotlin](https://kotlinlang.org/)

• **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)

• **Хранение данных**: [Room Database](https://developer.android.com/training/data-storage/room)

• **Модель**: [MVVM](https://developer.android.com/jetpack/guide) с ViewModel и MutableState/Flow

• **DI**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

• **Навигация**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

**Структура проекта (кратко)**

└── app
├── main
│   ├── java/com/easycook/doughcalculator
│   │   ├── database    # Room (DAO, Database, Entities)
│   │   ├── models      # IngredientUiItemModel, IngredientType и т.д.
│   │   ├── screens     # Основные Composable-экраны (CalculationScreen, SaveRecipeScreen, RecipeListScreen и т.п.)
│   │   ├── ui/theme    # Темы и стили Compose
│   │   ├── common      # Утилиты, константы, вспомогательные функции
│   │   └── RecipeViewModel.kt
│   └── res
│       ├── layout      # XML-макеты (если используются)
│       ├── values      # Файлы строк, тем и т.д.
│       └── drawable    # Иконки, изображения

**Как запустить проект**

1. **Склонируйте репозиторий**: git clone https://github.com/yourusername/DoughCalculator_Compose.git
2. **Откройте проект** в [Android Studio](https://developer.android.com/studio).

3. **Соберите и запустите**:

• Убедитесь, что в настройках Gradle всё соответствует используемой версии Android Studio/Gradle/Compose.

• Запустите эмулятор или подключите физическое устройство.

• Нажмите кнопку _Run_ или запустите сборку Gradle командой ./gradlew assembleDebug.

4. **Готово!** Приложение соберётся и запустится на устройстве или эмуляторе.



**Использование**

1. **Главный экран (CalculationScreen)**

• Выберите режим расчёта: по весу (Weight) или по процентам (Percent).

• Введите необходимые ингредиенты (мука, вода, соль и т.д.).

• Нажмите _Calculate_ для пересчёта.

2. **Меню (иконка «троеточие»)**

• _New_: очистить все поля для нового рецепта.

• _Open_: перейти на список сохранённых рецептов.

• _Save_: перейти на экран сохранения текущего рецепта.

3. **Экран сохранения рецепта**

• Введите название (title) и описание (description).

• Нажмите «Сохранить», чтобы зафиксировать рецепт в базе данных.

4. **Экран списка рецептов**

• Выберите любой сохранённый рецепт, чтобы открыть его в калькуляторе и внести корректировки.

• В списке сохранённых рецептов можно отмечать "избранные" рецепты, нажав на иконку в виде сердчека слева от названия рецепта (либо снять отметку).

• А так же можно удалять рецепты из списка, нажатием на иконку "корзины" справа от названия рецепта.
