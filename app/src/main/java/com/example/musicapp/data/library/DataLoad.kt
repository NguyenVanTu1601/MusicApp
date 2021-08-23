package com.example.musicapp.data.library

import android.os.AsyncTask

class DataLoad<V,T>(
    private val callback: OnDataCallBack<T>,
    private val handler: (V) -> T,
) : AsyncTask<V, Unit, T>() {

    private var exception: Exception? = null

    override fun onPostExecute(result: T) {
        super.onPostExecute(result)
        result?.let {
            callback.onSucceed(result)
        } ?: callback.onFailed(exception)
    }

    override fun doInBackground(vararg params: V): T? =
        try {
            handler(params.first())
        } catch (e: Exception) {
            exception = e
            null
        }
}
