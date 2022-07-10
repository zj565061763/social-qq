package com.sd.lib.social.qq

import android.app.Application
import android.content.Context
import com.tencent.tauth.Tencent

object FSocialQQ {
    private var _context: Application? = null
    private var _appId = ""
    private var _tencent: Tencent? = null

    internal val context: Context
        get() = checkNotNull(_context) { "You should init before this" }

    private val appId: String
        get() = _appId.also { check(it.isNotEmpty()) { "You should init before this" } }

    @JvmStatic
    val tencent: Tencent
        get() {
            val api = _tencent
                ?: Tencent.createInstance(
                    appId,
                    context,
                    SocialQQFileProvider.getAuthority(context)
                ).also {
                    _tencent = it
                }
            return api.also {
                if (!it.isQQInstalled(context)) {
                    Tencent.resetTargetAppInfoCache()
                }
                Tencent.setIsPermissionGranted(true)
            }
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