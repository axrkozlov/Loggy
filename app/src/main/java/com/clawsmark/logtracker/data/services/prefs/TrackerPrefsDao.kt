package com.clawsmark.logtracker.data.services.prefs

interface TrackerPrefsDao {

    val maxTrackerBufferSize : Int
    val maxLogcatBufferSize : Int

    //minutes
    val timeBeforeStartSending : Int
    //kB
    val maxLogFilesSize : Int


}