package com.example.study.app

import android.support.v4.app.Fragment
import com.example.study.model.Quotation
import com.example.study.model.abs.AbstractionBlock
import com.example.study.model.abs.BlockSheet

interface OnLoadFragmentListener {
    fun replaceAbsScreen(fragment: Fragment, tag: String)
    fun loadAbsIndexFragment(abstractionBlock: AbstractionBlock)
    fun loadBlockSheetFragment(blockSheet: BlockSheet)
    fun loadMenuFragment()
    fun loadAbstractionBlockMenu()
    fun loadForgettingFragment()
    fun loadWastingFragment()
    fun loadBlockItemListFragment()
    fun loadBack()
    fun loadActionBarToggle()
    fun loadQuotationEditor(quotation: Quotation)
    fun replaceMainScreen(fragment: Fragment, tag: String)
    fun replaceEbaScreen(fragment: Fragment, tag: String)
}