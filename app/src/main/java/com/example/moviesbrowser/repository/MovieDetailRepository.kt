package com.example.moviesbrowser.repository

import com.example.moviesbrowser.BuildConfig
import com.example.moviesbrowser.api.ApiService
import com.example.moviesbrowser.db.MovieDao
import com.example.moviesbrowser.db.MovieEntity
import com.example.moviesbrowser.model.MovieDetailResponse
import com.example.moviesbrowser.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailRepository @Inject constructor(
    private val apiService: ApiService,
    private val movieDao: MovieDao
) {

    fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetailResponse>> = flow {
        try {
            emit(Resource.Loading())
            delay(100)
            val response = apiService.getMovieDetails(movieId, apiKey = BuildConfig.TMDB_API_KEY)

            if (response.isSuccessful && response.body() != null) {
                val movieDetail = response.body()  // Get the movie details from the response body
                if (movieDetail != null) {
                    emit(Resource.Success(movieDetail))
                } else {
                    emit(Resource.Error("Error: Movie details are null"))
                }
            } else {
                emit(Resource.Error("Error: ${response.message()}"))  // Error message from the response
            }

        } catch (e: Exception) {
            emit(Resource.Error("Failed to load movie details: ${e.localizedMessage}"))
        }
    }

    suspend fun addFavorite(movie: MovieEntity) {
        movieDao.insertFavorite(movie)
    }

    suspend fun removeFavorite(movie: MovieEntity) {
        movieDao.deleteFavorite(movie)
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return movieDao.isFavorite(movieId) != null
    }
}
