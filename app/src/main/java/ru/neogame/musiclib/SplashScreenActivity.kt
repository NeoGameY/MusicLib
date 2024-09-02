package ru.neogame.musiclib

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.neogame.musiclib.databinding.ActivitySplashScreenBinding
import ru.neogame.musiclib.modules.DeleteMusicMonth

class SplashScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //DeleteMusicMonth(this).deleteUnplayedMusic()

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val splashScreenDuration = 3000L // 2 sec

        binding.root.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, splashScreenDuration)
    }
}