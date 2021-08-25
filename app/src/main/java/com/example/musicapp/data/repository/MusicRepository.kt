package com.example.musicapp.data.repository

import com.example.musicapp.data.library.OnDataCallBack
import com.example.musicapp.data.model.Music

class MusicRepository private constructor(
    private val local: MusicDataSource
) : MusicDataSource {

    override fun getAllMusic(callback: OnDataCallBack<List<Music>>) {
        local.getAllMusic(callback)
    }

    companion object {
        private var instance: MusicRepository? = null
        fun getInstance(local: MusicDataSource) = kotlin.synchronized(this){
            instance ?: MusicRepository(local).also { instance = it }
        }
    }
}
