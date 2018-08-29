package activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.view.View
import attendance.AttenChild
import attendance.AttendanceAdapter
import attendance.AttendancePresenter
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.vince.childcare.R
import core.CHECK_IN
import fragments.Attendance
import fragments.Billing
import fragments.Dashboard
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity(), AttendanceAdapter.CardItemListener {
  private var doubleBackToExitPressedOnce = false
  private val children: ArrayList<AttenChild> = ArrayList()
  private val attendancePresenter = AttendancePresenter()


  fun getFragmentRefreshListener(): FragmentRefreshListener? {
    return fragmentRefreshListener
  }

  fun setFragmentRefreshListener(fragmentRefreshListener: FragmentRefreshListener) {
    this.fragmentRefreshListener = fragmentRefreshListener
  }

  private var fragmentRefreshListener: FragmentRefreshListener? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val fragmentList = Arrays.asList(
        Dashboard(),
        Attendance(),
        Billing())

    spaceTabLayout.initialize(viewPager, supportFragmentManager,
        fragmentList, savedInstanceState)

    attendancePresenter.setUp(this, children)
  }

  override fun onResume() {
    super.onResume()
    updateChildData()
  }

  fun updateChildAttendanceData(attenMap: HashMap<String, Any>, position: Int) {
    children[position].checkInTime = attenMap[CHECK_IN].toString()
    fragmentRefreshListener?.onRefresh(children, position)
  }

  fun updateChildData() {
    attendancePresenter.getChildData(FirebaseAuth.getInstance().currentUser)
  }

  interface FragmentRefreshListener {
    fun onRefresh(children: ArrayList<AttenChild>, position: Int)
    fun childClicked(childRef: String, position: Int)
    fun setProgress(visibleState: Int)
  }

  override fun onBackPressed() {
    if (doubleBackToExitPressedOnce) {
      super.onBackPressed()
      AuthUI.getInstance()
          .signOut(this)
          .addOnCompleteListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
          }
      return
    }

    this.doubleBackToExitPressedOnce = true
    val snackbar = Snackbar
        .make(coordinator_layout, "Press back again to sign out.", Snackbar.LENGTH_LONG)
    snackbar.show()
    Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
  }

  override fun activateChild(childId: String) {

    attendancePresenter.activateChild(childId)
  }

  override fun childClicked(childId: String, position: Int) {
    fragmentRefreshListener?.childClicked(childId, position)
  }

  override fun checkInOutBtnClicked(childId: String, position: Int) {
    attendancePresenter.postAttendance(childId, position)
  }

  fun showProgress() {
    fragmentRefreshListener?.setProgress(View.VISIBLE)
  }

  fun hideProgress() {
    fragmentRefreshListener?.setProgress(View.GONE)
  }
}
