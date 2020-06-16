package com.clawmarks.loggy.sender

import com.clawmarks.loggy.context.LoggyContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Exception
import java.util.concurrent.CopyOnWriteArraySet

class SentNamesHolder(context: LoggyContext) {

    private val coroutineScope = CoroutineScope(Job())
    private val file: File = File(context.prefs.loggyPath, ".sentfilenames")

    fun save(set: MutableSet<String>) {
        coroutineScope.launch(Dispatchers.IO) {
            writeFile(set)
        }
    }

    private fun writeFile(set: MutableSet<String>) {
        try {
            if (set.isEmpty()) return
            val fOut = FileOutputStream(file)
            val osw = OutputStreamWriter(fOut)
            set.forEach {
                osw.write("$it\n")
            }
            osw.flush()
            osw.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun load(): CopyOnWriteArraySet<String> {
        val intoSet = CopyOnWriteArraySet<String>()
        if (file.exists()) {
            coroutineScope.launch(Dispatchers.IO) {
                readFile(intoSet)
            }
        }
        return intoSet
    }

    private fun readFile(intoSet: MutableSet<String>) {
        try {
            val fileInputStream = FileInputStream(file)
            val isr = InputStreamReader(fileInputStream)
            val br = BufferedReader(isr)
            var line: String? = ""
            while (line != null) {
                line = br.readLine()
                line?.let { intoSet.add(line) }
            }

        } catch (e: Exception) {
            e.printStackTrace()


        }
    }

}