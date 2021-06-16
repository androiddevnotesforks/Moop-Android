/*
 * Copyright 2021 SOUP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package soup.movie.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import soup.movie.data.db.internal.MovieCacheDatabase
import soup.movie.data.db.internal.MovieDatabase
import soup.movie.data.db.internal.RoomDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Singleton
    @Provides
    fun provideMoopDatabase(
        @ApplicationContext context: Context
    ): MoopDatabase {
        val movieDb = createMovieDatabase(context)
        val cacheDb = createCacheDatabase(context)
        return RoomDatabase(
            movieDb.favoriteMovieDao(),
            movieDb.openDateAlarmDao(),
            cacheDb.movieCacheDao()
        )
    }

    private fun createMovieDatabase(
        context: Context
    ): MovieDatabase {
        return Room
            .databaseBuilder(context.applicationContext, MovieDatabase::class.java, "movie.db")
            .build()
    }

    private fun createCacheDatabase(
        context: Context
    ): MovieCacheDatabase {
        return Room
            .databaseBuilder(context.applicationContext, MovieCacheDatabase::class.java, "moop.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}
