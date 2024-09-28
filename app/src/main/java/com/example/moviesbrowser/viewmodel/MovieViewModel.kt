package com.example.moviesbrowser.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesbrowser.db.MovieEntity
import com.example.moviesbrowser.model.Movie
import com.example.moviesbrowser.repository.MovieRepository
import com.example.moviesbrowser.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _moviesState = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val moviesState: StateFlow<Resource<List<Movie>>> = _moviesState

    fun getTrendingMovies(timeWindow: String) {
        viewModelScope.launch {
            repository.getTrendingMovies(timeWindow).collect {
                _moviesState.value = it
            }
        }
    }


    private val _favoritesState = MutableStateFlow<List<MovieEntity>>(emptyList())
    val favoritesState: StateFlow<List<MovieEntity>> = _favoritesState

    fun getFavoriteMovies() {
        viewModelScope.launch {
            repository.getFavorites().collect { favorites ->
                _favoritesState.value = favorites
            }
        }
    }

    fun addMovieToFavorites(movie: MovieEntity) {
        viewModelScope.launch {
            repository.addFavorite(movie)
        }
    }

    fun removeMovieFromFavorites(movie: MovieEntity) {
        viewModelScope.launch {
            repository.removeFavorite(movie)
        }
    }
}
