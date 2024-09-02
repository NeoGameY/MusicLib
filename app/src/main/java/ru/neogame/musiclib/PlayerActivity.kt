package ru.neogame.musiclib

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import ru.neogame.musiclib.databinding.ActivityPlayerBinding
import ru.neogame.musiclib.models.SongModel
import ru.neogame.musiclib.modules.DownloadedManager

class PlayerActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlayerBinding
    lateinit var exoPlayer: ExoPlayer

    var playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            showGif(isPlaying)
        }


        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            runOnUiThread {
                val currentIndex = MyExoplayer.getInstance()?.currentMediaItemIndex ?: 0
                MyExoplayer.updateCurrentSong(currentIndex)
                updateUI(MyExoplayer.getCurrentSong())
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        exoPlayer = MyExoplayer.getInstance()!!
        binding.playerView.player = exoPlayer
        binding.playerView.showController()
        exoPlayer.addListener(playerListener)

        updateUI(MyExoplayer.getCurrentSong())

        binding.iconArrowLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.saveButton.setOnClickListener {
            val downloadManager = DownloadedManager(this, binding)
            downloadManager.downloadMusic()
        }
        binding.saveButton.setOnLongClickListener {//ПОПРОБОВАТЬ  ОТКРЫТЬ ФАЙЛЫ С ПАПКАМИ АУДИОЗАПИСЯМИ
            //Toast.makeText(this, "Не зажимай так долго", Toast.LENGTH_SHORT).show()
            val musicDirectory = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            val musicDirectoryUri = Uri.parse(musicDirectory?.absolutePath)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(musicDirectoryUri, "*/*")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent.createChooser(intent, "Открыть папку с аудиозаписями"))
            true

        }

        /*binding.saveButton.setOnLongClickListener {
            Toast.makeText(this, "Не зажимай так долго", Toast.LENGTH_SHORT).show()
            val musicDirectory = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            val musicDirectoryUri = Uri.parse(musicDirectory?.absolutePath)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(musicDirectoryUri, "vnd.android.document/directory")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Нет приложения для открытия папки", Toast.LENGTH_SHORT).show()
            }
            true
        }*/
    }


    private fun updateUI(song: SongModel?) {
        song?.apply {
            binding.songTitleTextView.text = title
            binding.songSubtitleTextView.text = subtitle
            Glide.with(binding.songCoverImageView).load(coverUrl)
                .circleCrop()
                .into(binding.songCoverImageView)
            Glide.with(binding.songGifImageView).load(R.drawable.media_playing)
                .circleCrop()
                .into(binding.songGifImageView)

            showGif(exoPlayer.isPlaying)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.removeListener(playerListener)
    }

    private fun showGif(show: Boolean) {
        if (show)
            binding.songGifImageView.visibility = View.VISIBLE
        else
            binding.songGifImageView.visibility = View.INVISIBLE
    }
}
