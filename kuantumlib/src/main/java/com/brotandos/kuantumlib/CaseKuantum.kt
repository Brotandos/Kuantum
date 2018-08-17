package com.brotandos.kuantumlib

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

/**
 * @author: Brotandos
 * @creation_date: 08.04.2018
 */
class CaseKuantum<T>(private val cases: List<Case<T>>, initialPosition: Int = 0) :
    Kuantum<Int, Spinner>() {

    constructor(vararg cases: Case<T>, initialPosition: Int = 0) : this(
        cases.toList(),
        initialPosition
    )

    override var value: Int = initialPosition // selected position
        set(value) {
            field = value
            viewList.forEach { it.setSelection(value) }
            cases[value].handleReaction()
        }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            value = position
        }
    }

    val current: T get() = cases[value].caseValue

    override fun add(view: Spinner) {
        view.apply {
            super.add(this)
            adapter = ArrayAdapter<Case<T>>(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                cases
            ).apply {
                setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            }
            setSelection(value)
            onItemSelectedListener = this@CaseKuantum.onItemSelectedListener
        }
    }

    open class Case<T>(val caseValue: T,
                       val hReaction: (T) -> Unit = {},
                       private val label: String = caseValue.toString()) {

        fun handleReaction() = hReaction(caseValue)

        override fun toString() = label
    }
}


class CaseKuantumBuilder<T>(private var onChangeSubscriber: TextKuantum? = null) {
    var selectedPosition: Int = -1

    private val cases = mutableListOf<CaseKuantum.Case<T>>()

    fun case(
        value: T,
        label: String = value.toString(),
        hReaction: (T) -> Unit = {}
    ): CaseKuantum.Case<T> {
        val caseChangedReaction: (T) -> Unit = { caseValue ->
            onChangeSubscriber?.let { it becomes caseValue.toString() }
            hReaction(caseValue)
        }

        val case = CaseKuantum.Case(value, caseChangedReaction, label)
        cases += case
        return case
    }

    operator fun CaseKuantum.Case<T>.not(): CaseKuantum.Case<T> {
        if (selectedPosition != -1) throw RuntimeException("There's already selected case: $selectedPosition")
        selectedPosition = cases.indexOf(this)
        return this
    }

    fun build(): CaseKuantum<T> {
        val qCases = CaseKuantum(cases)
        if (selectedPosition != -1)
            qCases becomes selectedPosition
        return qCases
    }
}

fun <T> caseKuantum(
    onCaseChangeSubscriber: TextKuantum? = null,
    init: CaseKuantumBuilder<T>.() -> Unit
): CaseKuantum<T> {
    val caseKuantumBuilder = CaseKuantumBuilder<T>(onCaseChangeSubscriber)
    caseKuantumBuilder.init()
    return caseKuantumBuilder.build()
}