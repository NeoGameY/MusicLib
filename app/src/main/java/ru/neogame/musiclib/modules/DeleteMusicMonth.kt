package ru.neogame.musiclib.modules

import android.content.Context
import android.os.Environment

class DeleteMusicMonth(private val context: Context) {

    fun deleteUnplayedMusic() {
        val musicDirectory = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val files = musicDirectory?.listFiles()

        files?.forEach { file ->
            val lastModified = file.lastModified()
            val currentTime = System.currentTimeMillis()
            val thirtyDaysInMillis = 30 * 24 * 60 * 60 * 1000 // 30 days

            if (currentTime - lastModified > thirtyDaysInMillis) {
                file.delete()
            }
        }
    }
}