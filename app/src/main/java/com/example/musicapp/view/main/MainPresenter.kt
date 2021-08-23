package com.example.musicapp.view.main

import android.content.ContentResolver
import com.example.musicapp.data.library.MusicDataLocal
import com.example.musicapp.data.model.Music
import com.example.musicapp.utils.Constants
import kotlinx.coroutines.*

class MainPresenter(
    private var view: MainContract.View
): MainContract.Presenter{

    override fun loadDataSong(contentResolver: ContentResolver) {
        GlobalScope.launch(Dispatchers.IO) {
            val musics = MusicDataLocal.getInstance(contentResolver).getAllAudioFromDevice()
            view.loadListMusic(musics as ArrayList<Music>)
        }
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
