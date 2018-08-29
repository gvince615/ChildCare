package registration

import activities.RegistrationActivity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.vince.childcare.R
import core.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class RegistrationPresenter {

  lateinit var activity: RegistrationActivity

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

  fun loadChild(childID: String) {
    activity.showProgress()

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            for (document in task.result) {
              if (document.id == childID) {

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
    return if (document.contains(BILLING)) {
      val gson = Gson()
      gson.fromJson<Billing>(gson.toJsonTree(document[BILLING]), Billing::class.java)
    } else {
      null
    }
  }

  private fun getPediatrician(document: DocumentSnapshot): Pediatrician? {
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

  private fun getGuardians(document: DocumentSnapshot): ArrayList<Guardian>? {
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

  private fun getFamily(document: DocumentSnapshot): Family? {
    return if (document.contains(FAMILY_DATA)) {
      val gson = Gson()
      gson.fromJson<Family>(gson.toJsonTree(document[FAMILY_DATA]), Family::class.java)
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
          activity.hideProgress()
          activity.onChildImageUploaded(task.result.toString())
        } else {
          activity.hideProgress()
          Toast.makeText(activity.applicationContext, task.result.toString(), Toast.LENGTH_LONG).show()
        }
      }
    } else {
      Log.d("NULL PATH", "Can't be null")
    }
  }

  fun setUp(registrationActivity: RegistrationActivity) {
    this.activity = registrationActivity
  }

  fun getFamilies() {
    activity.showProgress()
    val familyNames = ArrayList<String>()
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid).collection(COLLECTION_REGISTRATION_DATA)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            for (document in task.result) {
              if (document.contains(FAMILY_DATA)) {
                if (!familyNames.contains(document.id)) {
                  familyNames.add(document.id)
                }
              }

              val names = familyNames.toArray(arrayOfNulls<String>(familyNames.size))

              activity.onFamilyNamesRetrieved(names)
              activity.hideProgress()
              Log.d("FIRESTORE", "families => " + familyNames.toString())
            }

            if (task.result.isEmpty) {
              activity.hideProgress()
              activity.onNoFamilyNamesRetrieved()
              Log.d("FIRESTORE", "no families => ")
            }
          } else {
            activity.hideProgress()
            Log.d("FIRESTORE", "Error getting documents: ", task.exception)
          }
        }
  }

  fun getFamilyData(familyId: String) {
    activity.showProgress()
    var childData = ChildData()

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId).get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {

            childData.family = getFamily(task.result)
            childData.guardians = getGuardians(task.result)
            childData.pediatrician = getPediatrician(task.result)

            FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
                FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
                    PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid).collection(COLLECTION_REGISTRATION_DATA).document(familyId)
                .collection(COLLECTION_CHILDREN).get()
                .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                    for (document in task.result) {

                      var newChild = getChild(document)
                      (newChild as Child).firstName = ""
                      newChild.lastName = ""
                      newChild.enrollmentDate = Date().toString()
                      newChild.birthDate = ""
                      newChild.isActive = ACTIVE
                      newChild.childImageUrl = ""
                      newChild.childId = familyId + "-" + ((Math.random() * 98999).toInt() + 1000).toString()

                      childData.child = newChild
                      childData.billing = getBilling(document)
                      activity.setDataCardsForAddNewChild(FullChildRegistrationData(childData))
                      Log.d("FIRESTORE", document.id + " => " + document.data)
                      break
                    }
                    activity.hideProgress()
                  } else {
                    activity.hideProgress()
                    //todo handle fail
                    Log.d("FIRESTORE", "Error getting documents: ", task.exception)
                  }
                }
          } else {
            activity.hideProgress()
            Log.d("FIRESTORE", "Error getting documents: ", task.exception)
          }
        }
  }

  fun saveNewFamily(firebaseUser: FirebaseUser?, familyData: HashMap<String, Any>) {

    val family = HashMap<String, Any>()
    family[FAMILY_DATA] = familyData

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        firebaseUser?.displayName.toString().replace(" ", "") + PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyData[FAMILY_ID].toString())
        .set(family)
        .addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.family_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.family_data_update_failed))
        }

  }

  fun saveNewChildDataDocument(firebaseUser: FirebaseUser?, familyID: String, childID: String, childData: HashMap<String, Any>) {
    val child = HashMap<String, Any>()
    child[CHILD] = childData
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        firebaseUser?.displayName.toString().replace(" ", "") + PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyID).collection(COLLECTION_CHILDREN).document(childID)
        .set(child)
        .addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.child_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.child_data_update_failed))
        }
  }

  fun saveNewPediatricianDataDocument(firebaseUser: FirebaseUser?, familyId: String, pediatricianMap: HashMap<String, Any>) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        firebaseUser?.displayName.toString().replace(" ", "") + PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId)
        .update(PEDIATRICIAN, pediatricianMap)
        .addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.pediatrician_data_succeeded))
        }
        .addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.pediatrician_data_update_failed))
        }
  }

  fun saveNewGuardianDataDocument(firebaseUser: FirebaseUser?, familyId: String, parentData: ArrayList<Any>) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        firebaseUser?.displayName.toString().replace(" ", "") + PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId)
        .update(GUARDIANS, parentData)
        .addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.guardian_data_succeeded))
        }
        .addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.guardian_data_update_failed))
        }
  }

  fun saveNewMedicationDataDocument(currentUser: FirebaseUser?, familyId: String, childID: String, medicationMap: ArrayList<Any>) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId).collection(COLLECTION_CHILDREN).document(childID)
        .update(MEDICATIONS, medicationMap)
        .addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.medication_data_succeeded))
        }.addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.medication_data_update_failed))
        }
  }


  fun saveNewBillingDataDocument(currentUser: FirebaseUser?, familyId: String, childID: String, billingMap: HashMap<String, Any>) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId).collection(COLLECTION_CHILDREN).document(childID)
        .update(BILLING, billingMap)
        .addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.billing_data_succeeded))
        }
        .addOnFailureListener {
          Log.e(FIRESTORE_TAG, activity.applicationContext.getString(R.string.billing_data_update_failed))
        }
  }

}
