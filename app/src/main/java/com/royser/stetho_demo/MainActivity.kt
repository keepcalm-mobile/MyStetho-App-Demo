package com.royser.stetho_demo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.royser.stetho_demo.database.AppDatabase
import com.royser.stetho_demo.database.User
import com.royser.stetho_demo.service.api.DemoApi
import com.royser.stetho_demo.service.api.DemoURL
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    companion object {
        const val MY_BIRTHDATE = "MY_BIRTHDATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        assignSharePreference()
        initDatabase()

        buttonCallService.setOnClickListener {
            callService()
            Timber.d("callService()")
        }

        buttonGetBirthdate.setOnClickListener {
            showBirthdate()
            Timber.d("showBirthdate()")
        }

        buttonWebView.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
            Timber.d("openWebView()")
        }
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            //TODO #2.2 : Add StethoInterceptor
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }


    @SuppressLint("SetTextI18n")
    private fun showBirthdate() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val myBirthdate = sharedPref.getInt(MY_BIRTHDATE, 0)

        textViewBirthDate.text = "My Birthdate is $myBirthdate"
    }

    private fun assignSharePreference() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(MY_BIRTHDATE, 1991)
            commit()
        }
    }

    private fun callService() {
        val retrofit = Retrofit.Builder()
            .baseUrl(DemoURL.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()

        val api: DemoApi = retrofit.create(DemoApi::class.java)

        CoroutineScope(Dispatchers.Default).launch {
            val response = withContext(Dispatchers.IO) {
                api.getWeather("44418", "2013/4/27")
            }

            Timber.d(response.toString())
        }
    }

    private fun initDatabase() {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

        CoroutineScope(Dispatchers.Default).launch {
            withContext(Dispatchers.IO) {
                db.clearAllTables()

                db.userDao()
                    .insertAll(
                        User(1, "John", "Charter"),
                        User(2, "Jim", "Carry"),
                        User(3, "Smith", "Arnold"),
                        User(4, "Bob", "Lee")
                    )
            }
        }
    }
}
