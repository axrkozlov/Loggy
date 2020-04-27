package com.clawmarks.loggy.buffer


import com.clawmarks.loggy.writer.BufferWriter
import com.clawmarks.loggy.message.LogcatMessage
import com.clawmarks.loggy.message.Message
import com.clawmarks.loggy.LoggyComponent
import com.clawmarks.loggy.context.LoggyContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentLinkedQueue

class LogcatBuffer(override val context: LoggyContext, override val bufferWriter: BufferWriter) : LoggyComponent, Buffer() {


    private val localBuffer = ConcurrentLinkedQueue<Message>()
    private var isLocalBufferEnabled = false
    val pid = android.os.Process.myPid()
    override val size: Int
        get() = if (isLocalBufferEnabled) localBuffer.size else 0

    override val blockSize: Int
        get() = prefs.bufferBlockSize
    override val maxSize: Int
        get() = prefs.maxBufferSize

    private var bufferedReader: BufferedReader? = null

    init {
        register()
    }
    override fun onPrefsUpdated() {
        setupLogcatBufferSize()
        setupBuffer()
        isLocalBufferEnabled = context.isLogcatAppEnabled || context.isLogcatFullEnabled
        if (isLocalBufferEnabled) return startLocalBuffer()
    }

    private fun startLocalBuffer() {
        val coroutineScope = CoroutineScope(Job())
        coroutineScope.launch(Dispatchers.IO) {
            while (isLocalBufferEnabled) {
                readLogcatLine()?.let {
                    push(it)
                }
            }
        }
    }

    override fun push(message: Message) {
        if (isBufferAvailable)  localBuffer.add(message)
    }

    override fun pull(): Message? = if (isLocalBufferEnabled) localBuffer.poll() else readLogcatLine()

    private fun readLogcatLine(): Message? = bufferedReader?.readLine()?.let { LogcatMessage(it) }

    private fun setupLogcatBufferSize() {
        try {
            val command = "logcat -g ${prefs.logcatBufferSizeKb}K -c"
            Runtime.getRuntime().exec(command)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupBuffer() {
        try {
            val command = if (context.isLogcatFullEnabled) "logcat -v time"
            else "logcat -v time $pid-* "
            val process: Process = Runtime.getRuntime().exec(command)
            bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}