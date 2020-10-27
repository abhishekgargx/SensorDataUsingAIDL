package com.abhishek.orientation.aidl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    private lateinit var orientationText: TextView
    private var orientationInterface: OrientationInterface? = null
    private var orientationServiceConnection: ServiceConnection? = null
    private var orientationServiceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        orientationText = findViewById(R.id.orientationText)
        addSensorDataObserver()
        bindOrientationService()
    }

    private fun addSensorDataObserver() {
        OrientationService.sensorData.observe(this, Observer {
            orientationText.text = it.contentToString()
        })
    }

    private fun getDataFromAIDL() {
        orientationInterface?.orientation()?.let {
            orientationText.text = it
        }
    }

    private fun bindOrientationService() {
        orientationServiceIntent = Intent(this, OrientationService::class.java)
        orientationServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                orientationInterface = OrientationInterface.Stub.asInterface(binder)
                getDataFromAIDL()
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
            }
        }
        orientationServiceIntent?.let {
            orientationServiceConnection?.let {
                bindService(
                    orientationServiceIntent,
                    it,
                    Context.BIND_AUTO_CREATE
                )
            }
        }
    }
}