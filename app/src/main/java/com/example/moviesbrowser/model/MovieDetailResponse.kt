package com.example.moviesbrowser.model

import com.example.moviesbrowser.db.MovieEntity
import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val runtime: Int?,
    val genres: List<Genre>,
    val budget: Long,
    val revenue: Long,
    @SerializedName("vote_average") val rating: Float
)


fun MovieDetailResponse.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        overview = this.overview,
        releaseDate = this.releaseDate,
        rating = this.rating
    )
}

