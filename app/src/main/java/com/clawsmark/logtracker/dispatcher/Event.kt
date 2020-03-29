package com.clawsmark.logtracker.dispatcher

import com.clawsmark.logtracker.data.MessageBuffer
import java.io.File

sealed class Event(val data: Any) {
    class BufferSaveEvent(val buffer: MessageBuffer):Event(buffer)
    class DeleteFileEvent(val file: File):Event(file)
    class Simple():Event(Any())
}