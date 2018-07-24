package com.vince.childcare.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R
import com.vince.childcare.core.FirestoreUtil
import com.vince.childcare.core.registration.Child
import com.vince.childcare.core.registration.Parent
import com.vince.childcare.core.registration.RegistrationAdapter
import com.vince.childcare.core.registration.RegistrationCardItem
import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : BaseActivity(), RegistrationAdapter.CardItemListener {


  private lateinit var adapter: RegistrationAdapter
  val list: MutableList<RegistrationCardItem<*>> = ArrayList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registration)

    child_menu_item.setOnClickListener { childMenuButtonClicked() }
    parent_menu_item.setOnClickListener { parentMenuButtonClicked() }
    medical_menu_item.setOnClickListener { medicalMenuButtonClicked() }
    billing_menu_item.setOnClickListener { billingMenuButtonClicked() }

    setUpRecyclerView()
  }

  private fun billingMenuButtonClicked() {
//    cards.add(BillingData())
//    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun medicalMenuButtonClicked() {
//    cards.add(MedicalData())
//    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun parentMenuButtonClicked() {
    adapter.addParent(Parent())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun childMenuButtonClicked() {
    adapter.addChild(Child())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }


  private fun setUpRecyclerView() {
    adapter = RegistrationAdapter(this, list, this)
    registration_rv.adapter = adapter
    val llm = LinearLayoutManager(applicationContext)
    registration_rv.layoutManager = llm
    registration_rv.itemAnimator = DefaultItemAnimator()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_save -> {
        Toast.makeText(this, "save tapped ", Toast.LENGTH_SHORT).show()
        saveRegistration()
      }

      android.R.id.home -> {
        onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }


  private fun saveRegistration() {

    var childCard: RegistrationCardItem<Child>? = saveAndGetChildCard()

    for (card in adapter.getList()) {
      when (card.viewType) {
        RegistrationCardItem.PARENT -> {
          // todo - save parent stuff??
        }
      }
    }
  }

  private fun saveAndGetChildCard(): RegistrationCardItem<Child>? {
    for ((pos, card) in adapter.getList().withIndex()) {
      when (card.viewType) {
        RegistrationCardItem.CHILD -> {

          val firstname = registration_rv.getChildAt(pos).findViewById<TextInputLayout>(R.id.child_first_name).editText?.text.toString()



          FirestoreUtil(FirebaseFirestore.getInstance(), this)
              .saveChildDataDocument(FirebaseAuth.getInstance().currentUser, card as RegistrationCardItem<Child>)

          Log.d("CHILD", card.`object`.firstName + " - " + card.`object`.lastName)
          return card
        }
      }
    }
    return null
  }

  override fun onBackPressed() {
    super.onBackPressed()
    beginBackTransition()
  }

  private fun beginBackTransition() {
    registration_rv.visibility = View.GONE
    val colorAnimation = ValueAnimator.ofObject(
        ArgbEvaluator(), resources.getColor(R.color.colorWhite, null), resources.getColor(R.color.colorWhiteTrans, null))
    colorAnimation.duration = 250 // milliseconds
    colorAnimation.addUpdateListener { animator -> content.setBackgroundColor(animator.animatedValue as Int) }
    colorAnimation.start()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.registration_menu, menu)
    return true
  }

  override fun onParentCardClicked(message: String) {
    showToast("Parent:$message")
  }

  override fun onChildCardClicked(message: String) {
    showToast("Parent:$message")
  }

  fun showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
}
