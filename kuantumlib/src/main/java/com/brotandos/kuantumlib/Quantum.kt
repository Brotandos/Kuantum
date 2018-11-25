package com.brotandos.kuantumlib

import android.view.View

abstract class Quantum<T, V: View> {

    abstract var value: T

    protected var view: V? = null
}