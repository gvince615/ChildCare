package fragments

import activities.MessageBoardActivity
import activities.RegistrationActivity
import activities.SetupActivity
import activities.TodoActivity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.vince.childcare.R
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*


class Dashboard : Fragment() {


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment

    val view: View = inflater.inflate(R.layout.fragment_dashboard, container,
        false)

    view.registration_cardview.setOnClickListener { startRegistrationActivity() }
    view.msg_board_cardview.setOnClickListener { startMessageBoardActivity() }
    view.todo_cardview.setOnClickListener { startTodoActivity() }
    view.setup_cardview.setOnClickListener { startSetupActivity() }

    setupBarGraph(view)

    return view
  }

  private fun setupBarGraph(view: View) {

    view.dashboard_bar_graph.setDrawBarShadow(false)
    view.dashboard_bar_graph.setDrawValueAboveBar(true)

    val xAxis = view.dashboard_bar_graph.xAxis
    xAxis.position = XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.granularity = 1f // only intervals of 1 day
    xAxis.labelCount = 7


    val leftAxis = view.dashboard_bar_graph.axisLeft
    leftAxis.setLabelCount(8, false)
    leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART)
    leftAxis.spaceTop = 15f
    leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

    val rightAxis = view.dashboard_bar_graph.axisRight
    rightAxis.setDrawGridLines(false)
    rightAxis.setLabelCount(8, false)
    rightAxis.spaceTop = 15f
    rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

    setData(4, 10f, view.dashboard_bar_graph)

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
      set1.colors = colors

      val dataSets = ArrayList<IBarDataSet>()
      dataSets.add(set1)

      val data = BarData(dataSets)
      data.setValueTextSize(10f)
      data.barWidth = 0.9f

      dashboard_bar_graph.data = data
    }
  }

  private fun startMessageBoardActivity() {
    val intent = Intent(activity, MessageBoardActivity::class.java)
    val options = activity?.let {
      ActivityOptionsCompat.makeSceneTransitionAnimation(it, msg_board_view,
          ViewCompat.getTransitionName(msg_board_view)!!)
    }
    startActivity(intent, options?.toBundle())
  }

  private fun startTodoActivity() {
    val intent = Intent(activity, TodoActivity::class.java)
    val options = activity?.let {
      ActivityOptionsCompat.makeSceneTransitionAnimation(it,
          todo_view,
          ViewCompat.getTransitionName(todo_view)!!)
    }
    startActivity(intent, options?.toBundle())
  }

  private fun startSetupActivity() {
    val intent = Intent(activity, SetupActivity::class.java)
    val options = activity?.let {
      ActivityOptionsCompat.makeSceneTransitionAnimation(it,
          setup_view,
          ViewCompat.getTransitionName(setup_view)!!)
    }
    startActivity(intent, options?.toBundle())
  }

  private fun startRegistrationActivity() {
    val intent = Intent(activity, RegistrationActivity::class.java)
    val options = activity?.let {
      ActivityOptionsCompat.makeSceneTransitionAnimation(it,
          registration_view,
          ViewCompat.getTransitionName(registration_view)!!)
    }
    startActivity(intent, options?.toBundle())
  }

}
