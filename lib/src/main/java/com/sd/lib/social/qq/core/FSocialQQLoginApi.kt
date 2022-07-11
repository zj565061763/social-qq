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

/**
 * 登录
 */
object FSocialQQLoginApi {
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
        _loginCallback = callback

        val map = mutableMapOf<String, Any>()
        if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            map[Constants.KEY_RESTORE_LANDSCAPE] = true
        }
        map[Constants.KEY_SCOPE] = "all"
        tencent.login(activity, _listener, map)
    }

    private fun processResponse(response: Any?) {
        if (response !is JSONObject) {
            notifyError(-1, "empty result")
            return
        }

        if (response.length() <= 0) {
            notifyError(-1, "empty result")
            return
        }

        val openId = response.optString(Constants.PARAM_OPEN_ID)
        if (openId.isEmpty()) {
            notifyError(-1, "empty PARAM_OPEN_ID")
            return
        }

        val token = response.optString(Constants.PARAM_ACCESS_TOKEN)
        if (token.isEmpty()) {
            notifyError(-1, "empty PARAM_ACCESS_TOKEN")
            return
        }

        val expires = response.optString(Constants.PARAM_EXPIRES_IN)
        if (expires.isEmpty()) {
            notifyError(-1, "empty PARAM_EXPIRES_IN")
            return
        }

        FSocialQQ.tencent.apply {
            setAccessToken(token, expires)
            setOpenId(openId)
        }

        val loginResult = QQLoginResult(openId = openId, accessToken = token)
        notifySuccess(loginResult)
    }

    private val _listener = object : IUiListener {
        override fun onComplete(response: Any?) {
            processResponse(response)
        }

        override fun onError(error: UiError?) {
            notifyError(error?.errorCode ?: -1, error?.errorMessage ?: "")
        }

        override fun onCancel() {
            notifyCancel()
        }

        override fun onWarning(code: Int) {
            notifyError(code, "warning")
        }
    }

    private fun notifySuccess(result: QQLoginResult) {
        _loginCallback?.onSuccess(result)
        resetState()
    }

    private fun notifyError(code: Int, message: String) {
        _loginCallback?.onError(code, message)
        resetState()
    }

    private fun notifyCancel() {
        _loginCallback?.onCancel()
        resetState()
    }

    private fun resetState() {
        _loginCallback = null
    }

    interface LoginCallback {
        fun onSuccess(result: QQLoginResult)

        fun onError(code: Int, message: String)

        fun onCancel()
    }
}