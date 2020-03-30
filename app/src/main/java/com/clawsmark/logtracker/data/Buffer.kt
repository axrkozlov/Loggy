package com.clawsmark.logtracker.data

import java.util.concurrent.ConcurrentLinkedQueue

abstract class Buffer<T>:ConcurrentLinkedQueue<T>()