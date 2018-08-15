package fragments

import activities.MainActivity
import activities.RegistrationActivity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import attendance.AttenChild
import attendance.AttendanceAdapter
import com.vince.childcare.R
import core.CHILD_TO_LOAD
import kotlinx.android.synthetic.main.fragment_attendance.*
import kotlinx.android.synthetic.main.fragment_attendance.view.*

class Attendance : Fragment() {

  var children: ArrayList<AttenChild> = ArrayList()
  lateinit var rv: RecyclerView
  private lateinit var adapter: AttendanceAdapter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    val view: View = inflater.inflate(R.layout.fragment_attendance, container, false)
    setupRecyclerView(view)

    setRefreshListener()
    return view
  }

  private fun setRefreshListener() {
    (activity as MainActivity).setFragmentRefreshListener(object : MainActivity.FragmentRefreshListener {
      override fun setProgress(visibleState: Int) {
        setProgressVisibility(visibleState)
      }

      override fun editChildClicked(childRef: String, position: Int) {
        val intent = Intent(activity, RegistrationActivity::class.java).putExtra(CHILD_TO_LOAD, childRef)
        val options = activity?.let {
          ActivityOptionsCompat.makeSceneTransitionAnimation(it,
              rv.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.child_image),
              ViewCompat.getTransitionName(rv.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.child_image)))
        }
        startActivity(intent, options?.toBundle())
      }

      override fun onRefresh(children: java.util.ArrayList<AttenChild>, position: Int) {
        refreshData(children, position)
      }
    })
  }

  private fun setProgressVisibility(visibleState: Int) {
    progress_layout_atten.visibility = visibleState
  }

  private fun refreshData(children: ArrayList<AttenChild>, position: Int) {
    this.children = children
    adapter.refreshData(this.children, position)
  }

  private fun setupRecyclerView(view: View) {
    rv = view.attendance_rv as RecyclerView
    rv.layoutManager = LinearLayoutManager(this.context)
    adapter = AttendanceAdapter(this.context!!, children, (activity as MainActivity))
    rv.adapter = adapter
  }
}
