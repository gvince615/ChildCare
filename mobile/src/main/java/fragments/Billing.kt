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


import android.graphics.Color
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
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
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
    setupPieChart(view)
    return view
  }

  private fun setupPieChart(view: View) {

    val chart = view.billingChart

    chart.setUsePercentValues(true)
    chart.description.isEnabled = false
    chart.setExtraOffsets(5f, 10f, 5f, 5f)

    chart.dragDecelerationFrictionCoef = 0.95f

    chart.centerText = "Revenue Graph"

    chart.isDrawHoleEnabled = true
    chart.setHoleColor(Color.WHITE)

    chart.setTransparentCircleColor(Color.WHITE)
    chart.setTransparentCircleAlpha(110)

    chart.holeRadius = 58f
    chart.transparentCircleRadius = 61f

    chart.setDrawCenterText(true)

    chart.rotationAngle = 0f
    // enable rotation of the chart by touch
    chart.isRotationEnabled = true
    chart.isHighlightPerTapEnabled = true

    // mChart.setUnit(" €");
    // mChart.setDrawUnitsInChart(true);

    // add a selection listener

    setData(4, 100f, chart)

    chart.animateY(1400, Easing.EasingOption.EaseInOutQuad)
    // mChart.spin(2000, 0, 360);


    val l = chart.legend
    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    l.orientation = Legend.LegendOrientation.VERTICAL
    l.setDrawInside(false)
    l.xEntrySpace = 7f
    l.yEntrySpace = 0f
    l.yOffset = 0f

    // entry label styling
    chart.setEntryLabelColor(Color.WHITE)
    chart.setEntryLabelTextSize(12f)
  }

  private fun setData(count: Int, range: Float, chart: PieChart) {

    val entries = ArrayList<PieEntry>()

    // NOTE: The order of the entries when being added to the entries array determines their position around the center of
    // the chart.
    for (i in 0 until count) {
      entries.add(PieEntry((Math.random() * range + range / 5).toFloat(), "label", resources.getDrawable(R.drawable.ic_tab_three, null)))
    }

    val dataSet = PieDataSet(entries, "Election Results")

    dataSet.setDrawIcons(false)

    dataSet.sliceSpace = 3f
    dataSet.iconsOffset = MPPointF(0f, 40f)
    dataSet.selectionShift = 5f

    // add a lot of colors

    val colors = ArrayList<Int>()

    for (c in ColorTemplate.VORDIPLOM_COLORS)
      colors.add(c)

    for (c in ColorTemplate.JOYFUL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.COLORFUL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.LIBERTY_COLORS)
      colors.add(c)

    for (c in ColorTemplate.PASTEL_COLORS)
      colors.add(c)

    colors.add(ColorTemplate.getHoloBlue())

    dataSet.colors = colors
    //dataSet.setSelectionShift(0f);

    val data = PieData(dataSet)
    data.setValueFormatter(PercentFormatter())
    data.setValueTextSize(11f)
    data.setValueTextColor(Color.WHITE)
    chart.data = data

    // undo all highlights
    chart.highlightValues(null)

    chart.invalidate()
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
