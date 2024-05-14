package com.easycook.doughcalculator.di

import android.app.Application
import androidx.room.Room
import com.easycook.doughcalculator.database.DoughRecipesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideMainDb(app: Application): DoughRecipesDatabase =
        Room.databaseBuilder(
            app,
            DoughRecipesDatabase::class.java,
            "dough_recipes.db"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
}