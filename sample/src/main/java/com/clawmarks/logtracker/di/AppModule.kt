package com.clawmarks.loggy.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.clawmarks.loggy.prefs.LoggyPrefs
import com.clawmarks.loggy.uploader.LoggyUploader
import com.clawmarks.logtracker.fileioapi.FileioUploader
import com.clawmarks.logtracker.prefs.LoggyPrefsImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appyModule = module {
    single { provideSharedPreferences(androidContext()) }
    single<LoggyPrefs> { LoggyPrefsImpl() }
    single<LoggyUploader> { FileioUploader() }
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}





