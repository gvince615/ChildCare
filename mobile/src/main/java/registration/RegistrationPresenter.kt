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


class RegistrationPresenter {

  lateinit var activity: RegistrationActivity
  var childToLoad: String = ""

  fun saveChildDataDocument(firebaseUser: FirebaseUser?, childData: HashMap<String, Any>) {

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        firebaseUser?.displayName.toString().replace(" ", "") + PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData[CHILD_ID].toString())
        .set(childData).addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.child_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.child_data_update_failed))
        }
  }

  fun saveParentDataDocument(firebaseUser: FirebaseUser?, parentData: HashMap<String, Any>, childData: HashMap<String, Any>?) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        firebaseUser?.displayName.toString().replace(" ", "") + PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData?.get(CHILD_ID).toString())
        .collection(COLLECTION_PARENTS).document(parentData[LAST_NAME].toString() + "_" + parentData[FIRST_NAME].toString())
        .set(parentData).addOnSuccessListener {
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
    var parentList: ArrayList<Parent> = ArrayList()
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

                child = document.toObject(Child::class.java)
                child.isActive = document[IS_ACTIVE].toString()
                child.parents = getParents(document)
                child.medications = getMedications(document)
                child.pediatrician = getPediatrician(document)
                child.billing = getBilling(document)

                activity.setDataCards(FullChildRegistrationData(child))

              }
              Log.d("FIRESTORE", document.id + " => " + document.data)
            }
            activity.hideProgress()
          } else {
            Log.d("FIRESTORE", "Error getting documents: ", task.exception)
          }
        }

  }

  private fun getBilling(document: QueryDocumentSnapshot): Billing? {
    var billing = Billing()
    return if (document.contains("billing")) {
      val gson = Gson()
      gson.fromJson<Billing>(gson.toJsonTree(billing), Billing::class.java)
    } else {
      null
    }
  }

  private fun getPediatrician(document: QueryDocumentSnapshot): Pediatrician? {
    var pediatrician = Pediatrician()
    return if (document.contains("pediatrician")) {
      val gson = Gson()
      gson.fromJson<Pediatrician>(gson.toJsonTree(pediatrician), Pediatrician::class.java)
    } else {
      null
    }
  }

  private fun getMedications(document: QueryDocumentSnapshot): ArrayList<Medication>? {
    val medications = ArrayList<Medication>()
    return if (document.contains("medications")) {
      for (medication in document["medications"] as ArrayList<*>) {
        val gson = Gson()
        medications.add(gson.fromJson<Medication>(gson.toJsonTree(medication), Medication::class.java))
      }
      medications
    } else {
      null
    }
  }

  private fun getParents(document: QueryDocumentSnapshot): ArrayList<Parent>? {
    val guardians = ArrayList<Parent>()
    return if (document.contains("guardians")) {
      for (guardian in document["guardians"] as ArrayList<*>) {
        val gson = Gson()
        guardians.add(gson.fromJson<Parent>(gson.toJsonTree(guardian), Parent::class.java))
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
        .collection(COLLECTION_MEDICAL).document(DOCUMENT_MEDICAL)
        .set(pediatricianMap).addOnSuccessListener {
          Toast.makeText(activity.applicationContext, "Registration saved ", Toast.LENGTH_SHORT).show()
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_update_failed))
        }
  }

  fun saveMedicationDataDocument(currentUser: FirebaseUser?, medicationMap: HashMap<String, Any>, childData: HashMap<String, Any>?) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData?.get(CHILD_ID).toString())
        .collection(COLLECTION_MEDICAL).document(DOCUMENT_MEDICAL).collection(COLLECTION_MEDICATION).document()
        .set(medicationMap).addOnSuccessListener {
          Toast.makeText(activity.applicationContext, "Registration saved ", Toast.LENGTH_SHORT).show()
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_update_failed))
        }
  }


  fun saveBillingDataDocument(currentUser: FirebaseUser?, medicationMap: HashMap<String, Any>, childData: HashMap<String, Any>?) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData?.get(CHILD_ID).toString())
        .collection(COLLECTION_BILLING).document()
        .set(medicationMap).addOnSuccessListener {
          Toast.makeText(activity.applicationContext, "Registration saved ", Toast.LENGTH_SHORT).show()
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.parent_data_update_failed))
        }
  }
}
