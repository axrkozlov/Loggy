package com.clawmarks.loggy.di

import com.clawmarks.loggy.buffer.AnalyticsBuffer
import com.clawmarks.loggy.buffer.LogcatBuffer
import com.clawmarks.loggy.filelist.LoggyFileListState
import com.clawmarks.loggy.sender.LoggySender
import com.clawmarks.loggy.prefs.LoggyPrefs
import com.clawmarks.loggy.prefs.LoggyDefaultPrefs
import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.context.LoggyContextImpl
import com.clawmarks.loggy.filelist.AnalyticsFileList
import com.clawmarks.loggy.filelist.LogcatFileList
import com.clawmarks.loggy.sender.SentNamesHolder
import com.clawmarks.loggy.uploader.LoggyDefaultUploader
import com.clawmarks.loggy.uploader.LoggyUploader
import com.clawmarks.loggy.userinteraction.*
import com.clawmarks.loggy.writer.AnalyticsBufferWriter
import com.clawmarks.loggy.writer.LogcatBufferWriter
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module

val loggyModule = module {
    single<LoggyContext> { LoggyContextImpl(get()) }

    single { AnalyticsFileList(get()) }
    single { LogcatFileList(get()) }
    single(named("analyticsFileListState")) { provideAnalyticsFileListState(get()) }
    single(named("logcatFileListState")) { provideLogcatFileListState(get()) }

    single { AnalyticsBufferWriter(get(), get(named("analyticsFileListState"))) }
    single { AnalyticsBuffer(get(), get()) }
    single { LogcatBufferWriter(get(), get(named("logcatFileListState"))) }
    single { LogcatBuffer(get(), get()) }
    single { SentNamesHolder(get()) }
    single { LoggySender(get(), get(), get(), get(), get()) }
    single<LoggyPrefs> { LoggyDefaultPrefs() }
    single<LoggyUploader> { LoggyDefaultUploader() }
    single { InteractionPermissionHandler(get()) } binds arrayOf(SendingPermissionObserver::class, UserInteractionObserver::class)
    single { provideUserInteractionDispatcher(get(), get()) }
    single { provideSendingPermissionDispatcher(get(), get()) }
}

private fun provideAnalyticsFileListState(loggyFileList: AnalyticsFileList): LoggyFileListState {
    return loggyFileList.state
}

private fun provideLogcatFileListState(loggyFileList: LogcatFileList): LoggyFileListState {
    return loggyFileList.state
}

private fun provideUserInteractionDispatcher(userInteractionObserver: UserInteractionObserver, context: LoggyContext): UserInteractionDispatcher {
    val dispatcher = PeriodicCheckIdleDispatcher(context)
    dispatcher.addObserver(userInteractionObserver)
    return dispatcher
}

private fun provideSendingPermissionDispatcher(sendingPermissionObserver: SendingPermissionObserver, context: LoggyContext): SendingPermissionDispatcher {
    val dispatcher = SendingPermissionDispatcherImpl(context)
    dispatcher.addObserver(sendingPermissionObserver)
    return dispatcher
}









