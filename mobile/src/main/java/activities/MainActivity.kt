package activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.util.Log
import attendance.AttenChild
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R
import core.*
import fragments.Attendance
import fragments.Billing
import fragments.Dashboard
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity() {
  private var doubleBackToExitPressedOnce = false
  val children: ArrayList<AttenChild> = ArrayList()

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
  }

  override fun onPostResume() {
    super.onPostResume()
    retrieveChildDataCollection(FirebaseAuth.getInstance().currentUser)
  }


  fun retrieveChildDataCollection(firebaseUser: FirebaseUser?) {
    children.clear()

    var d = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA)
        d.get()
        .addOnCompleteListener { task ->

          if (task.isSuccessful) {
            children.clear()

            for (document in task.result) {
              val child = AttenChild("", "", "", "")
              child.firstName = document[FIRST_NAME].toString()
              child.lastName = document[LAST_NAME].toString()
              child.birthDate = document[BIRTH_DATE].toString()
              children.add(child)
              Log.d(this.packageName.toString(), document.id + " => " + document.data)
            }

            if (getFragmentRefreshListener() != null) {
              getFragmentRefreshListener()?.onRefresh(children)
            }

          } else {
            Log.d(this.packageName.toString(), "Error getting documents: ", task.exception)
          }
        }
  }

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

  fun getChildObject(childDocId: String) {
    retrieveChildDataCollection(FirebaseAuth.getInstance().currentUser)
  }
}
