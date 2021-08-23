package com.example.musicapp.view.library

import android.app.Activity
import com.example.musicapp.data.model.Music

interface MusicLibraryContract {
    interface Presenter {
        fun checkPermission(activity: Activity)
        fun onPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray,
            activity: Activity
        )
    }

    interface View  {
        fun displayListMusic(musics: List<Music>)
        fun loadMusicFailed()
    }
}
