package activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.SimpleAdapter
import attendance.AttenChild
import attendance.AttendanceAdapter
import attendance.AttendancePresenter
import attendance.AttendanceRecord
import billing.BillingChildAdapter
import billing.BillingFamily
import billing.BillingFamilyAdapter
import billing.BillingPresenter
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.vince.childcare.R
import core.BottomSheetListView
import core.CHECK_IN
import fragments.Attendance
import fragments.Billing
import fragments.Dashboard
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), AttendanceAdapter.CardItemListener, BillingFamilyAdapter.BillingCardItemListener, BillingChildAdapter.BillingChildClickListener {


  private var doubleBackToExitPressedOnce = false
  private val children: ArrayList<AttenChild> = ArrayList()
  private val billingFamilies: ArrayList<BillingFamily> = ArrayList()

  private val attendancePresenter = AttendancePresenter()
  private val billingPresenter = BillingPresenter()

  override fun generateBillClicked(position: Int) {

  }

  override fun billingChildClicked(childName: String, childId: String, position: Int) {
    //billingPresenter.getBillingAttendanceRecords(childId)
  }

  fun getFragmentRefreshListener(): AttendanceFragmentRefreshListener? {
    return attendanceFragmentRefreshListener
  }

  fun setFragmentRefreshListener(attendanceFragmentRefreshListener: AttendanceFragmentRefreshListener) {
    this.attendanceFragmentRefreshListener = attendanceFragmentRefreshListener
  }

  fun getBillingFragmentRefreshListener(): BillingFragmentRefreshListener? {
    return billingFragmentRefreshListener
  }

  fun setBillingFragmentRefreshListener(billingFragmentRefreshListener: BillingFragmentRefreshListener) {
    this.billingFragmentRefreshListener = billingFragmentRefreshListener
  }

  private var billingFragmentRefreshListener: BillingFragmentRefreshListener? = null
  private var attendanceFragmentRefreshListener: AttendanceFragmentRefreshListener? = null


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
    billingPresenter.setUp(this, billingFamilies)
  }

  override fun onResume() {
    super.onResume()
    updateAttendanceData()
    updateBillingData()
  }

  private fun updateBillingData() {
    billingPresenter.getBillingFragmentData(FirebaseAuth.getInstance().currentUser)
  }

  fun updateChildAttendanceData(attenMap: HashMap<String, Any>, position: Int) {
    children[position].checkInTime = attenMap[CHECK_IN].toString()
    attendanceFragmentRefreshListener?.onRefresh(children, position)
  }

  fun updateAttendanceData() {
    attendancePresenter.getChildData(FirebaseAuth.getInstance().currentUser)
  }

  interface AttendanceFragmentRefreshListener {
    fun onRefresh(children: ArrayList<AttenChild>, position: Int)
    fun childClicked(childRef: String, position: Int)
    fun setProgress(visibleState: Int)
  }

  interface BillingFragmentRefreshListener {
    fun onRefresh(billingFamilies: ArrayList<BillingFamily>, position: Int)
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
    attendanceFragmentRefreshListener?.childClicked(childId, position)
  }

  override fun checkInOutBtnClicked(childId: String, position: Int) {
    attendancePresenter.postAttendance(childId, position)
  }

  fun showProgress() {
    attendanceFragmentRefreshListener?.setProgress(View.VISIBLE)
  }

  fun hideProgress() {
    attendanceFragmentRefreshListener?.setProgress(View.GONE)
  }

  fun onGetBillingAttendanceRecordsSuccess(childId: String, records: ArrayList<AttendanceRecord>) {
    val dialog = BottomSheetDialog(this.applicationContext)
    dialog.setContentView(R.layout.layout_records_bottom_sheet)

    val attenRecordsLv = dialog.findViewById(R.id.lv_atten_records) as BottomSheetListView?
    val adapter = SimpleAdapter(this, prepairData(records), android.R.layout.simple_list_item_1, arrayOf("AAA"), intArrayOf(android.R.id.text1))

    attenRecordsLv?.adapter = adapter

    dialog.show()


  }

  private fun prepairData(records: ArrayList<AttendanceRecord>): ArrayList<Map<String, Any>> {
    for (record in records) {

    }
    var data = ArrayList<Map<String, Any>>()
    var item: MutableMap<String, Any>
    item = HashMap()
    item["1"] = "A"
    item["2"] = "B"
    data.add(item)
    item = HashMap()
    item["3"] = "C"
    item["4"] = "D"
    data.add(item)
    item = HashMap()
    item["5"] = "E"
    item["6"] = "F"
    data.add(item)
    return data
  }
}
