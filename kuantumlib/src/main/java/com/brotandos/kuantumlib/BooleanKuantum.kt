package com.brotandos.kuantumlib

import android.view.View
import android.widget.CheckBox

/**
 * @author: Brotandos
 * @creation_date: 08.04.2018
 */
class BooleanKuantum(initialValue: Boolean = false): Kuantum<Boolean, CheckBox>() {
    override var value: Boolean = initialValue
        set(value) {
            if (field == value) return
            field = value
            handleReaction(value)
            viewList.forEach { it.toggle() }
        }

    private val onClickListener = View.OnClickListener {
        value = !value
        (it as CheckBox).toggle()
    }

    override fun add(view: CheckBox) {
        super.add(view)
        view.isChecked = value
        view.setOnClickListener(onClickListener)
    }

    fun add(vCheck: CheckBox, initialValue: Boolean) {
        vCheck.apply {
            super.add(this)
            isChecked = if (initialValue) value else !value
            setOnClickListener(onClickListener)
        }
    }
}