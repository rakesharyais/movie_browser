package com.example.moviesbrowser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.moviesbrowser.db.MovieEntity
import com.example.moviesbrowser.viewmodel.FavoriteViewModel

@Composable
fun FavoritesScreen(navBackStackEntry : NavBackStackEntry, navController: NavController) {

    val viewModel: FavoriteViewModel = hiltViewModel(navBackStackEntry)
    val favoritesState by viewModel.favoritesState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFavoriteMovies()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoritesState.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite movies",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(favoritesState) { favorite ->
                    FavoriteItem(favorite = favorite, navController = navController, viewModel = viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FavoriteItem(favorite: MovieEntity, navController: NavController, viewModel: FavoriteViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("movieDetail/${favorite.id}")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = "https://image.tmdb.org/t/p/w500${favorite.posterPath}",
            contentDescription = favorite.title,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = favorite.title,
                color = Color.Black
            )
        }

        IconButton(onClick = { viewModel.removeMovieFromFavorites(favorite) }) {
            Icon(Icons.Default.Delete,
                contentDescription = "Remove from favorites",
                tint = Color.Black
            )
        }
    }
}


