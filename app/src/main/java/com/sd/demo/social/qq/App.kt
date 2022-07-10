package com.sd.demo.social.qq

import android.app.Application
import com.sd.lib.social.qq.FSocialQQ

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FSocialQQ.init(this)
    }
}