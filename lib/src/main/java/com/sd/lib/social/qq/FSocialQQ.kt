package com.sd.lib.social.qq

import android.app.Application
import android.content.Context
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
    fun init(context: Context) {
        synchronized(this@FSocialQQ) {
            if (_context != null) return
            _context = context.applicationContext as Application

            val appId = context.getString(R.string.lib_social_qq_app_id)
            check(appId.isNotEmpty()) { "R.string.lib_social_qq_app_id is empty" }
            _appId = appId

            val appIdScheme = context.getString(R.string.lib_social_qq_app_id_scheme)
            check(appIdScheme == "tencent${appId}") {
                "R.string.lib_social_qq_app_id_scheme should be tencent${appId}"
            }
        }
    }

    /**
     * QQ是否已安装
     */
    fun isQQInstalled(): Boolean {
        return _tencent.isQQInstalled(context)
    }
}