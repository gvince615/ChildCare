package com.vince.childcare.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.MenuItem
import com.vince.childcare.R
import kotlinx.android.synthetic.main.activity_registration.*

class SetupActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_setup)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        // Respond to the action bar's Up/Home button
        onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onBackPressed() {
    super.onBackPressed()
    beginBackTransition()
  }

  private fun beginBackTransition() {
    val colorAnimation = ValueAnimator.ofObject(
        ArgbEvaluator(), resources.getColor(R.color.colorWhite, null), resources.getColor(R.color.colorWhiteTrans, null))
    colorAnimation.duration = 250 // milliseconds
    colorAnimation.addUpdateListener { animator -> content.setBackgroundColor(animator.animatedValue as Int) }
    colorAnimation.start()
  }
}
