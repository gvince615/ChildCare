package fragments

import activities.RegistrationActivity
import android.content.Context
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
import attendance.AttendancePresenter
import com.google.firebase.auth.FirebaseAuth
import com.vince.childcare.R
import core.CHECK_IN
import core.CHILD_ID
import kotlinx.android.synthetic.main.fragment_attendance.view.*
import java.util.HashMap
import kotlin.collections.ArrayList

class Attendance : Fragment(), AttendanceAdapter.CardItemListener {
  interface UpdateBillingListener {
    fun updateBilling()
  }

  var children: ArrayList<AttenChild> = ArrayList()
  lateinit var rv: RecyclerView
  private lateinit var adapter: AttendanceAdapter
  private val attendancePresenter = AttendancePresenter()


  private lateinit var updateBillingListener: UpdateBillingListener

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    try {
      updateBillingListener = context as UpdateBillingListener
    } catch (e: ClassCastException) {
      throw ClassCastException(context.toString() + " must implement UpdateBillingListener")
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    val view: View = inflater.inflate(R.layout.fragment_attendance, container, false)
    setupRecyclerView(view)

    attendancePresenter.setUp(this, children)
    return view
  }

  private fun setupRecyclerView(view: View) {
    rv = view.attendance_rv as RecyclerView
    rv.layoutManager = LinearLayoutManager(this.context)
    adapter = AttendanceAdapter(this.context!!, children, this)
    rv.adapter = adapter
  }

  fun refreshData(children: ArrayList<AttenChild>, position: Int) {
    this.children = children
    adapter.refreshData(this.children, position)
  }

  fun updateChildAttendanceData(attenMap: HashMap<String, Any>, position: Int) {
    children[position].checkInTime = attenMap[CHECK_IN].toString()
    refreshData(children, position)
    updateBillingListener.updateBilling()
  }

  fun showProgress() {
    //progress_layout_atten.visibility = View.VISIBLE
  }

  fun hideProgress() {
    //progress_layout_atten.visibility = View.GONE
  }

  override fun childClicked(childId: String, position: Int) {
    val intent = Intent(activity, RegistrationActivity::class.java).putExtra(CHILD_ID, childId)
    val options = activity?.let {
      ActivityOptionsCompat.makeSceneTransitionAnimation(it,
          rv.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.child_image),
          ViewCompat.getTransitionName(rv.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.child_image)))
    }
    startActivity(intent, options?.toBundle())
  }

  override fun onResume() {
    super.onResume()
    attendancePresenter.getChildData(FirebaseAuth.getInstance().currentUser)
  }

  override fun checkInOutBtnClicked(childId: String, position: Int) {
    attendancePresenter.postAttendance(childId, position)
  }

  override fun activateChild(childId: String) {
    attendancePresenter.activateChild(childId)
  }
}
