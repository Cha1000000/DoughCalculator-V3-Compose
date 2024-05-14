package com.easycook.doughcalculator.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DoughRecipeEntity::class], version = 1, exportSchema = true)
abstract class DoughRecipesDatabase  : RoomDatabase() {

    abstract val dao: DoughRecipeDao
}