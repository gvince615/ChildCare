package activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R
import core.FirestoreUtil
import core.HashMapUtil
import kotlinx.android.synthetic.main.activity_registration.*
import registration.*


class RegistrationActivity : BaseActivity(), RegistrationAdapter.CardItemListener {


  private lateinit var adapter: RegistrationAdapter
  private lateinit var registrationPresenter: RegistrationPresenter

  val list: MutableList<RegistrationCardItem<*>> = ArrayList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registration)



    if (intent.hasExtra("childToLoad")) {
      var childToLoad = intent.getStringExtra("childToLoad")
      // todo - do stuff to load all child data
      registrationPresenter = RegistrationPresenter()
      registrationPresenter.setUp(this, childToLoad)
      registrationPresenter.loadChild()
    } else {
      setUpRecyclerView()
    }

    child_menu_item.setOnClickListener { childMenuButtonClicked() }
    parent_menu_item.setOnClickListener { parentMenuButtonClicked() }
    medical_menu_item.setOnClickListener { medicalMenuButtonClicked() }
    billing_menu_item.setOnClickListener { billingMenuButtonClicked() }

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
    adapter.addParent(Parent("", "", "", "", ""))
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun childMenuButtonClicked() {
    adapter.addChild(Child("", "", "", "", "", "", "", "", ""))
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
        Toast.makeText(this, "Registration saved ", Toast.LENGTH_SHORT).show()
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

    if (validationSuccess()) {

      val childCard: HashMap<String, Any>? = saveAndGetChildCard()

      for (card in adapter.getList()) {
        when (card.viewType) {
          RegistrationCardItem.PARENT -> {

            FirestoreUtil(FirebaseFirestore.getInstance(), this)
                .saveParentDataDocument(FirebaseAuth.getInstance().currentUser, HashMapUtil().createParentMap(card as RegistrationCardItem<Parent>),
                    childCard)
          }
        }
      }
      finish()
    }
  }

  private fun validationSuccess(): Boolean {
    for (card in adapter.getList()) {
      if (emptyFields(card)) {
        return false
      } else if (lessThanMinimum(card)) {
        return false
      }
    }
    return true
  }

  private fun lessThanMinimum(card: RegistrationCardItem<*>): Boolean {
    when (card.viewType) {
      RegistrationCardItem.PARENT -> {
        return ((card.`object` as Parent).firstName.length < 2 || (card.`object` as Parent).lastName.length < 2 || (card.`object` as Parent).phoneNumber1.length < 12)
      }
      RegistrationCardItem.CHILD -> {
        return ((card.`object` as Child).firstName.length < 2 || (card.`object` as Child).lastName.length < 2 || (card.`object` as Child).birthDate.length < 10)
      }
    }
    return false
  }

  private fun emptyFields(card: RegistrationCardItem<*>): Boolean {
    when (card.viewType) {
      RegistrationCardItem.PARENT -> {
        return ((card.`object` as Parent).firstName.isEmpty() || (card.`object` as Parent).lastName.isEmpty() || (card.`object` as Parent).phoneNumber1.isEmpty())
      }
      RegistrationCardItem.CHILD -> {
        return ((card.`object` as Child).firstName.isEmpty() || (card.`object` as Child).lastName.isEmpty() || (card.`object` as Child).birthDate.isEmpty())
      }
    }
    return false
  }

  private fun saveAndGetChildCard(): HashMap<String, Any>? {
    for (card in adapter.getList()) {
      when (card.viewType) {
        RegistrationCardItem.CHILD -> {

          FirestoreUtil(FirebaseFirestore.getInstance(), this)
              .saveChildDataDocument(FirebaseAuth.getInstance().currentUser, HashMapUtil().createChildMap(card as RegistrationCardItem<Child>))

          return HashMapUtil().createChildMap(card)
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

  fun setDataCards(fullChildRegistrationData: FullChildRegistrationData) {


    list.add(RegistrationCardItem(fullChildRegistrationData.child, RegistrationCardItem.CHILD))

    for (parent in fullChildRegistrationData.parents) {
      list.add(RegistrationCardItem(parent, RegistrationCardItem.PARENT))
    }

    adapter = RegistrationAdapter(this, list, this)
    registration_rv.adapter = adapter
    val llm = LinearLayoutManager(applicationContext)
    registration_rv.layoutManager = llm
    registration_rv.itemAnimator = DefaultItemAnimator()

    adapter.setUpForEdit()

  }

  fun showProgress() {
    progress_layout.visibility = View.VISIBLE
  }

  fun hideProgress() {
    progress_layout.visibility = View.GONE
  }
}
