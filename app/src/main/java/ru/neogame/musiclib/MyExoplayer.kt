package ru.neogame.musiclib

import android.content.Context
import android.os.Environment
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import ru.neogame.musiclib.models.SongModel
import java.io.File

@OptIn(UnstableApi::class)
object MyExoplayer {

    var exoPlayer: ExoPlayer? = null
    private var currentSong: SongModel? = null
    private var playlist: List<SongModel> = listOf()

    fun getCurrentSong(): SongModel? {
        return currentSong
    }

    fun getInstance(): ExoPlayer? {
        return exoPlayer
    }

    fun initializePlayer(context: Context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }
    }

    fun startPlaying(context: Context, song: SongModel, playlist: List<SongModel>) {
        initializePlayer(context)
        this.playlist = playlist

        val mediaItems = playlist.map {songItem ->
            val localFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "${songItem.title}.mp3")
            if (localFile.exists()) {
                MediaItem.fromUri(localFile.toUri())
            } else {
                MediaItem.fromUri(songItem.url)
            }
        }

        val startIndex = playlist.indexOf(song)

        exoPlayer?.apply {
            setMediaItems(mediaItems, startIndex, 0L)
            prepare()
            play()
        }
        currentSong = song
    }

    fun updateCurrentSong(index: Int) {
        if (index >= 0 && index < playlist.size) {
            currentSong = playlist[index]
        }
    }
}
