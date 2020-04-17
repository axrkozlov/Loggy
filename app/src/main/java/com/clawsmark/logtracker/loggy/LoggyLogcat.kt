package com.clawsmark.logtracker.loggy

class LoggyLogcat {
    var level = 1
        set(value) {
            if (value >= 2) isLogcatEnabled = true
            if (value >= 3) isLogcatEnabled = true
            if (value >= 4) isFullLogcatEnabled = true

            field = value
        }
    internal var isLogcatCrashEnabled = false
    internal var isLogcatEnabled = false
    internal var isFullLogcatEnabled = false

}