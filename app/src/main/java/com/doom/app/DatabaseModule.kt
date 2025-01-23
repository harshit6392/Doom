package com.doom.app

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(context, NoteDatabase::class.java, "notes_database")
            .build()
    }

    @Provides
    fun provideNoteDao(noteDatabase: NoteDatabase): NoteDao {
        return noteDatabase.noteDao()
    }
}
