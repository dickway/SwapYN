package com.face.util

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.key.DiffKey
import com.apkfuns.logutils.LogUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.zzkj.structure.util.toast

/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */
object GoogleLoginUtil {

    private const val RC_GOOGLE_LOGIN = 789

    private fun signInClient(activity: Activity): GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(DiffKey.GOOGLE_WEB_CLIENT_ID)
            //可以获取到邮箱信息【这一步还挺有必要，不光可以获取到邮箱信息，还会影响到下面获取性别信息】
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, googleSignInOptions)
    }

    fun signOut(activity: Activity, onComplete: ((Boolean) -> Unit)? = null) {
        signInClient(activity).signOut().addOnCompleteListener {
            LogUtils.d("signOut complete isSuccessful = ${it.isSuccessful}")
        }
    }

    fun signIn(activity: Activity) {
        val intent: Intent = signInClient(activity).signInIntent
        activity.startActivityForResult(intent, RC_GOOGLE_LOGIN)
    }

    fun signIn(fragment: Fragment, requestCode: Int = RC_GOOGLE_LOGIN) {
        val intent: Intent = signInClient(fragment.requireActivity()).signInIntent
        fragment.startActivityForResult(intent, requestCode)
    }

    fun handleSignInResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callBack: (id: String, token: String) -> Unit
    ) {
        if (requestCode != RC_GOOGLE_LOGIN) {
            return
        }
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
            val id = account.id
            val token = account.idToken
            val info = """
            id：$id
            idToken：$token
            photoUrl：${account.photoUrl}
            displayName：${account.displayName}
            familyName：${account.familyName}
            givenName：${account.givenName}
            email：${account.email}
            """.trimIndent()
            LogUtils.i(account.zad())
            if (id.isNullOrBlank() || token.isNullOrBlank()) {
                LogUtils.e("Google login id or token is null")
                toast("Fail : Google login id or token is null")
            } else {
                callBack.invoke(id, token)
            }
        } catch (e: ApiException) {
            toast("Login Fail")
            // https://developers.google.com/android/reference/com/google/android/gms/common/api/CommonStatusCodes#NETWORK_ERROR
            LogUtils.e("Google login failed: code=${e.statusCode}, msg=${e.message}")
        }
    }
}