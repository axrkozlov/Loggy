package com.clawsmark.logtracker.locator

import com.clawsmark.logtracker.data.services.logmanager.LogFileDao
import com.clawsmark.logtracker.data.services.prefs.TrackerPrefs
import com.clawsmark.logtracker.data.services.logmanager.LogFileManager
import com.clawsmark.logtracker.data.services.prefs.TrackerPrefsDao

object LogTrackerServiceLocator {

    val LOG_FILE_DAO : LogFileDao = LogFileManager
    val trackerPrefs : TrackerPrefsDao = TrackerPrefs

}