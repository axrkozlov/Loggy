package com.clawsmark.logtracker.data.buffer


import com.clawsmark.logtracker.data.writer.BufferWriter
import com.clawsmark.logtracker.data.LogcatMessage
import com.clawsmark.logtracker.data.Message
import com.clawsmark.logtracker.loggy.LoggyComponent
import com.clawsmark.logtracker.loggy.LoggyContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max
import kotlin.math.min

class LogcatBuffer(override val context: LoggyContext, override val bufferWriter: BufferWriter) : LoggyComponent, Buffer() {
    private val localBuffer = ConcurrentLinkedQueue<Message>()
    private var isLocalBufferEnabled = false

    override val size: Int
        get() = if (isLocalBufferEnabled) localBuffer.size else 0

    override val blockSize: Int
        get() = prefs.bufferBlockSize
    override val maxSize: Int
        get() = prefs.maxBufferSize

    private var bufferedReader: BufferedReader? = null
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
        localBuffer.add(message)
    }

    override fun pull(): Message? = if (isLocalBufferEnabled) localBuffer.poll() else readLogcatLine()

    private fun readLogcatLine(): Message? = bufferedReader?.readLine()?.let { LogcatMessage(it) }

    private fun setupLogcatBufferSize() {
        var size = min(prefs.logcatBufferSizeKb, 100)
        size = max(size, prefs.logcatMaxBufferSizeKb)
        try {
            val command = "logcat -g ${size}K"
            Runtime.getRuntime().exec(command)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupBuffer() {
        try {
            val command = if (context.isLogcatFullEnabled) "logcat -d -v time"
            else "logcat -d -v time"
            val process: Process = Runtime.getRuntime().exec(command)
            bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}