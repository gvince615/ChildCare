package fragments

import activities.MessageBoardActivity
import activities.RegistrationActivity
import activities.SetupActivity
import activities.TodoActivity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseAuth
import com.vince.childcare.R
import dashboard.DashboardPresenter
import dashboard.Family
import dashboard.NumberFormatter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*


class Dashboard : Fragment() {
  private val dashboardPresenter = DashboardPresenter()
  private lateinit var dashboardData: ArrayList<Family>
  private var chart: PieChart? = null
  private lateinit var progressView: RelativeLayout

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment

    val view: View = inflater.inflate(R.layout.fragment_dashboard, container,
        false)

    chart = view.dashboard_graph
    progressView = view.progress_layout_dash
    view.registration_cardview.setOnClickListener { startRegistrationActivity() }
    view.msg_board_cardview.setOnClickListener { startMessageBoardActivity() }
    view.todo_cardview.setOnClickListener { startTodoActivity() }
    view.setup_cardview.setOnClickListener { startSetupActivity() }


    dashboardPresenter.setUp(this)
    dashboardPresenter.getDashboardFragmentData(FirebaseAuth.getInstance().currentUser)
    return view
  }

  private fun setData(families: ArrayList<Family>, chart: PieChart) {
    val entries = ArrayList<PieEntry>()
    for (family in families) {
      entries.add(PieEntry(family.children.toFloat(), family.familyName))
    }

    val dataSet = PieDataSet(entries, "")

    dataSet.sliceSpace = 3f
    dataSet.iconsOffset = MPPointF(0f, 40f)
    dataSet.selectionShift = 5f

    dataSet.colors = addColors()

    val data = PieData(dataSet)
    data.setValueFormatter(NumberFormatter())
    data.setValueTextSize(11f)
    data.setValueTextColor(Color.BLACK)
    chart.data = data

    // undo all highlights
    chart.highlightValues(null)

    chart.invalidate()
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

  private fun setupPieChart() {

    chart?.dragDecelerationFrictionCoef = 0.95f
    chart?.setUsePercentValues(false)
    chart?.centerText = "Registration\nBy Family"

    chart?.isDrawHoleEnabled = true
    chart?.setHoleColor(Color.TRANSPARENT)

    chart?.holeRadius = 58f
    chart?.transparentCircleRadius = 61f

    chart?.setDrawCenterText(true)
    chart?.rotationAngle = 0f
    chart?.isRotationEnabled = true

    chart?.let { setData(dashboardData, it) }

    chart?.animateY(1400, Easing.EasingOption.EaseInOutQuad)

    // entry label styling
    chart?.setEntryLabelColor(Color.BLACK)
    chart?.setEntryLabelTextSize(12f)

    chart?.description?.isEnabled = false

    val l = chart?.legend
    l?.isEnabled = false
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

  fun showProgress() {
    progressView.visibility = View.VISIBLE
  }

  fun hideProgress() {
    progressView.visibility = View.GONE
  }

  fun setDashboardData(dashboardData: ArrayList<Family>) {
    this.dashboardData = dashboardData
    setupPieChart()
  }

}
