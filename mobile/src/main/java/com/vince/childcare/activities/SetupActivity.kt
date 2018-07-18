package com.vince.childcare.activities

import android.os.Bundle
import android.view.MenuItem
import com.vince.childcare.R

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
}
