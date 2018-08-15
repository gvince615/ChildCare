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

  override fun editChildClicked(childRef: String, position: Int) {

    fragmentRefreshListener?.editChildClicked(childRef, position)
  }

  override fun checkInOutBtnClicked(childRef: String, position: Int, view: View) {
    attendancePresenter.postAttendance(childRef, position, view)
  }

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

  override fun onPostResume() {
    super.onPostResume()
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
    fun editChildClicked(childRef: String, position: Int)
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

  fun showProgress() {
    fragmentRefreshListener?.setProgress(View.VISIBLE)
  }

  fun hideProgress() {
    fragmentRefreshListener?.setProgress(View.GONE)
  }
}
