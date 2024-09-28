package com.example.moviesbrowser.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.moviesbrowser.model.Movie
import com.example.moviesbrowser.model.toMovieEntity
import com.example.moviesbrowser.utils.Resource
import com.example.moviesbrowser.viewmodel.MovieViewModel


@Composable
fun MovieListScreen(navController: NavController, backStackEntry: NavBackStackEntry) {
    val viewModel: MovieViewModel = hiltViewModel(backStackEntry)

    var selectedTimeWindow by remember { mutableStateOf("day") }
    val moviesState = viewModel.moviesState.collectAsState().value
    val favoritesState by viewModel.favoritesState.collectAsState()
    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isDataLoaded) {
            viewModel.getFavoriteMovies()
            isDataLoaded = true
        }
    }


    LaunchedEffect(selectedTimeWindow) {
        viewModel.getTrendingMovies(selectedTimeWindow)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Text(
            text = "TMDb Movies",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Most Popular",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Button(
                onClick = {
                    selectedTimeWindow = "day"
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (selectedTimeWindow == "day") Color.Blue else Color.Gray
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "Today", color = Color.White)
            }

            Button(
                onClick = {
                    selectedTimeWindow = "week"
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (selectedTimeWindow == "week") Color.Blue else Color.Gray
                )
            ) {
                Text(text = "This Week", color = Color.White)
            }
        }

        when (moviesState) {
            is Resource.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
            is Resource.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "An error occurred: ${moviesState.message}")
                }
            }
            is Resource.Success -> {
                val movies = moviesState.data ?: emptyList()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 56.dp)
                ) {
                    items(movies) { movie ->
                        val isFavorite = favoritesState.any { it.id == movie.id }
                        MovieListItem(
                            movie = movie,
                            navController = navController,
                            viewModel = viewModel,
                            isFavorite = isFavorite
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieListItem(movie: Movie, navController: NavController, viewModel: MovieViewModel, isFavorite: Boolean) {

    var currentFavoriteState by remember { mutableStateOf(isFavorite) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("movieDetail/${movie.id}")
            }
    ) {
        GlideImage(
            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
            contentDescription = movie.title,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movie.title,
                color = Color.Black,
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        IconButton(
            onClick = {
                currentFavoriteState = !currentFavoriteState
                if (currentFavoriteState) {
                    viewModel.addMovieToFavorites(movie.toMovieEntity())
                } else {
                    viewModel.removeMovieFromFavorites(movie.toMovieEntity())
                }
            }
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = if (currentFavoriteState) "Remove from favorites" else "Add to favorites",
                tint = if (currentFavoriteState) Color.Red else Color.Gray
            )
        }
    }
}


