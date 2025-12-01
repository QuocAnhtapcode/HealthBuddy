package com.example.healthbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthbuddy.screens.MainApp
import com.example.healthbuddy.screens.test.WorkoutViewModel
import com.example.healthbuddy.ui.theme.HealthBuddyTheme
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val workoutViewModel by viewModels<WorkoutViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthBuddyTheme {
                MainApp()
                //TestScreen(phoneViewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        workoutViewModel.start()
    }
    override fun onStop() {
        workoutViewModel.stop()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            try{
                Wearable.getCapabilityClient(this@MainActivity)
                    .addLocalCapability(com.example.healthbuddy.common.DataPaths.PHONE_CAPABILITY)
                    .await()
            } catch (e: Exception){

            }
        }
    }

    override fun onPause() {
        lifecycleScope.launch {
            try{
                Wearable.getCapabilityClient(this@MainActivity)
                    .removeLocalCapability(com.example.healthbuddy.common.DataPaths.PHONE_CAPABILITY)
                    .await()
            } catch (e: Exception){

            }
        }
        super.onPause()
    }
}
