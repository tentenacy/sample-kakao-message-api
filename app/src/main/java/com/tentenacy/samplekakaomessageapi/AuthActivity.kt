package com.tentenacy.samplekakaomessageapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    companion object {
        const val TAG = "AuthActivity"
    }

    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "로그인 성공 ${token.accessToken}")
            startActivity(Intent(this, FriendsActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SampleKakaoMessageApi)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        btn_login_kakao.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        //로그인 필요
                    } else {
                        //기타 에러
                    }
                } else {
                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                    startActivity(Intent(this, FriendsActivity::class.java))
                    finish()
                }
            }
        } else {
            //로그인 필요
        }
    }
}