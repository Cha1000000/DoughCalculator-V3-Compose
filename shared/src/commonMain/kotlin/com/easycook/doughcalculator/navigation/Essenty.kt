package com.easycook.doughcalculator.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

/**
 * Эта чистая переупаковка аннотаций из библиотеки essenty для улучшения импортов
 * Для поддержки сериализуемых состояний между различными платформами
 */

/**
 * Маркерный интерфейс, который позволяет реализовать механизм сериализации объектов
 * для сохранения состояния между уничтожениями навигационных компонентов
 */
typealias ParcelableState = Parcelable

/**
 * Аннотация, которая указывает компилятору, что данный класс должен быть
 * автоматически сделан сериализуемым с помощью механизма Parcelable
 */
typealias ParcelizeState = Parcelize 