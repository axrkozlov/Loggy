package com.clawmarks.loggy.di

import org.koin.core.Koin
import org.koin.core.KoinComponent

interface LoggyKoinComponent : KoinComponent {
    override fun getKoin(): Koin = LoggyKoinContext.koinApp.koin
}