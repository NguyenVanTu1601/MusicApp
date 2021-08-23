package com.example.musicapp.view.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.data.model.Music
import com.example.musicapp.databinding.ItemMusicBinding

class MusicAdapter(
    private val onItemClick: (Music) -> Unit
) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    private val musics = mutableListOf<Music>()
    override fun getItemCount() = musics.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =  ViewHolder(
            ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClick
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(musics[position])
    }

    fun updateListMusic(musics: List<Music>) {
        this.musics.apply {
            clear()
            addAll(musics)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemMusicBinding,
        onItemClick: (Music) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var itemMusic: Music? = null
        init {
            binding.root.setOnClickListener {
                itemMusic?.let { onItemClick(it) }
            }
        }

        fun bindData(item: Music) {
            itemMusic = item
            binding.apply {
                textItemSongName.text = item.name
                textItemSongAuthor.text = item.author
            }
        }
    }
}
