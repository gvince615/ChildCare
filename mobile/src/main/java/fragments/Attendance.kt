package fragments

import activities.MainActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import attendance.AttenChild
import attendance.AttendanceAdapter
import com.vince.childcare.R
import kotlinx.android.synthetic.main.fragment_attendance.view.*


class Attendance : Fragment() {

  var children: ArrayList<AttenChild> = ArrayList()
  lateinit var rv: RecyclerView
  lateinit var adapter: AttendanceAdapter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    val view: View = inflater.inflate(R.layout.fragment_attendance, container, false)

    setupRecyclerView(view)

    (activity as MainActivity).setFragmentRefreshListener(object : MainActivity.FragmentRefreshListener {
      override fun onRefresh(children: java.util.ArrayList<AttenChild>) {

        refreshData(children)
      }
    })

    return view
  }

  override fun onResume() {
    super.onResume()
  }

  private fun refreshData(children: ArrayList<AttenChild>) {
    this.children = children
    adapter.refreshData(this.children)

  }

  private fun setupRecyclerView(view: View) {
    rv = view.attendance_rv as RecyclerView
    rv.layoutManager = LinearLayoutManager(this.context)
    adapter = AttendanceAdapter(children, this.context!!)
    rv.adapter = adapter
  }
}
