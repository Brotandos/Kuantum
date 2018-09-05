package com.brotandos.kuantumlib

import org.jetbrains.anko.doAsync

class DatabaseTextKuantum(text: String = "", private val query: (String) -> Unit) : TextKuantum(text) {

    override fun onFocusChanged() {
        super.onFocusChanged()
        save()
    }

    fun save() {
        doAsync {
            query(value)
        }
    }
}