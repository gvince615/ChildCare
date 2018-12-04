package ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R
import core.AnalyticsUtil
import core.FirestoreUtil
import core.SIGN_IN_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : BaseActivity() {
  private val providers = Arrays.asList(
      AuthUI.IdpConfig.EmailBuilder().build()
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    displayFirebaseAuthLogin()
  }

  private fun displayFirebaseAuthLogin() {

    startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(true)
            .setLogo(R.mipmap.ic_launcher_round)
            .setAvailableProviders(providers)
            .build(),
        SIGN_IN_REQUEST_CODE)
  }

  private fun startMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == SIGN_IN_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {

        AnalyticsUtil(FirebaseAnalytics.getInstance(this))::logLoginEvent
        FirestoreUtil(FirebaseFirestore.getInstance(), this).updateOrAddUser(firebaseAuth.currentUser)

        startMainActivity()
      } else {

        val snackbar = Snackbar
            .make(coordinator_layout, getString(R.string.sign_in_msg), Snackbar.LENGTH_LONG)
            .setAction("RETRY") {
              displayFirebaseAuthLogin()
            }
        snackbar.setActionTextColor(Color.RED)
        val sbView = snackbar.view
        val textView = sbView.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.YELLOW)
        snackbar.duration = Snackbar.LENGTH_INDEFINITE
        snackbar.show()
      }
    }
  }
}
