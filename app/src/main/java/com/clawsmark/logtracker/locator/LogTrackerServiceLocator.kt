package com.clawsmark.logtracker.locator

import com.clawsmark.logtracker.data.services.logfilemanager.LogFileDao
import com.clawsmark.logtracker.data.services.prefs.TrackerPrefs
import com.clawsmark.logtracker.data.services.logfilemanager.LogFileManager
import com.clawsmark.logtracker.data.services.prefs.TrackerPrefsDao

object LogTrackerServiceLocator {

    val LogFileDao : LogFileDao = LogFileManager
    val trackerPrefs : TrackerPrefsDao = TrackerPrefs

}