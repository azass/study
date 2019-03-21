package com.example.study.app.abs

import com.example.study.model.abs.BlockItem

interface OnBlockItemSelectListener {
    fun onBlockItemSelected(index: Int, blockItem: BlockItem)
}