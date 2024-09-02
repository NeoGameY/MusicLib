package ru.neogame.musiclib.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import ru.neogame.musiclib.MyExoplayer
import ru.neogame.musiclib.PlayerActivity
import ru.neogame.musiclib.databinding.SectionSongListRecyclerRowBinding
import ru.neogame.musiclib.models.SongModel

class SectionSongListAdapter(private var songList: List<SongModel>) :
    RecyclerView.Adapter<SectionSongListAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: SectionSongListRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(song: SongModel, songList: List<SongModel>) {
            binding.songTitleTextView.text = song.title
            binding.songSubtitleTextView.text = song.subtitle
            Glide.with(binding.songCoverImageView.context)
                .load(song.coverUrl)
                .apply(RequestOptions().transform(RoundedCorners(32)))
                .into(binding.songCoverImageView)
            binding.root.setOnClickListener {
                MyExoplayer.startPlaying(binding.root.context, song, songList)
                binding.root.context.startActivity(Intent(it.context, PlayerActivity::class.java))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SectionSongListRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(songList[position], songList)
    }

    fun updateList(newSongList: List<SongModel>) {
        songList = newSongList
        notifyDataSetChanged()
    }
}

