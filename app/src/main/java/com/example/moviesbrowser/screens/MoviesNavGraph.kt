package com.example.moviesbrowser.screens

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState


object Routes {
    const val MOVIE_LIST = "movieList"
    const val MOVIE_DETAIL = "movieDetail"
    const val FAVORITES = "favorites"
}

object Arguments {
    const val MOVIE_ID = "movieId"
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.MovieList,
        BottomNavItem.Favorites,
    )

    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black,
        elevation = 5.dp
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Gray
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object MovieList : BottomNavItem("Movies", Icons.Default.Home, Routes.MOVIE_LIST)
    object Favorites : BottomNavItem("Favorites", Icons.Default.Favorite, Routes.FAVORITES)
}

@Composable
fun MoviesNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "movieList"
    ) {
        composable(Routes.MOVIE_LIST) {backStackEntry ->
            MovieListScreen(navController = navController, backStackEntry = backStackEntry)
        }

        composable("${Routes.MOVIE_DETAIL}/{${Arguments.MOVIE_ID}}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toInt()
            movieId?.let {
                MovieDetailScreen(navBackStackEntry = backStackEntry)
            }
        }

        composable(Routes.FAVORITES) {backStackEntry ->
            FavoritesScreen(navBackStackEntry = backStackEntry, navController = navController)
        }
    }
}

