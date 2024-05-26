package com.easycook.doughcalculator.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dough_recipes_table")
data class DoughRecipeEntity(
    @PrimaryKey(autoGenerate = true)
    var recipeId: Long? = null,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false,

    @ColumnInfo(name = "flour_gram")
    var flourGram: Int = 0,

    @ColumnInfo(name = "water_gram")
    var waterGram: Int = 0,

    @ColumnInfo(name = "salt_gram")
    var saltGram: Int = 0,

    @ColumnInfo(name = "sugar_gram")
    var sugarGram: Int = 0,

    @ColumnInfo(name = "butter_gram")
    var butterGram: Int = 0,

    @ColumnInfo(name = "egg_gram")
    var eggGram: Int = 0,

    @ColumnInfo(name = "yeast_gram")
    var yeastGram: Int = 0,

    @ColumnInfo(name = "milk_gram")
    var milkGram: Int = 0,

    @ColumnInfo(name = "flour_gram_correction")
    var flourGramCorrection: Int = 0,

    @ColumnInfo(name = "water_gram_correction")
    var waterGramCorrection: Int = 0,

    @ColumnInfo(name = "salt_gram_correction")
    var saltGramCorrection: Int = 0,

    @ColumnInfo(name = "sugar_gram_correction")
    var sugarGramCorrection: Int = 0,

    @ColumnInfo(name = "butter_gram_correction")
    var butterGramCorrection: Int = 0,

    @ColumnInfo(name = "egg_gram_correction")
    var eggGramCorrection: Int = 0,

    @ColumnInfo(name = "yeast_gram_correction")
    var yeastGramCorrection: Int = 0,

    @ColumnInfo(name = "milk_gram_correction")
    var milkGramCorrection: Int = 0,

    @ColumnInfo(name = "water_percent")
    var waterPercent: Double = 0.0,

    @ColumnInfo(name = "salt_percent")
    var saltPercent: Double = 0.0,

    @ColumnInfo(name = "sugar_percent")
    var sugarPercent: Double = 0.0,

    @ColumnInfo(name = "butter_percent")
    var butterPercent: Double = 0.0,

    @ColumnInfo(name = "egg_percent")
    var eggPercent: Double = 0.0,

    @ColumnInfo(name = "yeast_percent")
    var yeastPercent: Double = 0.0,

    @ColumnInfo(name = "milk_percent")
    var milkPercent: Double = 0.0
)
