package registration

import activities.RegistrationActivity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.vince.childcare.R
import core.*
import java.io.File
import java.util.*


class RegistrationPresenter {

  lateinit var activity: RegistrationActivity
  var childToLoad: String = ""

  fun saveChildDataDocument(firebaseUser: FirebaseUser?, childData: HashMap<String, Any>) {

    val child = HashMap<String, Any>()
    child[CHILD] = childData

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        firebaseUser?.displayName.toString().replace(" ", "") + PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData[CHILD_ID].toString())
        .set(child)
        .addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.child_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.child_data_update_failed))
        }
  }

  fun saveParentDataDocument(firebaseUser: FirebaseUser?, parentData: ArrayList<Any>, childData: HashMap<String, Any>?) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        firebaseUser?.displayName.toString().replace(" ", "") + PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData?.get(CHILD_ID).toString())
        .update(GUARDIANS, parentData)
        .addOnSuccessListener {
          Toast.makeText(activity.applicationContext, "Registration saved ", Toast.LENGTH_SHORT).show()
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_update_failed))
        }
  }

  fun deleteChildDataDocument(childToDelete: String) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childToDelete)
        .delete()
        .addOnSuccessListener {
          activity.onDeleteChildSuccess()
          Log.d(FIRESTORE_TAG, "DocumentSnapshot successfully deleted!")
        }
        .addOnFailureListener { e ->
          //todo failed
          Log.w(FIRESTORE_TAG, "Error deleting document", e)
        }
  }

  fun loadChild() {
    var childRef = childToLoad
    var child: Child

    activity.showProgress()

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            for (document in task.result) {
              if (document.id == childRef) {

                var childData = ChildData()
                childData.child = getChild(document)
                childData.guardians = getGuardians(document)
                childData.medications = getMedications(document)
                childData.pediatrician = getPediatrician(document)
                childData.billing = getBilling(document)

                activity.setDataCards(FullChildRegistrationData(childData))

              }
              Log.d("FIRESTORE", document.id + " => " + document.data)
            }
            activity.hideProgress()
          } else {
            Log.d("FIRESTORE", "Error getting documents: ", task.exception)
          }
        }

  }

  private fun getChild(document: QueryDocumentSnapshot): Child? {
    return if (document.contains(CHILD)) {
      val gson = Gson()
      gson.fromJson<Child>(gson.toJsonTree(document[CHILD]), Child::class.java)
    } else {
      null
    }
  }

  private fun getBilling(document: QueryDocumentSnapshot): Billing? {
    return if (document.contains(core.BILLING)) {
      val gson = Gson()
      gson.fromJson<Billing>(gson.toJsonTree(document[BILLING]), Billing::class.java)
    } else {
      null
    }
  }

  private fun getPediatrician(document: QueryDocumentSnapshot): Pediatrician? {
    return if (document.contains(PEDIATRICIAN)) {
      val gson = Gson()
      gson.fromJson<Pediatrician>(gson.toJsonTree(document[PEDIATRICIAN]), Pediatrician::class.java)
    } else {
      null
    }
  }

  private fun getMedications(document: QueryDocumentSnapshot): ArrayList<Medication>? {
    val medications = ArrayList<Medication>()
    return if (document.contains(MEDICATIONS)) {
      for (medication in document[MEDICATIONS] as ArrayList<*>) {
        val gson = Gson()
        medications.add(gson.fromJson<Medication>(gson.toJsonTree(medication), Medication::class.java))
      }
      medications
    } else {
      null
    }
  }

  private fun getGuardians(document: QueryDocumentSnapshot): ArrayList<Guardian>? {
    val guardians = ArrayList<Guardian>()
    return if (document.contains(GUARDIANS)) {
      for (guardian in document[GUARDIANS] as ArrayList<*>) {
        val gson = Gson()
        guardians.add(gson.fromJson<Guardian>(gson.toJsonTree(guardian), Guardian::class.java))
      }
      guardians
    } else {
      null
    }
  }

  fun uploadChildImage(filePath: Uri?, storageReference: StorageReference) {

    //checking if file is available
    if (filePath != null) {
      //displaying progress dialog while image is uploading
      activity.showProgress()

      val file = Uri.fromFile(File(filePath.toString()))
      val ref = storageReference.child(FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
          PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid + "/" + STORAGE_PATH_CHILD_IMAGES + file.lastPathSegment)
      val uploadTask: UploadTask = ref.putFile(file)

      val urlTask = uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
          throw task.exception!!
        }

        ref.downloadUrl
      }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
          activity.onChildImageUploaded(task.result.toString())
        } else {
          activity.hideProgress()
          Toast.makeText(activity.applicationContext, task.result.toString(), Toast.LENGTH_LONG).show()
        }
      }
    } else {
      //display an error if no file is selected
    }
  }

  fun setUp(registrationActivity: RegistrationActivity, childToLoad: String) {
    this.activity = registrationActivity
    this.childToLoad = childToLoad
  }

  fun setUp(registrationActivity: RegistrationActivity) {
    this.activity = registrationActivity
  }

  fun savePediatricianDataDocument(currentUser: FirebaseUser?, pediatricianMap: HashMap<String, Any>, childData: HashMap<String, Any>?) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData?.get(CHILD_ID).toString())
        .update(PEDIATRICIAN, pediatricianMap)

        .addOnSuccessListener {
          Toast.makeText(activity.applicationContext, "Registration saved ", Toast.LENGTH_SHORT).show()
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_succeeded))
        }

        .addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_update_failed))
        }
  }

  fun saveMedicationDataDocument(currentUser: FirebaseUser?, medicationMap: ArrayList<Any>, childData: HashMap<String, Any>?) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData?.get(CHILD_ID).toString())
        .update(MEDICATIONS, medicationMap)
        .addOnSuccessListener {
          Toast.makeText(activity.applicationContext, "Registration saved ", Toast.LENGTH_SHORT).show()
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.medication_data_update_failed))
        }
  }


  fun saveBillingDataDocument(currentUser: FirebaseUser?, billingMap: HashMap<String, Any>, childData: HashMap<String, Any>?) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData?.get(CHILD_ID).toString()).collection(COLLECTION_BILLING).document()
        .update("billing", billingMap)
        .addOnSuccessListener {
          Toast.makeText(activity.applicationContext, "Registration saved ", Toast.LENGTH_SHORT).show()
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_succeeded))
        }

        .addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_update_failed))
        }
  }
}
