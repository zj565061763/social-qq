package com.sd.demo.social.qq.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.social.qq.databinding.ActivityMainBinding
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
        Log.i(TAG, "click login")
        FSocialQQLoginApi.login(this@MainActivity, object : FSocialQQLoginApi.LoginCallback {
            override fun onSuccess(result: QQLoginResult) {
                Log.i(TAG, "login onSuccess $result")
            }

            override fun onError(code: Int, message: String) {
                Log.i(TAG, "login onError $code $message")
            }

            override fun onCancel() {
                Log.i(TAG, "login onCancel")
            }
        })
    }

    private fun share() {
        Log.i(TAG, "click share")
        FSocialQQShareApi.shareUrl(
            this@MainActivity,
            targetUrl = "http://www.baidu.com",
            title = "我是标题",
            description = "我是描述",
            imageUrl = "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png",
            callback = object : FSocialQQShareApi.ShareCallback {
                override fun onSuccess(result: QQShareResult) {
                    Log.i(TAG, "share onSuccess $result")
                }

                override fun onError(code: Int, message: String) {
                    Log.i(TAG, "share onError $code $message")
                }

                override fun onCancel() {
                    Log.i(TAG, "share onCancel")
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FSocialQQLoginApi.onActivityResult(requestCode, resultCode, data)
        FSocialQQShareApi.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}