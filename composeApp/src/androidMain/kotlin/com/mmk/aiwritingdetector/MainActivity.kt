package com.mmk.aiwritingdetector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mmk.aiwritingdetector.util.ActivityProvider
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityProvider.currentActivity = this
        enableEdgeToEdge()
        /*startKoin {
            modules(androidPlatformModule)
        }*/
        setContent {
            App {
                androidContext(this@MainActivity.applicationContext)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        ActivityProvider.currentActivity = null
    }
}
