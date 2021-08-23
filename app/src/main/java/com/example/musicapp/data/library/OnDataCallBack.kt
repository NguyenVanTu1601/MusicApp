package com.example.musicapp.data.library

interface OnDataCallBack<T> {
    fun onSucceed(data: T)
    fun onFailed(e: Exception?)
}
