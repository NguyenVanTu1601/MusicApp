package com.example.musicapp.view.library

import android.app.Activity
import android.content.ContentResolver
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.musicapp.data.library.MusicDataLocal
import com.example.musicapp.data.model.Music
import com.example.musicapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MusicLibraryPresenter(
    private var view: MusicLibraryContract.View
) : MusicLibraryContract.Presenter {

    override fun checkPermission(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.REQUEST_CODE
            )
        } else {
            loadMusic(activity.contentResolver)
        }
    }

    override fun onPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        activity: Activity
    ) {
        if (requestCode ==  Constants.REQUEST_CODE
            && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            loadMusic(activity.contentResolver)
        }
    }

    private fun loadMusic(contentResolver: ContentResolver) {
        GlobalScope.launch(Dispatchers.IO) {
            val musics = MusicDataLocal.getInstance(contentResolver).getAllAudioFromDevice()
            view.displayListMusic(musics as ArrayList<Music>)
        }
    }
}
