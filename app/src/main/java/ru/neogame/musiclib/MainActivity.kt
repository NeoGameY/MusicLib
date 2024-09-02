package ru.neogame.musiclib

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import ru.neogame.musiclib.databinding.ActivityMainBinding
import ru.neogame.musiclib.models.SongModel
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import com.google.firebase.firestore.FieldPath
import ru.neogame.musiclib.adapter.SectionSongListAdapter


class MainActivity : AppCompatActivity() {

    private var allSongs: List<SongModel> = listOf()
    private lateinit var adapter: SectionSongListAdapter
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swipeRefreshLayout.setOnRefreshListener {
            setupSection("secion_1",binding.section1RecyclerView)
            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchView.clearFocus()

            binding.searchView.setQuery("", false)
            binding.searchView.setIconified(true)

        }

        setupSection("secion_1",binding.section1RecyclerView)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                binding.searchView.clearAnimation()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter as the user types
                filterSongs(newText)
                return true
            }
        })

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
            binding.searchView.requestFocusFromTouch()
        }

        binding.mainmenu.setOnClickListener {
            binding.searchView.clearFocus()
            binding.searchView.clearAnimation()
        }

        binding.menuImageView.setOnClickListener {
            // Отключаем обработчик
            binding.menuImageView.isEnabled = false

            // Показываем Toast
            Toast.makeText(this, "Здесь будет что-то новенькое", Toast.LENGTH_SHORT).show()

            // Включаем обработчик через 2 секунды (продолжительность Toast.LENGTH_SHORT)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.menuImageView.isEnabled = true
            }, 2000)
        }





    }

    override fun onResume() {
        super.onResume()
        showPlayerView()
        MyExoplayer.exoPlayer?.addListener(playerListener)
    }

    override fun onPause() {
        super.onPause()
        MyExoplayer.exoPlayer?.removeListener(playerListener)
    }

    private val playerListener = object : Player.Listener {
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            runOnUiThread {
                val currentIndex = MyExoplayer.getInstance()?.currentMediaItemIndex ?: 0
                MyExoplayer.updateCurrentSong(currentIndex)
                showPlayerView()
            }
        }
    }


    private fun showPlayerView(){
        binding.playerView.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        MyExoplayer.getCurrentSong()?.let {
            binding.playerView.visibility = View.VISIBLE
            binding.songTitleTextView.text = "Сейчас играет: " + it.title
            Glide.with(binding.songCoverImageView).load(it.coverUrl)
                .apply(RequestOptions().transform(RoundedCorners(32)))
                .into(binding.songCoverImageView)
        } ?: run {
            binding.playerView.visibility = View.GONE
        }
    }

    //Sections
    private fun setupSection(id: String, recyclerView: RecyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener { documentSnapshot ->
                val section = documentSnapshot.toObject(SongModel::class.java)
                section?.apply {
                    FirebaseFirestore.getInstance().collection("songs")
                        .whereIn(FieldPath.documentId(), section.songs)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            allSongs = querySnapshot.toObjects(SongModel::class.java)
                            adapter = SectionSongListAdapter(allSongs)
                            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                            recyclerView.adapter = adapter
                        }
                }
            }
    }

    private fun filterSongs(query: String?) {
        try {
            val filteredList = if (!query.isNullOrEmpty()) {
                allSongs.filter {
                    it.title.contains(query, true) || it.subtitle.contains(query, true)
                }
            } else {
                allSongs
            }
            adapter.updateList(filteredList)
        }
        catch (e:Exception){}
    }

}





