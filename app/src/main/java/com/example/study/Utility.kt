package com.example.study

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*

fun createEditDialog(context: Context, dialogView: View, title: String): AlertDialog {

    val builder = AlertDialog.Builder(context)
        .setView(dialogView)
        .setTitle(title)
    val dialog = builder.show()
    return dialog
}

fun closeKeyboard(activity: Activity) {
    val view = activity.currentFocus
    if (view != null) {
        val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        view.clearFocus()
    }
}

fun setupPieChartView(mPieChart: PieChart) {
    mPieChart.setUsePercentValues(true);
    val description = Description()
    description.text = "チャートの説明"
    mPieChart.setDescription(description);

    val legend = mPieChart.getLegend();
    legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

    // 円グラフに表示するデータ
    val values = Arrays.asList(40f, 30f, 20f, 10f);
    val entries = mutableListOf<PieEntry>();
    for (i in values.indices) {
        entries.add(PieEntry(values.get(i), i))
    }

    val dataSet = PieDataSet(entries, "チャートのラベル")
    dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
    dataSet.setDrawValues(true);

    val labels = Arrays.asList("A", "B", "C", "D");
    val pieData = PieData(dataSet);
    pieData.setValueFormatter(PercentFormatter());
    pieData.setValueTextSize(12f);
    pieData.setValueTextColor(Color.WHITE);

    mPieChart.setData(pieData);
}

fun setupStackBarChartView(mChart: BarChart) {
    mChart.setMaxVisibleValueCount(40)
    val yValues = mutableListOf<BarEntry>()
    val count = 14
    for (i in 1..count) {
        val val1 = (Math.random() * count).toFloat() + 20
        val val2 = (Math.random() * count).toFloat() + 20
        val val3 = (Math.random() * count).toFloat() + 20

        yValues.add(BarEntry(i.toFloat(), floatArrayOf(val1, val2, val3)))
    }

    val set1 = BarDataSet(yValues, "Satatics of USA")
    set1.setDrawIcons(true)
    set1.setStackLabels(arrayOf("Children", "Adults", "Elders"))

    set1.colors = listOf(Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0))
    val data = BarData(set1)
    data.setValueFormatter(MyValueFormatter())

    mChart.data = data
    mChart.setFitBars(true)
    mChart.invalidate()
}