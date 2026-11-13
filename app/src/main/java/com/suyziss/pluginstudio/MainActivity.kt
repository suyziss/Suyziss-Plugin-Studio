package com.suyziss.pluginstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.suyziss.pluginstudio.ui.PluginStudioApp
import com.suyziss.pluginstudio.ui.theme.PluginStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PluginStudioTheme { PluginStudioApp() } }
    }
}
