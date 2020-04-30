package com.clawmarks.logtracker

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.clawmarks.loggy.Loggy
import com.clawmarks.loggy.userinteraction.UserInteractionDispatcher
import com.clawmarks.logtracker.utils.trace
import kotlinx.coroutines.*
import org.koin.android.ext.android.get
import java.util.*

class MainActivity : AppCompatActivity() {
    var files = ArrayList<String>()
    private var pDialog: ProgressDialog? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    val userInteractionDispatcher = get<UserInteractionDispatcher>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        trace("asdf", "asdf")
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    2)
        }
        findViewById<View>(R.id.btnSelectFiles).setOnClickListener {
            CoroutineScope(Job()).launch(Dispatchers.IO) {
                Loggy.startSending()
                delay(100)
                Loggy.stopSending()
            }

//            LogSender.startSending()
//            coroutineScope.launch {
//                for (i in 0..10_000) {
//                    trace("MainActivity", "message $i LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOONG")
//                }
//                Tracker.saveBuffer()
//            }

//            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                        2)
//            } else {
//                val intent = Intent()
//                intent.type = "image/*"
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                intent.action = Intent.ACTION_GET_CONTENT
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
//            }


        }
        findViewById<View>(R.id.btnStop).setOnClickListener {
            Loggy.stopSending()
            Log.i("MainActivity", "onCreate: ")
//            TestClass().testfun()
        }
        findViewById<View>(R.id.btngenerateLog).setOnClickListener {
            Loggy.stopSending()
            CoroutineScope(Job()).launch(Dispatchers.IO) {
                for (i in 1..10_000) {
                    trace("SOME", "Message\n $i")
                    Log.i("MainActivity", "onCreate: \n someinfo $i \n")

//                    if (i == 500) throw Exception("rerere")
//                    delay(2)
                }

            }

        }
    }


    override fun onUserInteraction() {
        super.onUserInteraction()
//        userInteractionDispatcher.onInteraction()
    }

}