package com.brotandos.kuantumlib

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView

/**
 * @author: Brotandos
 * @creation_date: 08.04.2018
 */
open class TextKuantum(text: String = "") : Kuantum<String, TextView>() {
    override var value: String = text
        set(value) {
            field = value
            viewList.forEach {
                /**
                 * If view is not under the focus, text must be updated
                 * */
                val needToUpdateValue = !it.isFocused
                if (needToUpdateValue) it.text = value
                it.handleReflectiveReaction(value)
            }
            handleReaction(value)
        }

    constructor(text: String = "", reaction: (String) -> Unit) : this(text) {
        handleReaction = reaction
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) { value = s.toString() }
    }

    /**
     * Need to restrict infinite text changing
     * If view is under the focus, update [value] by textWatcher
     * else just update by [EditText.setText]
     * */
    private val onFocusChangeListener = View.OnFocusChangeListener { v, focused -> if (v is EditText)
        if (focused) v.addTextChangedListener(textWatcher)
        else v.removeTextChangedListener(textWatcher)
        onFocusChanged()
    }

    open fun onFocusChanged() {}

    override fun add(view: TextView) {
        super.add(view)
        view.text = value
        (view as? EditText)?.onFocusChangeListener = onFocusChangeListener
    }

    override fun toString(): String {
        return value
    }
}