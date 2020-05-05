package com.clawmarks.loggy.di

import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

object LoggyKoinContext {
    val koinApp : KoinApplication = koinApplication {
        modules(loggyModule)
    }
}