package com.clawsmark.logtracker.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.clawsmark.logtracker.data.buffer.AnalyticsBuffer
import com.clawsmark.logtracker.data.services.prefs.LoggyPrefs
import com.clawsmark.logtracker.data.services.prefs.LoggyPrefsImpl
import com.clawsmark.logtracker.loggy.analytics.LoggyAnalytics
import com.clawsmark.logtracker.loggy.LoggyLogcat
import com.clawsmark.logtracker.loggy.LoggyContext
import com.clawsmark.logtracker.loggy.LoggyContextImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { provideSharedPreferences(androidContext()) }
    single<LoggyPrefs> { LoggyPrefsImpl(get()) }
    single<LoggyContext> { LoggyContextImpl(get() ) }
    single { LoggyAnalytics(get(),get() ) }
    single { LoggyLogcat() }
    single { AnalyticsBuffer(get(),get()) }
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}





