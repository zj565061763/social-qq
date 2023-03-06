package com.sd.demo.social.qq

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.social.qq.databinding.ActivityMainBinding
import com.sd.lib.social.qq.FSocialQQ
import com.sd.lib.social.qq.core.FSocialQQLoginApi
import com.sd.lib.social.qq.core.FSocialQQShareApi
import com.sd.lib.social.qq.model.QQLoginResult
import com.sd.lib.social.qq.model.QQShareResult

class MainActivity : AppCompatActivity() {
    private val _binding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        // 登录
        _binding.btnLogin.setOnClickListener {
            login()
        }

        // 分享
        _binding.btnShare.setOnClickListener {
            share()
        }
    }

    private fun login() {
        logMsg { "login" }
        FSocialQQLoginApi.login(this@MainActivity, object : FSocialQQLoginApi.LoginCallback {
            override fun onSuccess(result: QQLoginResult) {
                logMsg { "login onSuccess $result" }
            }

            override fun onError(code: Int, message: String) {
                logMsg { "login onError $code $message" }
            }

            override fun onCancel() {
                logMsg { "login onCancel" }
            }
        })
    }

    private fun share() {
        logMsg { "share" }
        FSocialQQShareApi.shareUrl(
            this@MainActivity,
            targetUrl = "http://www.baidu.com",
            title = "我是标题",
            description = "我是描述",
            imageUrl = "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png",
            callback = object : FSocialQQShareApi.ShareCallback {
                override fun onSuccess(result: QQShareResult) {
                    logMsg { "share onSuccess $result" }
                }

                override fun onError(code: Int, message: String) {
                    logMsg { "share onError $code $message" }
                }

                override fun onCancel() {
                    logMsg { "share onCancel" }
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FSocialQQ.onActivityResult(requestCode, resultCode, data)
    }
}

inline fun logMsg(block: () -> String) {
    Log.i("social-qq-demo", block())
}