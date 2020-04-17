package com.clawsmark.logtracker.data.services.logfilemanager

object LogFileManager {



    private val serialNumber: String ="1234567890"

    private val maxBufferStringCount = 1_00
//    private val prefs = LogTrackerServiceLocator.TRACKER_PREFS


//    private val job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = job + Dispatchers.IO

//    private val memory: File = Environment.getExternalStorageDirectory()
//    private val trackerLogDir = File(memory.absolutePath.toString() + "/logs")
//    private val logcatLogDir = File(memory.absolutePath.toString() + "/logcat")
//
//    override val trackerLogs = LogFileList(trackerLogDir, 20036)
//    override val logcatLogs = LogFileList(logcatLogDir, 20036)
//
//    private val TRACKER_WRITER: LoggyWritable = LoggyWriter(trackerLogDir, serialNumber, ReportType.ANALYTIC) {
//        this.trackerLogs.update()
//    }
//
//    private val LOGCAT_WRITER: LoggyWritable = LoggyWriter(logcatLogDir, serialNumber, ReportType.REGULAR) {
//        this.logcatLogs.update()
//    }
//    init {
//        Log.i("LogFileManager", "LogFileManager: $trackerLogs")
//
//    }
//    override fun Buffer(buffer: Buffer<*>) {
//        when (buffer) {
//            is LogcatBuffer -> LOGCAT_WRITER.write(buffer)
//            is TrackerBuffer -> TRACKER_WRITER.write(buffer)
//        }
//
//    }


}