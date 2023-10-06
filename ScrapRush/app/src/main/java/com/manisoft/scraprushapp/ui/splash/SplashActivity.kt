package com.manisoft.scraprushapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.ui.CompleteProfileActivity
import com.manisoft.scraprushapp.ui.login.LoginActivity
import com.manisoft.scraprushapp.ui.MainActivity
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.changeNavBarColor
import com.manisoft.scraprushapp.utils.changeStatusBarColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        changeStatusBarColor()
        changeNavBarColor()

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            when {
                KeyValues.readBoolean(Constants.LOGIN_STATUS, false) ?: false -> {
                    //launch dashboard screen
                    when {
                        KeyValues.readBoolean(Constants.PROFILE_STATUS, false) ?: false -> {
                            startActivity(Intent(this@SplashActivity, CompleteProfileActivity::class.java))
                            finish()
                        }

                        else -> {
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        }
                    }

                }

                else -> {
                    //launch login screen
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

}