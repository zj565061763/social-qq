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

    val _tencent by lazy {
        Tencent.setIsPermissionGranted(true)
        Tencent.createInstance(appId, context, SocialQQFileProvider.getAuthority(context))
    }

    @JvmStatic
    val tencent: Tencent
        get() {
            if (!_tencent.isQQInstalled(context)) {
                Tencent.resetTargetAppInfoCache()
            }
            return _tencent
        }

    @JvmStatic
    fun init(context: Context, appId: String) {
        check(appId.isNotEmpty()) { "appId is empty" }
        _context = context.applicationContext as Application
        _appId = appId

        val appIdScheme = context.getString(R.string.lib_social_qq_app_id_scheme)
        check(appIdScheme == "tencent${appId}") { "R.string.lib_social_qq_app_id_scheme should be tencent${appId}" }
    }
}