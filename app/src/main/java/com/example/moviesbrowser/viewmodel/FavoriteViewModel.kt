package com.example.moviesbrowser.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesbrowser.db.MovieEntity
import com.example.moviesbrowser.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<List<MovieEntity>>(emptyList())
    val favoritesState: StateFlow<List<MovieEntity>> = _favoritesState

    fun getFavoriteMovies() {
        viewModelScope.launch {
            favoriteRepository.getFavorites().collect { favorites ->
                _favoritesState.value = favorites
            }
        }
    }

    fun removeMovieFromFavorites(movie: MovieEntity) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(movie)
        }
    }
}
