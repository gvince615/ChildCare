package activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.vince.childcare.R
import kotlinx.android.synthetic.main.activity_registration.*


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

  fun beginBackTransition() {
    val colorAnimation = ValueAnimator.ofObject(
        ArgbEvaluator(), resources.getColor(R.color.colorWhite, null), resources.getColor(R.color.colorWhiteTrans, null))
    colorAnimation.duration = 250 // milliseconds
    colorAnimation.addUpdateListener { animator -> content.setBackgroundColor(animator.animatedValue as Int) }
    colorAnimation.start()
  }
}
