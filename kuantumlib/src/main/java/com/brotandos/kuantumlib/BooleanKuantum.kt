package com.brotandos.kuantumlib

import android.view.View
import android.widget.CheckBox

/**
 * @author: Brotandos
 * @creation_date: 08.04.2018
 */
open class BooleanKuantum(initialValue: Boolean = false): Kuantum<Boolean, CheckBox>() {
    override var value: Boolean = initialValue
        set(value) {
            if (field == value) return
            field = value
            handleReaction(value)
            triggers.forEach { it(value) }
            viewList.forEach { it.toggle() }
        }

    private val triggers = mutableListOf<(Boolean) -> Unit>()

    open fun clearTriggers() {
        triggers.clear()
    }

    override fun reset() {
        super.reset()
        clearTriggers()
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

    infix fun triggers(trigger: (Boolean) -> Unit) {
        this.triggers.add(trigger)
        trigger(value)
    }
}

/**
 * BooleanKuantum's visibility trigger of View
 * 't' marker stands for 'trigger'
 * */
infix fun <T: View> T.visibility(qVisibility: BooleanKuantum): T {
    qVisibility triggers { this.visibility = if (it) View.VISIBLE else View.GONE }
    return this
}