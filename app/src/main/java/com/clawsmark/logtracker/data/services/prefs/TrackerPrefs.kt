package com.clawsmark.logtracker.data.services.prefs

object TrackerPrefs : TrackerPrefsDao {
    override val maxTrackerBufferSize: Int
        get() = 100
    override val maxLogcatBufferSize: Int
        get() = 100

    override val timeBeforeStartSending: Int
        get() = 1
    override val maxLogFilesSize: Int
        get() = 10


}