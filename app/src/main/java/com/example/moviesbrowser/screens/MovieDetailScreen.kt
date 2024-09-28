package com.example.moviesbrowser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.moviesbrowser.model.MovieDetailResponse
import com.example.moviesbrowser.model.toMovieEntity
import com.example.moviesbrowser.utils.Resource
import com.example.moviesbrowser.viewmodel.MovieDetailViewModel

@Composable
fun MovieDetailScreen(navBackStackEntry: NavBackStackEntry) {

    val viewModel: MovieDetailViewModel = hiltViewModel(navBackStackEntry)
    val movieId = navBackStackEntry.arguments?.getString("movieId")?.toIntOrNull()
    var isFavorite by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }
    val movieDetailsState = viewModel.movieDetailsState.collectAsState().value

    LaunchedEffect(movieId) {
        if (movieId != null) {
            viewModel.getMovieDetails(movieId)
            if (!isDataLoaded) {
                isFavorite = viewModel.isMovieFavorite(movieId)
                isDataLoaded = true
            }
        }
    }

    when (movieDetailsState) {
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
                Text(text = "Error: ${movieDetailsState.message}")
            }
        }
        is Resource.Success -> {
            val movieDetail = movieDetailsState.data
            movieDetail?.let {
                MovieDetailContent(
                    movieDetail = it,
                    isFavorite = isFavorite,
                    onFavoriteToggle = {
                        isFavorite = !isFavorite
                        if (isFavorite) {
                            viewModel.addMovieToFavorites(it.toMovieEntity())
                        } else {
                            viewModel.removeMovieFromFavorites(it.toMovieEntity())
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieDetailContent(
    movieDetail: MovieDetailResponse,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            GlideImage(
                model = "https://image.tmdb.org/t/p/w500${movieDetail.posterPath}",
                contentDescription = movieDetail.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = movieDetail.title,
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = movieDetail.overview,
            color = Color.Black,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Release Date: ${movieDetail.releaseDate ?: "N/A"}",
            color = Color.DarkGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))


        val genres = movieDetail.genres?.joinToString(", ") { it.name } ?: "N/A"
        Text(
            text = "Genres: $genres",
            color = Color.DarkGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        val runtime = movieDetail.runtime?.let { "$it minutes" } ?: "N/A"
        Text(
            text = "Duration: $runtime",
            color = Color.DarkGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}





