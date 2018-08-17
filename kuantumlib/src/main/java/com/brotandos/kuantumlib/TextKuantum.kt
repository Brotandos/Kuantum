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
class TextKuantum(text: String = "") : Kuantum<String, TextView>() {
    override var value: String = text
        set(value) {
            field = value
            handleReaction(value)
            viewList.forEach {
                if (it.isFocused.not()) it.text = value
                it.handleReflectiveReaction(value)
            }
        }

    constructor(text: String = "", reaction: (String) -> Unit) : this(text) {
        handleReaction = reaction
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) { value = s.toString() }
    }

    private val onFocusChangeListener = View.OnFocusChangeListener { v, focused -> if (v is EditText)
        if (focused) v.addTextChangedListener(textWatcher)
        else v.removeTextChangedListener(textWatcher)
    }

    override fun add(view: TextView) {
        super.add(view)
        view.text = value
        (view as? EditText)?.onFocusChangeListener = onFocusChangeListener
    }

    override fun toString(): String {
        return value
    }
}