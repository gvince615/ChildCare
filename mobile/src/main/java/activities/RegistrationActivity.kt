package activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R
import core.*
import kotlinx.android.synthetic.main.activity_registration.*
import registration.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class RegistrationActivity : BaseActivity(), RegistrationAdapter.CardItemListener {
  private lateinit var imageView: ImageView
  private lateinit var adapter: RegistrationAdapter
  private lateinit var registrationPresenter: RegistrationPresenter
  val list: MutableList<RegistrationCardItem<*>> = ArrayList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registration)

    if (intent.hasExtra("childToLoad")) {
      var childToLoad = intent.getStringExtra("childToLoad")
      registrationPresenter = RegistrationPresenter()
      registrationPresenter.setUp(this, childToLoad)
      registrationPresenter.loadChild()
    } else {
      setUpRecyclerView()
    }

    parent_menu_item.setOnClickListener { parentMenuButtonClicked() }
    medical_menu_item.setOnClickListener { medicalMenuButtonClicked() }
    billing_menu_item.setOnClickListener { billingMenuButtonClicked() }
  }

  override fun childImageClicked(it: View?) {
    this.imageView = it as ImageView
    val customSnackbar = CustomSnackbar.make(reg_coordinator_layout, 500)

    customSnackbar.setText("Choose Image Source")
    customSnackbar.setAction1("Take Picture", View.OnClickListener {
      openCameraIntent()
    })
    customSnackbar.setAction2("Select Picture", View.OnClickListener {
    })

    customSnackbar.show()
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
        imageView.setImageBitmap(imageBitmap)
        (list[0].`object` as Child).childImageUri = saveImageToInternalStorage(imageBitmap).toString()
      }
    }
  }

  private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

    val wrapper = ContextWrapper(applicationContext)
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)

    file = File(file, "${UUID.randomUUID()}.jpg")

    try {
      val stream: OutputStream = FileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
      stream.flush()
      stream.close()
    } catch (e: IOException) { // Catch the exception
      e.printStackTrace()
    }
    hideProgress()
    return Uri.parse(file.absolutePath)
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
    adapter.addChild(Child("", "", "", "Active", "", "", "", "", ""))
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
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_save -> {
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

  private fun getOutputMediaFileUri(type: Int): Uri? {
    return Uri.fromFile(getOutputMediaFile(type))
  }

  private fun getOutputMediaFile(type: Int): File? {

    // Check that the SDCard is mounted
    val mediaStorageDir = File(
        Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES)
    // Create the storage directory(MyCameraVideo) if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.e("Item Attachment",
            "Failed to create directory MyCameraVideo.")
        return null
      }
    }

    val mediaFile: File

    if (type == 1) {
      mediaFile = File(mediaStorageDir.path + File.separator + ".jpg")
    } else {
      return null
    }

    return mediaFile
  }

  fun showProgress() {
    progress_layout_reg.visibility = View.VISIBLE
  }

  fun hideProgress() {
    progress_layout_reg.visibility = View.GONE
  }
}
