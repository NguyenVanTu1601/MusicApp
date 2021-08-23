package com.example.musicapp.view.library

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.musicapp.R
import com.example.musicapp.data.library.MusicDataLocal
import com.example.musicapp.data.model.Music
import com.example.musicapp.data.repository.MusicRepository
import com.example.musicapp.databinding.ActivityMusicLibraryBinding
import com.example.musicapp.utils.Constants
import com.example.musicapp.view.main.MainActivity

class MusicLibraryActivity : AppCompatActivity(), MusicLibraryContract.View {
    private val binding: ActivityMusicLibraryBinding by lazy {
        ActivityMusicLibraryBinding.inflate(layoutInflater)
    }
    private val adapter = MusicAdapter(this::onMusicClick)
    private val presenter: MusicLibraryContract.Presenter by lazy {
        val local = MusicDataLocal.getInstance(contentResolver)
        val repository =   MusicRepository.getInstance(local)
        MusicLibraryPresenter(this, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkPermission(this)
        initAdapter()
    }

    override fun displayListMusic(musics: List<Music>) {
        adapter.updateListMusic(musics)
    }

    override fun loadMusicFailed() {
        Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(activity: Activity) {
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
            presenter.loadMusic(contentResolver)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onPermissionsResult(requestCode, grantResults)
    }

    private fun onPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (requestCode ==  Constants.REQUEST_CODE
            && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            presenter.loadMusic(contentResolver)
        }
    }

    private fun initAdapter() {
        binding.recyclerLibrary.adapter = adapter
    }

    private fun onMusicClick(music: Music) {
        val intent = MainActivity.getIntent(this, music)
        startActivity(intent)
    }
}
