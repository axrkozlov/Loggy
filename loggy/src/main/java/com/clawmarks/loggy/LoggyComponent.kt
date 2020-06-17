package com.clawmarks.loggy

interface LoggyComponent {

    val componentName: String
        get() = this::class.java.simpleName

}