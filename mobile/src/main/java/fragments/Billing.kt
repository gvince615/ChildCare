/*
 * Copyright (c) 2016 Lung Razvan
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import billing.BillingFamily
import billing.BillingFamilyAdapter
import billing.BillingPresenter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.vince.childcare.R
import kotlinx.android.synthetic.main.fragment_billing.view.*


class Billing : Fragment() {

  private lateinit var billingFamilyAdapter: BillingFamilyAdapter
  private var billingFamilies: ArrayList<BillingFamily> = ArrayList()
  private lateinit var rv: RecyclerView
  private val billingPresenter = BillingPresenter()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    val view: View = inflater.inflate(R.layout.fragment_billing, container, false)
    setupRecyclerView(view)
    billingPresenter.setUp(this, billingFamilies)
    setupBarGraph(view)
    return view
  }

  private fun setupBarGraph(view: View) {

    view.billingChart.setDrawBarShadow(false)
    view.billingChart.setDrawValueAboveBar(true)

    val xAxis = view.billingChart.xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.granularity = 1f // only intervals of 1 day
    xAxis.labelCount = 7


    val leftAxis = view.billingChart.axisLeft
    leftAxis.setLabelCount(8, false)
    leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
    leftAxis.spaceTop = 15f
    leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

    val rightAxis = view.billingChart.axisRight
    rightAxis.setDrawGridLines(false)
    rightAxis.setLabelCount(8, false)
    rightAxis.spaceTop = 15f
    rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

    setData(4, 10f, view.billingChart)

    // mChart.setDrawLegend(false);
  }

  private fun setData(count: Int, range: Float, dashboard_bar_graph: BarChart) {

    val start = 1f

    val yVals1 = ArrayList<BarEntry>()

    var i = start.toInt()
    while (i < start + count.toFloat() + 1f) {
      val mult = range + 1
      val `val` = (Math.random() * mult).toFloat()

      if (Math.random() * 100 < 10) {
        yVals1.add(BarEntry(i.toFloat(), `val`, resources.getDrawable(R.drawable.ic_tab_three, null)))
      } else {
        yVals1.add(BarEntry(i.toFloat(), `val`))
      }
      i++
    }

    val set1: BarDataSet

    if (dashboard_bar_graph.data != null && dashboard_bar_graph.data.dataSetCount > 0) {
      set1 = dashboard_bar_graph.data.getDataSetByIndex(0) as BarDataSet
      set1.values = yVals1
      dashboard_bar_graph.data.notifyDataChanged()
      dashboard_bar_graph.notifyDataSetChanged()
    } else {
      set1 = BarDataSet(yVals1, "Registration by Bracket")

      set1.colors = addColors()

      val dataSets = ArrayList<IBarDataSet>()
      dataSets.add(set1)

      val data = BarData(dataSets)
      data.setValueTextSize(10f)
      data.barWidth = 0.9f

      dashboard_bar_graph.data = data
    }
  }

  private fun addColors(): ArrayList<Int> {
    val colors = ArrayList<Int>()

    for (c in ColorTemplate.MATERIAL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.JOYFUL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.COLORFUL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.PASTEL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.LIBERTY_COLORS)
      colors.add(c)

    for (c in ColorTemplate.VORDIPLOM_COLORS)
      colors.add(c)
    return colors
  }

  override fun onResume() {
    super.onResume()
    billingPresenter.getBillingFragmentData(FirebaseAuth.getInstance().currentUser)
  }

  fun updateBilling() {
    billingPresenter.getBillingFragmentData(FirebaseAuth.getInstance().currentUser)
  }

  private fun setupRecyclerView(view: View) {
    rv = view.billing_family_rv as RecyclerView
    rv.layoutManager = LinearLayoutManager(this.context)
    billingFamilyAdapter = BillingFamilyAdapter(this.context!!, billingFamilies)
    rv.adapter = billingFamilyAdapter
  }

  fun showProgress() {
    //progress_layout_billing.visibility = View.VISIBLE
  }

  fun hideProgress() {
    //progress_layout_billing.visibility = View.GONE
  }

  fun refresh(billingFamilyData: ArrayList<BillingFamily>) {
    this.billingFamilies = billingFamilyData
    billingFamilyAdapter.notifyDataSetChanged()
  }
}
