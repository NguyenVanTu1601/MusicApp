package com.example.musicapp.view.main

import com.example.musicapp.data.library.OnDataCallBack
import com.example.musicapp.data.model.Music
import com.example.musicapp.data.repository.MusicRepository
import com.example.musicapp.utils.Constants

class MainPresenter(
    private var view: MainContract.View,
    private val musicRepository: MusicRepository
): MainContract.Presenter{

    override fun loadDataSong() {
        musicRepository.getAllMusic(object : OnDataCallBack<List<Music>> {
            override fun onSucceed(data: List<Music>) {
                view.loadListMusic(data as ArrayList<Music>)
            }

            override fun onFailed(e: Exception?) {
                view.loadMusicFailed()
            }

        })
    }

    override fun loadDataNextMusic(id: String, musics: MutableList<Music>) {
        var pos = POSITION_START
        for (index in musics.indices) {
            if (musics[index].id == id && index < musics.size - Constants.ONE) pos = index + Constants.ONE
        }
        view.loadNextMusic(musics[pos])
    }

    override fun loadDataPrevMusic(id: String, musics: MutableList<Music>) {
        var pos = POSITION_START
        for (index in musics.indices) {
            if (musics[index].id == id && index > POSITION_START) pos = index - Constants.ONE
        }
        view.loadPrevMusic(musics[pos])
    }

    companion object {
        const val POSITION_START = Constants.SONG_START_POSITION
    }
}
