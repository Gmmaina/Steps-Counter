package com.example.dailyburn.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.dailyburn.R
import com.example.dailyburn.activities.MainActivity
import com.example.dailyburn.repositories.StepRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepRepository = StepRepository()

    private var initialStepCount = -1
    private var steps = 0
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate() {
        super.onCreate()

        // Initialize sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Log.e("StepService", "No step counter sensor found on device")
        }

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Register sensor listener
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        // Load today's step count from Firebase
        if (userId.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val todaySteps = stepRepository.getTodaySteps(userId)
                if (todaySteps != null) {
                    steps = todaySteps.steps
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        saveSteps()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val currentSteps = event.values[0].toInt()

            // Initialize the counter if this is the first reading
            if (initialStepCount == -1) {
                initialStepCount = currentSteps
            }

            // Calculate steps taken since service started
            val stepsSinceReboot = currentSteps - initialStepCount

            // Update the steps count and save to Firebase
            if (stepsSinceReboot > 0) {
                steps += stepsSinceReboot
                initialStepCount = currentSteps // Reset for next reading

                // Save steps periodically to avoid too many writes
                if (steps % 10 == 0) {
                    saveSteps()
                    updateNotification()

                    // Broadcast that steps have been updated
                    val intent = Intent(ACTION_STEPS_UPDATED)
                    intent.putExtra("steps", steps)
                    sendBroadcast(intent)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not needed for step counter
    }

    private fun saveSteps() {
        if (userId.isEmpty() || steps <= 0) return

        CoroutineScope(Dispatchers.IO).launch {
            stepRepository.saveSteps(userId, steps)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Step Counter",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks your daily steps"
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("FitTrack Active")
        .setContentText("Counting steps: $steps")
        .setSmallIcon(R.drawable.ic_directions_walk)
        .setContentIntent(getPendingIntent())
        .build()

    private fun updateNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(
            this, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    companion object {
        private const val CHANNEL_ID = "step_counter_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_STEPS_UPDATED = "com.example.fittrack.STEPS_UPDATED"
    }
}