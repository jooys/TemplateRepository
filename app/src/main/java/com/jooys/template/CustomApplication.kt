package com.jooys.template

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CustomApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }
}
