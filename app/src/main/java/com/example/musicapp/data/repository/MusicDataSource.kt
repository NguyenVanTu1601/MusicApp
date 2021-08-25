package com.example.musicapp.data.repository

import com.example.musicapp.data.library.OnDataCallBack
import com.example.musicapp.data.model.Music

interface MusicDataSource {
    fun getAllMusic(callback: OnDataCallBack<List<Music>>)
}
