package com.clawsmark.logtracker

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.clawsmark.logtracker.api.FileUploader
import com.clawsmark.logtracker.api.FileUploader.FileUploaderCallback
import com.clawsmark.logtracker.data.services.logsender.LogSender
import com.clawsmark.logtracker.tracker.Tracker
import com.clawsmark.logtracker.utils.trace
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    var files = ArrayList<String>()
    private var pDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pDialog = ProgressDialog(this)
        pDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { dialog, which ->
            fileUploader!!.cancel()
            pDialog!!.dismiss() //dismiss dialog
        }
        findViewById<View>(R.id.btnSelectFiles).setOnClickListener {
            for (i in 1..100) {
                trace("MainActivity", "message $i")
            }
            Tracker.saveBuffer()
            LogSender.markFileAsSent("")

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && null != data) {
            if (data.clipData != null) {
                val count = data.clipData.itemCount //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (i in 0 until count) {
                    val imageUri = data.clipData.getItemAt(i).uri
                    getImageFilePath(imageUri)
                }
            }
            if (files.size > 0) {
                uploadFiles()
            }
        }
    }

    fun getImageFilePath(uri: Uri) {
        val file = File(uri.path)
        val filePath = file.path.split(":").toTypedArray()
        val image_id = filePath[filePath.size - 1]
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", arrayOf(image_id), null)
        if (cursor != null) {
            cursor.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            files.add(imagePath)
            cursor.close()
        }
    }

    var fileUploader: FileUploader? = null
    fun uploadFiles() {
        val filesToUpload = arrayOfNulls<File>(files.size)
        for (i in files.indices) {
            filesToUpload[i] = File(files[i])
        }
        showProgress("Uploading media ...")
        fileUploader = FileUploader()
        fileUploader!!.uploadFiles("/", "file", filesToUpload, object : FileUploaderCallback {
            override fun onError() {
                hideProgress()
            }

            override fun onFinish(responses: Array<String?>) {
                hideProgress()
                for (i in responses.indices) {
                    val str = responses[i]
                    Log.e("RESPONSE $i", responses[i])
                }
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                updateProgress(totalpercent, "Uploading file $filenumber", "")
                Log.e("Progress Status", "$currentpercent $totalpercent $filenumber")
            }
        })
    }

    fun updateProgress(`val`: Int, title: String?, msg: String?) {
        pDialog!!.setTitle(title)
        pDialog!!.setMessage(msg)
        pDialog!!.progress = `val`
    }

    fun showProgress(str: String?) {
        try {
            pDialog!!.setCancelable(false)
            pDialog!!.setTitle("Please wait")
            pDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            pDialog!!.max = 100 // Progress Dialog Max Value
            pDialog!!.setMessage(str)
            if (pDialog!!.isShowing) pDialog!!.dismiss()
            pDialog!!.show()
        } catch (e: Exception) {
        }
    }

    fun hideProgress() {
        try {
            if (pDialog!!.isShowing) pDialog!!.dismiss()
        } catch (e: Exception) {
        }
    }
}