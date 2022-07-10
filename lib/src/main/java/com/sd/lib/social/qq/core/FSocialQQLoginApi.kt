package com.sd.lib.social.qq.core

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import com.sd.lib.social.qq.FSocialQQ
import com.sd.lib.social.qq.model.QQLoginResult
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 登录
 */
object FSocialQQLoginApi {
    private val _isLogin = AtomicBoolean(false)
    private var _loginCallback: LoginCallback? = null

    /**
     * 登录
     */
    @JvmStatic
    fun login(activity: Activity, callback: LoginCallback) {
        with(FSocialQQ) {
            loginInternal(tencent, activity, callback)
        }
    }

    /**
     * Activity结果回调
     */
    @JvmStatic
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, _listener)
        }
    }

    /**
     * 退出登录
     */
    @JvmStatic
    fun logout() {
        with(FSocialQQ) {
            tencent.logout(context)
        }
    }

    private fun loginInternal(tencent: Tencent, activity: Activity, callback: LoginCallback) {
        if (_isLogin.compareAndSet(false, true)) {
            _loginCallback = callback

            val map = mutableMapOf<String, Any>()
            if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                map[Constants.KEY_RESTORE_LANDSCAPE] = true
            }
            map[Constants.KEY_SCOPE] = "all"
            tencent.login(activity, _listener, map)
        }
    }

    private fun resetState() {
        _loginCallback = null
        _isLogin.set(false)
    }

    private fun processResponse(response: Any?) {
        val callback = _loginCallback
        if (response !is JSONObject) {
            callback?.onError(-1, "empty result")
            return
        }

        if (response.length() <= 0) {
            callback?.onError(-1, "empty result")
            return
        }

        val openId = response.optString(Constants.PARAM_OPEN_ID)
        if (openId.isEmpty()) {
            callback?.onError(-1, "empty PARAM_OPEN_ID")
            return
        }

        val token = response.optString(Constants.PARAM_ACCESS_TOKEN)
        if (token.isEmpty()) {
            callback?.onError(-1, "empty PARAM_ACCESS_TOKEN")
            return
        }

        val expires = response.optString(Constants.PARAM_EXPIRES_IN)
        if (expires.isEmpty()) {
            callback?.onError(-1, "empty PARAM_EXPIRES_IN")
            return
        }

        FSocialQQ.tencent.apply {
            setAccessToken(token, expires)
            setOpenId(openId)
        }

        val loginResult = QQLoginResult(openId = openId, accessToken = token)
        callback?.onSuccess(loginResult)
    }

    private val _listener = object : IUiListener {
        override fun onComplete(response: Any?) {
            processResponse(response)
            resetState()
        }

        override fun onError(error: UiError?) {
            _loginCallback?.onError(error?.errorCode ?: -1, error?.errorMessage ?: "")
            resetState()
        }

        override fun onCancel() {
            _loginCallback?.onCancel()
            resetState()
        }

        override fun onWarning(p0: Int) {
            _loginCallback?.onError(p0, "warning")
            resetState()
        }
    }

    interface LoginCallback {
        fun onSuccess(result: QQLoginResult)

        fun onError(code: Int, message: String)

        fun onCancel()
    }
}