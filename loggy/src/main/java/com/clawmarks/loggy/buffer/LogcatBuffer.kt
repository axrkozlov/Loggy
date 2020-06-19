package com.clawmarks.loggy.buffer


import com.clawmarks.loggy.message.LogcatMessage
import com.clawmarks.loggy.message.Message
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.writer.LogcatBufferWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max
import kotlin.math.min

class LogcatBuffer(override val context: LoggyContext, override val bufferWriter: LogcatBufferWriter) : LoggyContextComponent, Buffer() {


    private val localBuffer = ConcurrentLinkedQueue<Message>()
    private var isLocalBufferEnabled = false
    val pid = android.os.Process.myPid()
    override val size: Int
        get() = if (isLocalBufferEnabled) localBuffer.size else 0

    override val bufferSize: Int
        get() = prefs.bufferSize
    override val maxSize: Int
        get() = prefs.bufferOverflowSize

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
        var size = max(prefs.logcatBufferSizeKb, 100)
        size = min(size, 8192)
        try {
            val command = "logcat -G ${size}K"
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