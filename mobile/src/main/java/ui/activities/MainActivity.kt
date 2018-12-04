package ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import com.firebase.ui.auth.AuthUI
import com.vince.childcare.R
import kotlinx.android.synthetic.main.activity_main.*
import ui.fragments.Attendance
import ui.fragments.Billing
import ui.fragments.Dashboard
import java.util.*


class MainActivity : BaseActivity(), Attendance.UpdateBillingListener {
  override fun updateBilling() {
    val billingFragment = fragmentList[2] as Billing
    billingFragment.updateBilling()
  }

  private val fragmentList = Arrays.asList(
      Dashboard(),
      Attendance(),
      Billing())

  private var doubleBackToExitPressedOnce = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    spaceTabLayout.initialize(viewPager, supportFragmentManager,
        fragmentList, savedInstanceState)
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
        .make(coordinator_layout, getString(R.string.double_tap_msg), Snackbar.LENGTH_LONG)
    snackbar.show()
    Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
  }
}
