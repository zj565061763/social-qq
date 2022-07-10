package com.sd.lib.social.qq.core

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.sd.lib.social.qq.FSocialQQ
import com.sd.lib.social.qq.model.QQShareResult
import com.tencent.connect.common.Constants
import com.tencent.connect.share.QQShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 分享
 */
object FSocialQQShareApi {
    private val _isShare = AtomicBoolean(false)
    private var _shareCallback: ShareCallback? = null

    /**
     * 分享链接
     */
    @JvmStatic
    @JvmOverloads
    fun shareUrl(
        activity: Activity,
        /** 标题 */
        title: String,
        /** 分享的跳转链接 */
        targetUrl: String,
        /** 摘要 */
        summary: String = "",
        /** 图片url，可以是在线url或者本地路径 */
        imageUrl: String = "",
        /** 回调 */
        callback: ShareCallback,
    ) {
        if (_isShare.compareAndSet(false, true)) {
            _shareCallback = callback
            val params = Bundle().apply {
                putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
                putString(QQShare.SHARE_TO_QQ_TITLE, title)
                putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
                if (summary.isNotEmpty()) {
                    putString(QQShare.SHARE_TO_QQ_SUMMARY, summary)
                }
                if (imageUrl.isNotEmpty()) {
                    putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl)
                }
            }
            with(FSocialQQ) {
                tencent.shareToQQ(activity, params, _listener)
            }
        }
    }

    /**
     * Activity结果回调
     */
    @JvmStatic
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, _listener)
        }
    }

    private fun processResponse(response: Any?) {
        _shareCallback?.onSuccess(QQShareResult())
    }

    private val _listener = object : IUiListener {
        override fun onComplete(response: Any?) {
            processResponse(response)
            resetState()
        }

        override fun onError(error: UiError?) {
            _shareCallback?.onError(error?.errorCode ?: -1, error?.errorMessage ?: "")
            resetState()
        }

        override fun onCancel() {
            _shareCallback?.onCancel()
            resetState()
        }

        override fun onWarning(p0: Int) {
            _shareCallback?.onError(p0, "warning")
            resetState()
        }
    }

    private fun resetState() {
        _shareCallback = null
        _isShare.set(false)
    }

    interface ShareCallback {
        fun onSuccess(result: QQShareResult)

        fun onError(code: Int, message: String)

        fun onCancel()
    }
}