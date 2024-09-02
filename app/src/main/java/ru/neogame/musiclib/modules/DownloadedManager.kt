// DownloadManager.kt
package ru.neogame.musiclib.modules

import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import ru.neogame.musiclib.MyExoplayer
import ru.neogame.musiclib.PlayerActivity
import ru.neogame.musiclib.databinding.ActivityPlayerBinding
import java.io.File

class DownloadedManager(private val context: Context, private val binding: ActivityPlayerBinding) {

    fun downloadMusic() {
        val currentSong = MyExoplayer.getCurrentSong()
        currentSong?.let {
            if (!isSongDownloaded(it.title)) {
                if (isInternetAvailable()) {

                    binding.saveButton.isEnabled = false
                    performDownload(it.url, it.title)
                    Toast.makeText(context, "Загрузка началась", Toast.LENGTH_SHORT).show()

                    // Включаем кнопку снова после завершения загрузки
                    binding.saveButton.postDelayed({
                        binding.saveButton.isEnabled = true
                    }, 10000) // Задержка в 10 секунды
                } else {
                    binding.saveButton.isEnabled = false
                    Toast.makeText(context, "Нет доступа к интернету", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.saveButton.isEnabled = true
                    }, 2000)
                }
            } else {
                binding.saveButton.isEnabled = false
                Toast.makeText(context, "Аудио журнал уже скачан", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.saveButton.isEnabled = true
                }, 2000)
            }
        }
    }


    private fun isSongDownloaded(songTitle: String): Boolean {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "$songTitle.mp3")
        return file.exists()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun performDownload(songUrl: String, songTitle: String) {
        val request = DownloadManager.Request(Uri.parse(songUrl))
            .setTitle(songTitle)
            .setDescription("Осталось скачать:")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MUSIC, "$songTitle.mp3")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }


}
