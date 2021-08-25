package com.example.musicapp.view.library

import android.content.ContentResolver
import com.example.musicapp.data.library.OnDataCallBack
import com.example.musicapp.data.model.Music
import com.example.musicapp.data.repository.MusicRepository

class MusicLibraryPresenter(
    private var view: MusicLibraryContract.View,
    private var musicRepository: MusicRepository
) : MusicLibraryContract.Presenter {

    override fun loadMusic(contentResolver: ContentResolver) {
        musicRepository.getAllMusic(object : OnDataCallBack<List<Music>> {
            override fun onSucceed(data: List<Music>) {
                view.displayListMusic(data as ArrayList<Music>)
            }

            override fun onFailed(e: Exception?) {
                view.loadMusicFailed()
            }
        })
    }
}
