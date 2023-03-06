package com.sd.lib.social.qq

import android.app.Application
import android.content.Context
import android.content.Intent
import com.sd.lib.social.qq.core.FSocialQQLoginApi
import com.sd.lib.social.qq.core.FSocialQQShareApi
import com.tencent.tauth.Tencent

object FSocialQQ {
    private var _context: Application? = null
    private var _appId = ""

    internal val context: Context
        get() = checkNotNull(_context) { "You should init before this" }

    private val appId: String
        get() = _appId.also { check(it.isNotEmpty()) { "You should init before this" } }

    private val _tencent by lazy {
        Tencent.createInstance(
            appId,
            context,
            SocialQQFileProvider.getAuthority(context)
        )
    }

    /**
     * SDK对象
     */
    @JvmStatic
    val tencent: Tencent
        get() = _tencent.also {
            if (!it.isQQInstalled(context)) {
                Tencent.resetTargetAppInfoCache()
            }
            Tencent.setIsPermissionGranted(true)
        }

    /**
     * 初始化
     */
    @JvmStatic
    fun init(context: Context, appId: String) {
        synchronized(this@FSocialQQ) {
            if (_context != null) return
            _context = context.applicationContext as Application

            require(appId.isNotEmpty()) { "appId is empty" }
            _appId = appId
        }
    }

    /**
     * Activity结果回调
     */
    @JvmStatic
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        FSocialQQLoginApi.onActivityResult(requestCode, resultCode, data)
        FSocialQQShareApi.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * QQ是否已安装
     */
    fun isQQInstalled(): Boolean {
        return _tencent.isQQInstalled(context)
    }
}