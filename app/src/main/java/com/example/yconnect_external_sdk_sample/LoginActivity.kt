package com.example.yconnect_external_sdk_sample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import jp.co.yahoo.yconnect.YConnectImplicit
import jp.co.yahoo.yconnect.core.api.ApiClientException
import jp.co.yahoo.yconnect.core.oidc.OIDCScope
import jp.co.yahoo.yconnect.core.oidc.UserInfoObject
import jp.co.yahoo.yconnect.core.util.YConnectLogger

class LoginActivity : AppCompatActivity() {
    val TAG = LoginActivity::class.java.simpleName

    val clientId = "dj00aiZpPXhwMUlBc255TndVVSZzPWNvbnN1bWVyc2VjcmV0Jng9YmU-"
    val SCHEME = "yj-ntoyozum:/"
    val state = "44GC44Ga44GrWeOCk+ODmuODreODmuODrShez4leKQ=="
    val nonce = "KOOAjeODu8+J44O7KeOAjVlhaG9vISAo77yP44O7z4njg7sp77yPSkFQQU4="
    val scope = arrayOf(
        OIDCScope.OPENID, OIDCScope.PROFILE,
        OIDCScope.EMAIL, OIDCScope.ADDRESS
    )

    val yconnect: YConnectImplicit by lazy { YConnectImplicit.getInstance() }

    @SuppressLint("StaticFieldLeak")
    val userinfoTask = object : AsyncTask<String, String, UserInfoObject>() {
        override fun doInBackground(vararg p0: String?): UserInfoObject {
            try {
                yconnect.requestUserInfo(p0[0])
            } catch (e: ApiClientException) {
                Log.e(
                    TAG,
                    "error:${e.error} description:${e.errorDescription} message: ${e.message}"
                )
                cancel(true)
            }
            Log.i(TAG, "userInfoObject:${yconnect.userInfoObject}")
            return yconnect.userInfoObject
        }

        override fun onPostExecute(result: UserInfoObject?) {
            super.onPostExecute(result)
            finish()
        }

        override fun onCancelled() {
            super.onCancelled()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        YConnectLogger.setLogLevel(YConnectLogger.DEBUG)

        if (intent.action == Intent.ACTION_VIEW) {
            yconnect.parseAuthorizationResponse(intent.data, SCHEME, state)
            userinfoTask.execute(yconnect.accessToken)
            return
        }

        yconnect.init(clientId, SCHEME, state, null, emptyArray(), scope, nonce, null, null)
        yconnect.requestAuthorization(this)
    }
}