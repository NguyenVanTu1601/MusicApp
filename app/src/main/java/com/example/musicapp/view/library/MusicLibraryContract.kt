package com.example.musicapp.view.library

import android.content.ContentResolver
import com.example.musicapp.data.model.Music

interface MusicLibraryContract {

    interface Presenter {
        fun loadMusic(contentResolver: ContentResolver)
    }
    interface View  {
        fun displayListMusic(musics: List<Music>)
        fun loadMusicFailed()
    }
}
