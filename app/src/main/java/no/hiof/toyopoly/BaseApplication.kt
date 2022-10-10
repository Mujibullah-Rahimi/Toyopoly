package no.hiof.toyopoly

import android.app.Application
import android.util.Log
import androidx.annotation.Nullable
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.interfaces.UserInfo


class BaseApplication() : Application() {
    private lateinit var currentUserId: String
    private lateinit var currentUserName: String
    private val APP_ID: String = "2AEF3C8A-DEE1-4036-863A-0D7DE37F2A88"

    override fun onCreate() {
        super.onCreate()
//        FirebaseApp.initializeApp(this)
//        if (FirebaseAuth.getInstance().currentUser != null){
//            currentUser = FirebaseAuth.getInstance().currentUser!!
//        }else{
//            Log.v("Current user is empty", "Current user is empty")
//        }
        SendbirdUIKit.init(object: SendbirdUIKitAdapter {
            override fun getAppId(): String {
                return APP_ID
            }

            override fun getAccessToken(): String? {
                TODO("Not yet implemented")
            }

            override fun getUserInfo(): UserInfo {
                return object : UserInfo {
                    override fun getUserId(): String {
                        Log.v("CurrentUser in BaseApp", currentUserId)
                        return currentUserId // Specify your user ID.
                    }

                    @Nullable
                    override fun getNickname(): String {
                        Log.v("CurrentUser in BaseApp", currentUserName)
                        return currentUserName // Specify your user nickname.
                    }

                    @Nullable
                    override fun getProfileUrl(): String {
                        return ""
                    }
                }
            }

            override fun getInitResultHandler(): InitResultHandler {
                return object : InitResultHandler {
                    override fun onMigrationStarted() {
                        // DB migration has started.
                    }

                    override fun onInitFailed(e: SendbirdException) {
                        TODO("Not yet implemented")
                        // If DB migration fails, this method is called.
                    }

                    override fun onInitSucceed() {
                        // If DB migration is successful, this method is called and you can proceed to the next step.
                        // In the sample app, the `LiveData` class notifies you on the initialization progress
                        // And observes the `MutableLiveData<InitState> initState` value in `SplashActivity()`.
                        // If successful, the `LoginActivity` screen
                        // Or the `HomeActivity` screen will show.
                    }
                }
            }
        }, this)
    }

    fun setUserId(userId: String) {
        currentUserId = userId
    }

    fun setUserName(userName: String) {
        currentUserName = userName
    }
}