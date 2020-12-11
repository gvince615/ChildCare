package activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


open class BaseActivity : AppCompatActivity() {

  lateinit var firebaseAnalytics: FirebaseAnalytics
  lateinit var firebaseAuth: FirebaseAuth
  lateinit var firebaseStorage: FirebaseStorage

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseApp.initializeApp(this)

    firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    firebaseAuth = FirebaseAuth.getInstance()
    firebaseStorage = FirebaseStorage.getInstance()

  }
}
