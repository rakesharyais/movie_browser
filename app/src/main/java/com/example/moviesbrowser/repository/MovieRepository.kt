package com.example.moviesbrowser.repository

import com.example.moviesbrowser.BuildConfig
import com.example.moviesbrowser.api.ApiService
import com.example.moviesbrowser.db.MovieDao
import com.example.moviesbrowser.db.MovieEntity
import com.example.moviesbrowser.model.Movie
import com.example.moviesbrowser.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: ApiService,
    private val movieDao: MovieDao
) {

    fun getTrendingMovies(timeWindow: String): Flow<Resource<List<Movie>>> = flow {
        try {
            emit(Resource.Loading())
            delay(100)

            val response = apiService.getTrendingMovies(timeWindow, apiKey = BuildConfig.TMDB_API_KEY)

            if (response.isSuccessful && response.body() != null) {
                val movies = response.body()?.results ?: emptyList() // Get the movie list from the body
                emit(Resource.Success(movies))
            } else {
                emit(Resource.Error("Error: ${response.message()}")) // Error message from the response
            }

        } catch (e: Exception) {
            emit(Resource.Error("Failed to load trending movies: ${e.localizedMessage}"))
        }
    }


    fun getFavorites(): Flow<List<MovieEntity>> {
        return movieDao.getAllFavorites()
    }

    suspend fun addFavorite(movie: MovieEntity) {
        movieDao.insertFavorite(movie)
    }

    suspend fun removeFavorite(movie: MovieEntity) {
        movieDao.deleteFavorite(movie)
    }

}
