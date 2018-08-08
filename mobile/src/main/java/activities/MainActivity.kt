package activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import attendance.AttenChild
import attendance.AttendanceAdapter
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.vince.childcare.R
import fragments.Attendance
import fragments.Billing
import fragments.Dashboard
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity(), AttendanceAdapter.CardItemListener {
  override fun onChildCardClicked(childRef: String) {
    attendancePresenter.postAttendance(childRef)
  }

  override fun onChildCardLongClicked(childRef: String) {
    Snackbar.make(
        this.coordinator_layout, // Parent view
        "LONG CLICKED", // Message to show
        Snackbar.LENGTH_SHORT // How long to display the message.
    ).show()
  }

  private var doubleBackToExitPressedOnce = false
  val children: ArrayList<AttenChild> = ArrayList()
  val attendancePresenter = AttendancePresenter()


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

  fun updateChildData() {
    attendancePresenter.getChildData(FirebaseAuth.getInstance().currentUser)

//    children.clear()
//
//    var d = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + firebaseUser?.uid)
//        .collection(COLLECTION_REGISTRATION_DATA)
//    d.get()
//        .addOnCompleteListener { task ->
//
//          if (task.isSuccessful) {
//            children.clear()
//
//            for (document in task.result) {
//              val child = AttenChild("", "", "", "", "", "")
//              child.firstName = document[FIRST_NAME].toString()
//              child.lastName = document[LAST_NAME].toString()
//              child.birthDate = document[BIRTH_DATE].toString()
//              child.isActive = document[IS_ACTIVE].toString()
//
//              Log.d("Firestore", document.id + " => " + document.data)
//
//              children.add(child)
//              getLatestAttendanceData(child)
//            }
//
//            if (getFragmentRefreshListener() != null) {
//              getFragmentRefreshListener()?.onRefresh(children)
//            }
//
//          } else {
//            Log.d("Firestore", "Error getting documents: ", task.exception)
//          }
//        }
  }

//  private fun getLatestAttendanceData(child: AttenChild) {
//    var checkIn = ""
//    var checkOut = ""
//
//    var d = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
//        .collection(COLLECTION_REGISTRATION_DATA).document(child.lastName + "_" + child.firstName).collection(COLLECTION_ATTENDANCE_DATA)
//    d.orderBy("timestamp", Query.Direction.DESCENDING).limit(1).get()
//        .addOnCompleteListener { task ->
//
//          if (task.isSuccessful) {
//
//            for (doc in task.result.documents) {
//              if (doc.contains("checkIn")) {
//                checkIn = doc["checkIn"].toString()
//
//              }
//              if (doc.contains("checkOut")) {
//                checkOut = doc["checkOut"].toString()
//              }
//
//              if (checkIn != "" && checkOut == "") {
//                child.checkInTime = checkIn
//              }
//
//              Log.d("Firestore", doc.id + " => " + checkIn + "::" + checkOut)
//              break
//            }
//
//            if (getFragmentRefreshListener() != null) {
//              getFragmentRefreshListener()?.onRefresh(children)
//            }
//
//          } else {
//            Log.d("Firestore", "Error getting documents: ", task.exception)
//          }
//        }
//  }

  interface FragmentRefreshListener {
    fun onRefresh(children: ArrayList<AttenChild>)
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
}
