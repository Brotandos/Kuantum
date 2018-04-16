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
            reactions.forEach { it(value) }
            viewList.forEach { it.toggle() }
        }

    private val onClickListener = View.OnClickListener {
        value = !value
        (it as CheckBox).toggle()
    }

    override fun plus(view: CheckBox): BooleanKuantum {
        super.plus(view)
        view.isChecked = value
        view.setOnClickListener(onClickListener)
        return this
    }

    operator fun plus(pair: Pair<CheckBox, Boolean>): BooleanKuantum {
        pair.first.apply {
            super.plus(this)
            isChecked = if (pair.second) value else !value
            setOnClickListener(onClickListener)
        }
        return this
    }
}