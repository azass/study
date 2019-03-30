package com.example.study.app

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.study.DateUtils
import com.example.study.R
import com.example.study.StudyUtils
import com.example.study.app.eba.EbaWebFragment
import com.example.study.model.Quotation
import com.example.study.model.eba.EbaWebLecture
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.DailyResultsDao
import com.example.study.persistence.abs.AbsDailyResultsDao
import com.example.study.persistence.abs.AbsTotalResultsDao
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class ResultFragment: Fragment() {
    private lateinit var mPieChart: PieChart
    private val mQuotationList = mutableListOf<Quotation>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        val daysLeft = view.findViewById<TextView>(R.id.daysLeft)
        daysLeft.setText(StudyUtils.getDaysLeft())

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val studyProgressBar = view.findViewById<ProgressBar>(R.id.studyProgressBar)
        // 水平プログレスバーの最大値を設定します
        progressBar.max = 100
        progressBar.progress = 20
        studyProgressBar.progress = 20

        val db = BlockStudyDatabase(context!!).readableDatabase

        setupAbsResultBoard(view, db)
        setupMessageBoade(view)

//        mPieChart = view.findViewById<PieChart>(R.id.pie_chart);
//        setupPieChartView(mPieChart);
        val chart = view.findViewById<BarChart>(R.id.bar_chart)
        setupResultChart(chart, db)
        return view
    }

    fun setupAbsResultBoard(view: View, db: SQLiteDatabase) {

        val yesterdayCount = view.findViewById<TextView>(R.id.yesterdayCount)
        val todayCount = view.findViewById<TextView>(R.id.todayCount)
        val averageCount = view.findViewById<TextView>(R.id.averageCount)
        val trendCount1 = view.findViewById<TextView>(R.id.trendCount1)
        val trendCount2 = view.findViewById<TextView>(R.id.trendCount2)

        val yesterdayClearCount = view.findViewById<TextView>(R.id.yesterdayClearCount)
        val todayClearCount = view.findViewById<TextView>(R.id.todayClearCount)
        val averageClearCount = view.findViewById<TextView>(R.id.averageClearCount)
        val trendClearCount1 = view.findViewById<TextView>(R.id.trendClearCount1)
        val trendClearCount2 = view.findViewById<TextView>(R.id.trendClearCount2)

        val yesterdayPlayTime = view.findViewById<TextView>(R.id.yesterdayPlayTime)
        val todayPlayTime = view.findViewById<TextView>(R.id.todayPlayTime)
        val averagePlayTime = view.findViewById<TextView>(R.id.averagePlayTime)
        val trendPlayTime1 = view.findViewById<TextView>(R.id.trendPlayTime1)
        val trendPlayTime2 = view.findViewById<TextView>(R.id.trendPlayTime2)


        val dailyResultsDao = AbsDailyResultsDao(db)
        val totalResultsDao = AbsTotalResultsDao(db)
        val yesterdayResult = dailyResultsDao.select(DateUtils.getYesterdayLabel())
        val todayResult = dailyResultsDao.select(DateUtils.getNowDateLabel())
        val totalResult = totalResultsDao.select()

        if (yesterdayResult == null) {
            yesterdayCount.setText("ー")
            yesterdayClearCount.setText("ー")
            yesterdayPlayTime.setText("ー")
        } else {
            yesterdayCount.setText(yesterdayResult!!.answerCount.toString())
            yesterdayClearCount.setText(yesterdayResult!!.correctCount.toString())
            yesterdayPlayTime.setText(yesterdayResult.execTime.toString())
        }

        if (todayResult == null) {
            todayCount.setText("ー")
            todayClearCount.setText("ー")
            todayPlayTime.setText("ー")
        } else {
            todayCount.setText(todayResult!!.answerCount.toString())
            todayClearCount.setText(todayResult!!.correctCount.toString())
            todayPlayTime.setText(todayResult.execTime.toString())
        }

        averageCount.setText(if (totalResult!!.averageAnswerCount() == null) "ー" else totalResult.averageAnswerCount()!!.toString())
        averageClearCount.setText(if (totalResult!!.averageCorrectCount() == null) "ー" else totalResult.averageCorrectCount()!!.toString())
        averagePlayTime.setText(if (totalResult!!.averageExecTime() == null) "ー" else totalResult.averageExecTime()!!.toString())

        val cardLayout = view.findViewById<View>(R.id.cardLayout)
        cardLayout.setOnClickListener {
            fragmentManager!!
                .beginTransaction()
                .replace(R.id.content_main, ResultFrameFragment(), "ResultFrameFragment")
                .commit()
            val ebaWebLecture = EbaWebLecture(0, "", "https://eba.learning-ware.jp", "")
            fragmentManager!!
                .beginTransaction()
                .replace(R.id.result_frame_layout, EbaWebFragment.newInstance(ebaWebLecture), "eba")
                .commit()
        }
    }

    fun setupResultChart(mChart: BarChart, db: SQLiteDatabase) {
        val dao = DailyResultsDao(db)
        val data = dao.find(DateUtils.getDateLabel(-13), DateUtils.getNowDateLabel())

        val xValues = mutableListOf<String>()
        val yValues = mutableListOf<BarEntry>()

        for (i in -13..0) {
            xValues.add(DateUtils.getDateLabel(i))
        }

        for (i in xValues.indices) {
            var yValue = data[xValues[i]]
            if (yValue == null) {
                yValue = floatArrayOf(0.1f, 0.1f)
            }
            yValues.add(BarEntry(i.toFloat(), yValue))
        }

        mChart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)

        val set1 = BarDataSet(yValues, "")
        set1.setDrawIcons(true)
        set1.setStackLabels(arrayOf("ABS", "EBA"))

        set1.colors = listOf(Color.rgb(193, 37, 82), Color.rgb(255, 102, 0))
        val barData = BarData(set1)

//        barData.setValueFormatter(MyValueFormatter())

        mChart.data = barData
        mChart.setFitBars(true)
        mChart.invalidate()
    }

    fun setupMessageBoade(view: View) {

        val database = FirebaseDatabase.getInstance().reference
        database.child("Quotations").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val quotation = it.getValue<Quotation>(Quotation::class.java)!!
                    mQuotationList.add(quotation)
                }
                val rnd = Random.nextInt(0, mQuotationList.size)
                view.findViewById<TextView>(R.id.quotation).text = mQuotationList[rnd].quote + "\n" + mQuotationList[rnd].author
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}