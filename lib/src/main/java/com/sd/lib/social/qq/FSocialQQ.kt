package com.sd.lib.social.qq

import android.app.Application
import android.content.Context
import com.tencent.tauth.Tencent

object FSocialQQ {
    private lateinit var _context: Application
    private lateinit var _appId: String

    val _tencent by lazy {
        Tencent.setIsPermissionGranted(true)
        createTencent()
    }

    @JvmStatic
    val tencent: Tencent
        get() {
            if (!_tencent.isQQInstalled(_context)) {
                Tencent.resetTargetAppInfoCache()
            }
            return _tencent
        }

    internal val context: Context
        get() = _context

    @JvmStatic
    fun init(context: Context) {
        initInternal(context)
    }

    private fun createTencent(): Tencent {
        return Tencent.createInstance(_appId, _context, SocialQQFileProvider.getAuthority(_context))
    }

    private fun initInternal(context: Context) {
        _context = context.applicationContext as Application

        val appId = context.getString(R.string.lib_social_qq_app_id)
        check(appId.isNotEmpty()) { "R.string.lib_social_qq_app_id is empty" }
        _appId = appId

        val appIdScheme = context.getString(R.string.lib_social_qq_app_id_scheme)
        check(appIdScheme == "tencent${appId}") { "R.string.lib_social_qq_app_id_scheme should be tencent${appId}" }
    }
}