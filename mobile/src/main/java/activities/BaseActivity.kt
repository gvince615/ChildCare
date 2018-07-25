package activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth


open class BaseActivity : AppCompatActivity() {

  private lateinit var firebaseAnalytics: FirebaseAnalytics
  private lateinit var firebaseAuth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseApp.initializeApp(this)

    firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    firebaseAuth = FirebaseAuth.getInstance()
  }
}
