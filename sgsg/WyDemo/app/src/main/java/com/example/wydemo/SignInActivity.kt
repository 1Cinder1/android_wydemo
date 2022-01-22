package com.example.wydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.account
import kotlinx.android.synthetic.main.activity_sign_up.password
import kotlinx.android.synthetic.main.activity_sign_up.signUpBtn
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class SignInActivity : AppCompatActivity() {
    private val relaAddress = "/user/account/login"
    private val args = HashMap<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        //登录逻辑
        signInBtn.setOnClickListener {
            val accountInput = account.text.toString()
            val passwordInput = password.text.toString()
            if (accountInput != "" && passwordInput != "") {
                args["openid"] = accountInput
                args["password"] = passwordInput
                HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
                    override fun onResponse(call: Call, response: Response) {
//                        Log.d("sgsg", "发送请求成功")
                        val responseData: String? = response.body()?.string()
                        if (responseData != null) {
//                            Log.d("sgsg", responseData)
                            if (parseJSONWithJSONObject(responseData)) {
                                Thread.sleep(1000)
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }
                })
            } else Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseJSONWithJSONObject(jsonData: String): Boolean {
        try {
            val jsonObj = JSONObject(jsonData)
            if (jsonObj.has("error")) {
                val msg = jsonObj.getString("message")
                runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() }
                return false
            }
            User.signIn = true
            val data = jsonObj.getString("data")
            val jsonObj2 = JSONObject(data)
            User.id = jsonObj2.getString("userOpenid")
            User.pwd = jsonObj2.getString("userPassword")
            User.certification = if (jsonObj2.getString("certification") == "1") true else false
            User.creditLevel = jsonObj2.getString("creditLevel").toInt()
            runOnUiThread { Toast.makeText(applicationContext, "登录成功", Toast.LENGTH_SHORT).show() }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}