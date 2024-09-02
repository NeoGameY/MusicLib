package ru.neogame.musiclib.models

data class SongModel(
    val id : String,
    val title : String,
    val subtitle : String,
    val url : String,
    val coverUrl : String,
    val songs : List<String>

) {
    constructor() : this("","","","","", listOf())
}
