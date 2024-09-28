package com.example.moviesbrowser.repository

import com.example.moviesbrowser.db.MovieDao
import com.example.moviesbrowser.db.MovieEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val movieDao: MovieDao
) {

    fun getFavorites(): Flow<List<MovieEntity>> {
        return movieDao.getAllFavorites()
    }

    suspend fun removeFavorite(movie: MovieEntity) {
        movieDao.deleteFavorite(movie)
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return movieDao.isFavorite(movieId) != null
    }
}
