package com.example.musicapp.view.library

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.musicapp.R
import com.example.musicapp.data.model.Music
import com.example.musicapp.databinding.ActivityMusicLibraryBinding
import com.example.musicapp.utils.Constants
import com.example.musicapp.view.main.MainActivity

class MusicLibraryActivity : AppCompatActivity(), MusicLibraryContract.View {
    private lateinit var binding: ActivityMusicLibraryBinding
    private val adapter = MusicAdapter(this::onMusicClick)
    lateinit var presenter: MusicLibraryContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_library)
        initViewBinding()
        initData()
        initAdapter()
        presenter.checkPermission(this)
    }

    private fun initAdapter() {
        binding.recyclerLibrary.adapter = adapter
    }

    fun initData(){
        this.presenter = MusicLibraryPresenter(this)
    }

    private fun initViewBinding() {
        binding = ActivityMusicLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.onPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun onMusicClick(music: Music) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.KEY_PASS_DATA, music)
        startActivity(intent)
    }

    override fun displayListMusic(musics: List<Music>) {
        adapter.updateListMusic(musics)
    }

    override fun loadMusicFailed() {
        Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_SHORT).show()
    }
}
