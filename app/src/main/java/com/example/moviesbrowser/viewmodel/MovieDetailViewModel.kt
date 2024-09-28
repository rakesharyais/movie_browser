package com.example.moviesbrowser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesbrowser.db.MovieEntity
import com.example.moviesbrowser.model.MovieDetailResponse
import com.example.moviesbrowser.repository.MovieDetailRepository
import com.example.moviesbrowser.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieDetailRepository: MovieDetailRepository,
) : ViewModel() {

    private val _movieDetailsState = MutableStateFlow<Resource<MovieDetailResponse>>(Resource.Loading())
    val movieDetailsState: StateFlow<Resource<MovieDetailResponse>> = _movieDetailsState

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            movieDetailRepository.getMovieDetails(movieId).collect {
                _movieDetailsState.value = it
            }
        }
    }

    fun addMovieToFavorites(movie: MovieEntity) {
        viewModelScope.launch {
            movieDetailRepository.addFavorite(movie)
        }
    }

    fun removeMovieFromFavorites(movie: MovieEntity) {
        viewModelScope.launch {
            movieDetailRepository.removeFavorite(movie)
        }
    }

    suspend fun isMovieFavorite(movieId: Int): Boolean {
        return movieDetailRepository.isFavorite(movieId)
    }
}
