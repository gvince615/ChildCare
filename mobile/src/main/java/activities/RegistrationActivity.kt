package activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
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
import android.widget.ImageView
import com.bumptech.glide.Glide
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

    registrationPresenter = RegistrationPresenter()

    if (intent.hasExtra("childToLoad")) {
      var childToLoad = intent.getStringExtra("childToLoad")
      registrationPresenter.setUp(this, childToLoad)
      registrationPresenter.loadChild()
    } else {
      setUpRecyclerView()
      registrationPresenter.setUp(this)
    }


    parent_menu_item.setOnClickListener { parentMenuButtonClicked() }
    medical_menu_item.setOnClickListener { medicalMenuButtonClicked() }
    billing_menu_item.setOnClickListener { billingMenuButtonClicked() }
  }

  override fun childImageClicked(it: View?) {
    this.imageView = it as ImageView

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

        Glide.with(this)
            .asBitmap()
            .load(rotatedImage)
            .into(imageView)
      }
    }
  }

  private var childImage: Uri? = null
  private fun saveImage(imageBitmap: Bitmap): Bitmap? {
    childImage = null
    val matrix = Matrix()
    matrix.postRotate(90F)
    val rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.width, imageBitmap.height, matrix, true)
    childImage = saveImageToInternalStorage(rotatedBitmap)
    return rotatedBitmap
  }

  private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

    val wrapper = ContextWrapper(applicationContext)
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)

    var card = adapter.getList()[0]
    var childName = (card.`object` as Child).firstName + (card.`object` as Child).lastName

    file = File(file, "$childName.jpg")

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
    adapter.addChild(Child("", "", "", "", "Active", "", "", "", ""))
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
          registrationPresenter.deleteChildDataDocument((card as RegistrationCardItem<Child>).`object`.lastName + "_" + card.`object`.firstName)
        }
      }
    }
    finish()
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

  private fun saveChildCard() {
    for (card in adapter.getList()) {
      when (card.viewType) {
        RegistrationCardItem.CHILD -> {

          if (childImage != null) {
            registrationPresenter.uploadFile(childImage!!, firebaseStorage.reference)
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

  fun showProgress() {
    progress_layout_reg.visibility = View.VISIBLE
  }

  fun hideProgress() {
    progress_layout_reg.visibility = View.GONE
  }

  fun onChildImageUploaded(url: String) {
    var chilCard: RegistrationCardItem<Child>? = null

    for (card in adapter.getList()) {
      when (card.viewType) {
        RegistrationCardItem.CHILD -> {
          chilCard = card as RegistrationCardItem<Child>
          if (url != "") {
            card.`object`.childImageUrl = url
          }

          FirestoreUtil(FirebaseFirestore.getInstance(), this)
          registrationPresenter.saveChildDataDocument(FirebaseAuth.getInstance().currentUser,
              HashMapUtil().createChildMap(list[0] as RegistrationCardItem<Child>))
        }

        RegistrationCardItem.PARENT -> {
          FirestoreUtil(FirebaseFirestore.getInstance(), this)
          registrationPresenter.saveParentDataDocument(FirebaseAuth.getInstance().currentUser,
              HashMapUtil().createParentMap(card as RegistrationCardItem<Parent>),
              chilCard?.let { HashMapUtil().createChildMap(it) })
        }
      }
      finish()
    }
  }

  fun onDeleteChildSuccess() {

  }
}
