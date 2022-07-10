package com.sd.lib.social.qq

import android.content.Context
import androidx.core.content.FileProvider

class SocialQQFileProvider : FileProvider() {
    companion object {
        @JvmStatic
        fun getAuthority(context: Context): String {
            return "${context.packageName}.f-fp-lib-social-qq"
        }
    }
}