package com.example.musicapp.view.main

import com.example.musicapp.data.model.Music

interface MainContract {
    interface Presenter {
        fun loadDataSong()
        fun loadDataNextMusic(id: String, musics: MutableList<Music>)
        fun loadDataPrevMusic(id: String, musics: MutableList<Music>)
    }

    interface View {
        fun loadListMusic(listMusics: ArrayList<Music>)
        fun loadMusicFailed()
        fun loadNextMusic(music: Music)
        fun loadPrevMusic(music: Music)
    }
}
