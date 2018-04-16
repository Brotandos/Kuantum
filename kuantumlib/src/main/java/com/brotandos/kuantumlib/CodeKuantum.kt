package com.brotandos.kuantumlib

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

/**
 * @author: Brotandos
 * @creation_date: 08.04.2018
 */
class CodeKuantum<T: Any> (
        initialPosition: Int,
        private val tuple: List<Tuple<T>>
): Kuantum<Int, Spinner>() {
    override var value: Int = initialPosition // selected position
        set(value) {
            field = value
            viewList.forEach { it.setSelection(value) }
            tuple[value].apply { reaction(code) }
        }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            value = position
        }
    }

    override fun plus(view: Spinner): Kuantum<Int, Spinner> {
        view.apply {
            super.plus(this)
            adapter = ArrayAdapter<Tuple<T>>(context, R.layout.support_simple_spinner_dropdown_item, tuple).apply {
                setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            }
            setSelection(value)
            onItemSelectedListener = this@CodeKuantum.onItemSelectedListener
        }
        return this
    }

    open class Tuple<T> (val code: T, val reaction: (T) -> Unit)
}