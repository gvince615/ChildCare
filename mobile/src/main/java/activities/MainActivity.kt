package activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
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

  private fun retrieveChildDataCollection(firebaseUser: FirebaseUser?) {
    children.clear()

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA)
        .get()
        .addOnCompleteListener { task ->

          if (task.isSuccessful) {
            children.clear()

            for (document in task.result) {
              val child = AttenChild()
              child.first_name = document[FIRST_NAME].toString()
              child.last_name = document[LAST_NAME].toString()
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

  override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
    super.onSaveInstanceState(outState, outPersistentState)
    spaceTabLayout.saveState(outState)
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
