package com.manisoft.scraprushapp.ui.customer.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.ui.MainActivity
import com.manisoft.scraprushapp.ui.admin.dashboard.AdminDashboardActivity
import com.manisoft.scraprushapp.ui.customer.login.LoginActivity
import com.manisoft.scraprushapp.ui.customer.profile.CompleteProfileActivity
import com.manisoft.scraprushapp.utils.CheckAppUpdate
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.UserRoles
import com.manisoft.scraprushapp.utils.changeNavBarColor
import com.manisoft.scraprushapp.utils.changeStatusBarColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity(), CheckAppUpdate.UpdateCheckCallback {
    private lateinit var checkAppUpdate: CheckAppUpdate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        changeStatusBarColor()
        changeNavBarColor()

        checkAppUpdate = CheckAppUpdate(this, this)

    }

    override fun onResume() {
        super.onResume()
        checkAppUpdate.checkUpdate()
    }

    override fun onUpdateAvailable() {

    }

    override fun onUpdateNotAvailable() {
        moveToNextActivity()
    }

    private fun moveToNextActivity() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            when {
                KeyValues.readBoolean(Constants.LOGIN_STATUS, false) ?: false -> {
                    when (KeyValues.readString(Constants.USER_ROLE, "") ?: "") {
                        UserRoles.SUPER_ADMIN, UserRoles.ADMIN -> {
                            startActivity(Intent(this@SplashActivity, AdminDashboardActivity::class.java))
                            finish()
                        }

                        UserRoles.CUSTOMER -> {
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