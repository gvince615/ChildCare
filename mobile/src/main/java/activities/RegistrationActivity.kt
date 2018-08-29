package activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.vince.childcare.R
import core.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_registration.*
import registration.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class RegistrationActivity : BaseActivity(), RegistrationAdapter.CardItemListener {
  private var imageView: CircleImageView? = null
  private lateinit var adapter: RegistrationAdapter
  private lateinit var registrationPresenter: RegistrationPresenter
  val list: MutableList<RegistrationCardItem<*>> = ArrayList()

  private var isInEditMode: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registration)
    registrationPresenter = RegistrationPresenter()
    registrationPresenter.setUp(this)
    isInEditMode = false
    if (intent.hasExtra(CHILD_ID)) {
      isInEditMode = true
      registrationPresenter.loadChild(intent.getStringExtra(CHILD_ID))
    } else {
      registrationPresenter.getFamilies()
    }

    parent_menu_item.setOnClickListener { parentMenuButtonClicked() }
    pediatrician_menu_item.setOnClickListener { pediatricianMenuButtonClicked() }
    medication_menu_item.setOnClickListener { medicationMenuButtonClicked() }
    billing_menu_item.setOnClickListener { billingMenuButtonClicked() }
  }

  override fun childImageClicked(it: View?) {
    imageView = it as CircleImageView
    val snackbar = Snackbar
        .make(reg_coordinator_layout, "Snap a photo...", Snackbar.LENGTH_LONG)
        .setAction("Open Camera") {
          openCameraIntent()
        }
    snackbar.show()
  }

  private fun openCameraIntent() {

    PermissionUtil.handlePermission(this, ACTION_UPLOAD_PERMISSION, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE)
    if (PermissionUtil.handlePermission(this, ACTION_UPLOAD_PERMISSION, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE)) {
      val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      if (pictureIntent.resolveActivity(packageManager) != null) {
        startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
      if (data != null && data.extras != null) {
        val imageBitmap = data.extras.get("data") as Bitmap
        var rotatedImage = saveImage(imageBitmap)
        imageView?.isDrawingCacheEnabled = true

        imageView?.let {
          Glide.with(this)
              .asBitmap()
              .load(rotatedImage)
              .into(it)
        }
      }
    }
  }

  private fun saveImage(imageBitmap: Bitmap): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(90F)
    return Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.width, imageBitmap.height, matrix, true)
  }

  private fun saveImageToInternalStorage(bitmap: Bitmap?, childId: String): Uri {

    val wrapper = ContextWrapper(applicationContext)
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)

    var card = adapter.getList()[0]

    if (!isInEditMode && (card as RegistrationCardItem<Child>).`object`.childId.isEmpty()) {
      card.`object`.childId = childId
    }

    file = File(file, (list[0] as RegistrationCardItem<Child>).`object`.childId + ".jpg")

    try {
      val stream: OutputStream = FileOutputStream(file)
      bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
      stream.flush()
      stream.close()
    } catch (e: IOException) { // Catch the exception
      e.printStackTrace()
    }
    hideProgress()
    return Uri.parse(file.absolutePath)
  }


  private fun billingMenuButtonClicked() {
    adapter.addBilling(Billing())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun medicationMenuButtonClicked() {
    adapter.addMedication(Medication())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun pediatricianMenuButtonClicked() {
    adapter.addPediatrician(Pediatrician())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun parentMenuButtonClicked() {
    adapter.addParent(Guardian())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun childMenuButtonClicked() {
    adapter.addChild(Child())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  override fun onDeleteCardBtnTapped(adapterPosition: Int) {
    adapter.deleteCard(adapterPosition)
  }

  private fun setUpRecyclerView() {
    adapter = RegistrationAdapter(this, list, this)
    registration_rv.adapter = adapter
    val llm = LinearLayoutManager(applicationContext)
    registration_rv.layoutManager = llm
    registration_rv.itemAnimator = DefaultItemAnimator()

    childMenuButtonClicked()
    parentMenuButtonClicked()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_save -> {
        saveRegistration()
      }

      R.id.menu_delete -> {
        deleteRegistration()
      }

      android.R.id.home -> {
        onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun deleteRegistration() {
    for (card in adapter.getList()) {
      when (card.viewType) {
        RegistrationCardItem.CHILD -> {
          registrationPresenter.deleteChildDataDocument((card as RegistrationCardItem<Child>).`object`.childId)
        }
      }
    }
    onBackPressed()
  }

  private fun saveRegistration() {

    if (validationSuccess()) {
      saveChildCard()
    } else {
      val snackbar = Snackbar
          .make(reg_coordinator_layout, "Unable to save registration with empty fields.", Snackbar.LENGTH_LONG)
      snackbar.show()
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
        return ((card.`object` as Guardian).phoneNumber1.length < 12)
      }
      RegistrationCardItem.CHILD -> {
        return ((card.`object` as Child).birthDate.length < 10)
      }
    }
    return false
  }

  private fun emptyFields(card: RegistrationCardItem<*>): Boolean {
    when (card.viewType) {
      RegistrationCardItem.PARENT -> {
        return ((card.`object` as Guardian).firstName.isEmpty() || (card.`object` as Guardian).lastName.isEmpty()
            || (card.`object` as Guardian).phoneNumber1.isEmpty() || (card.`object` as Guardian).emailAddress.isEmpty())
      }
      RegistrationCardItem.CHILD -> {
        return ((card.`object` as Child).firstName.isEmpty() || (card.`object` as Child).lastName.isEmpty()
            || (card.`object` as Child).birthDate.isEmpty())
      }
    }
    return false
  }

  private lateinit var familyId: String

  private lateinit var childId: String

  private fun saveChildCard() {
    for (card in adapter.getList()) {
      when (card.viewType) {
        RegistrationCardItem.CHILD -> {

          if ((card as RegistrationCardItem<Child>).`object`.childId.isEmpty()) {
            familyId = "ID_" + ((Math.random() * 9899).toInt() + 100).toString()
            childId = familyId + "-" + ((Math.random() * 98999).toInt() + 1000).toString()
          } else {
            var parts = card.`object`.childId.split("-")
            familyId = parts[0]
            childId = card.`object`.childId
          }

          if (imageView != null) {
            registrationPresenter.uploadChildImage(saveImageToInternalStorage(imageView?.drawingCache, childId), firebaseStorage.reference)
          } else {
            onChildImageUploaded("")
          }
        }
      }
    }
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
    menuInflater.inflate(R.menu.registration_menu, menu)
    return true
  }

  fun setDataCards(fullChildRegistrationData: FullChildRegistrationData) {
    if (fullChildRegistrationData.childData.child != null) {
      list.add(RegistrationCardItem(fullChildRegistrationData.childData.child, RegistrationCardItem.CHILD))
    }

    if (fullChildRegistrationData.childData.guardians != null) {
      for ((index, parent) in fullChildRegistrationData.childData.guardians!!.withIndex()) {
        list.add(RegistrationCardItem(parent, RegistrationCardItem.PARENT))
      }
    }

    if (fullChildRegistrationData.childData.pediatrician != null) {
      list.add(RegistrationCardItem(fullChildRegistrationData.childData.pediatrician, RegistrationCardItem.PEDIATRICIAN))
    }

    if (fullChildRegistrationData.childData.medications != null) {
      for (medication in fullChildRegistrationData.childData.medications!!) {
        list.add(RegistrationCardItem(medication, RegistrationCardItem.MEDICATION))
      }
    }

    if (fullChildRegistrationData.childData.billing != null) {
      list.add(RegistrationCardItem(fullChildRegistrationData.childData.billing, RegistrationCardItem.BILLING))
    }

    adapter = RegistrationAdapter(this, list, this)
    registration_rv.adapter = adapter
    val llm = LinearLayoutManager(applicationContext)
    registration_rv.layoutManager = llm
    registration_rv.itemAnimator = DefaultItemAnimator()

    adapter.setUpForEdit()

  }

  fun setDataCardsForAddNewChild(fullChildRegistrationData: FullChildRegistrationData) {
    if (fullChildRegistrationData.childData.child != null) {
      list.add(RegistrationCardItem(fullChildRegistrationData.childData.child, RegistrationCardItem.CHILD))
    }

    if (fullChildRegistrationData.childData.guardians != null) {
      for (guardian in fullChildRegistrationData.childData.guardians!!) {
        list.add(RegistrationCardItem(guardian, RegistrationCardItem.PARENT))
      }
    }

    if (fullChildRegistrationData.childData.pediatrician != null) {
      list.add(RegistrationCardItem(fullChildRegistrationData.childData.pediatrician, RegistrationCardItem.PEDIATRICIAN))
    }

    if (fullChildRegistrationData.childData.medications != null) {
      for (medication in fullChildRegistrationData.childData.medications!!) {
        list.add(RegistrationCardItem(medication, RegistrationCardItem.MEDICATION))
      }
    }

    if (fullChildRegistrationData.childData.billing != null) {
      list.add(RegistrationCardItem(fullChildRegistrationData.childData.billing, RegistrationCardItem.BILLING))
    }

    adapter = RegistrationAdapter(this, list, this)
    registration_rv.adapter = adapter
    val llm = LinearLayoutManager(applicationContext)
    registration_rv.layoutManager = llm
    registration_rv.itemAnimator = DefaultItemAnimator()

    adapter.setUpForEdit()

  }

  fun showProgress() {
    progress_layout_reg.visibility = View.VISIBLE
  }

  fun hideProgress() {
    progress_layout_reg.visibility = View.GONE
  }

  fun onChildImageUploaded(url: String) {
    var parents = ArrayList<Any>()
    var medications = ArrayList<Any>()

    for (card in adapter.getList()) {
      when (card.viewType) {
        RegistrationCardItem.CHILD -> {

          if (url != "") {
            (card as RegistrationCardItem<Child>).`object`.childImageUrl = url
          }

          registrationPresenter.saveNewFamily(FirebaseAuth.getInstance().currentUser, HashMapUtil().createFamilyMap(familyId, "test"))
          registrationPresenter.saveNewChildDataDocument(FirebaseAuth.getInstance().currentUser, familyId, childId,
              HashMapUtil().createChildMap(card as RegistrationCardItem<Child>))
        }

        RegistrationCardItem.PARENT -> {
          parents.add(HashMapUtil().createParentMap(card as RegistrationCardItem<Guardian>))
        }

        RegistrationCardItem.PEDIATRICIAN -> {
          registrationPresenter.saveNewPediatricianDataDocument(FirebaseAuth.getInstance().currentUser, familyId,
              HashMapUtil().createPediatricianMap(card as RegistrationCardItem<Pediatrician>))
        }

        RegistrationCardItem.MEDICATION -> {
          medications.add(HashMapUtil().createMedicationMap(card as RegistrationCardItem<Medication>))
        }

        RegistrationCardItem.BILLING -> {
          registrationPresenter.saveNewBillingDataDocument(FirebaseAuth.getInstance().currentUser, familyId, childId,
              HashMapUtil().createBillingMap(card as RegistrationCardItem<Billing>))
        }
      }

      if (parents.isNotEmpty()) {
        registrationPresenter.saveNewGuardianDataDocument(FirebaseAuth.getInstance().currentUser, familyId, parents)
      }
      if (medications.isNotEmpty()) {
        registrationPresenter.saveNewMedicationDataDocument(FirebaseAuth.getInstance().currentUser, familyId, childId, medications)
      }
      finish()
    }
  }

  fun onDeleteChildSuccess() {

  }

  private var addToFamily: Boolean = false

  fun onFamilyNamesRetrieved(familyNames: Array<String?>) {
    var d = AlertDialog.Builder(this)
    d.setTitle("New Registration")
    d.setMessage("Add the new child to an EXISTING family or a NEW family")
    d.setPositiveButton("Existing") { dialog, which ->
      val builder = AlertDialog.Builder(this@RegistrationActivity)
      builder.setTitle("Select Family")
      builder.setItems(familyNames) { dialog, item ->
        addToFamily = true
        familyNames[item]?.let { registrationPresenter.getFamilyData(it) }
        dialog.dismiss()
      }
      builder.setNeutralButton("Go Back") { dialog, which ->
        // go back
        d.show()
      }.show()
    }
    d.setNeutralButton("New") { dialog, which ->
      // add blank child card .. continue
      // generate new family ID on save of new child
      addToFamily = false
      setUpRecyclerView()
    }
    d.setNegativeButton("Cancel") { dialog, which ->
      // finish Activity
      onBackPressed()
    }
    d.show()
  }

  fun onNoFamilyNamesRetrieved() {
    addToFamily = false
    setUpRecyclerView()
  }
}
