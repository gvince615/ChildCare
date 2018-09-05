package registration

import activities.RegistrationActivity
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.vince.childcare.R
import core.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class RegistrationPresenter {

  private lateinit var activity: RegistrationActivity

  fun deleteChildDataDocument(childId: String) {
    val id = childId.split("-")
    val familyId = id[0]

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId).collection(COLLECTION_CHILDREN).document(childId)
        .delete()
        .addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.getString(R.string.child_deleted))
        }
        .addOnFailureListener { e ->
          //todo failed
          Log.w(FIRESTORE_TAG, activity.getString(R.string.error_deleting_child), e)
        }
  }

  fun loadChild(childID: String) {
    val id = childID.split("-")
    val familyId = id[0]

    activity.showProgress()
    val childData = ChildData()

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
                .collection(COLLECTION_CHILDREN).document(childID).get()
                .addOnCompleteListener {
                  if (it.isSuccessful) {
                    val document = it.result

                    childData.child = getChild(document)
                    childData.billing = getBilling(document)
                    childData.medications = getMedications(document)
                    activity.setDataCards(FullChildRegistrationData(childData))
                    Log.d(FIRESTORE_TAG + REGISTRATION_TAG, document.id + " => " + document.data)

                    activity.hideProgress()
                  } else {
                    activity.hideProgress()
                    //todo handle fail
                    Log.d(FIRESTORE_TAG + REGISTRATION_TAG, activity.getString(R.string.error_getting_child_data), it.exception)
                  }
                }
          } else {
            activity.hideProgress()
            Log.d(FIRESTORE_TAG + REGISTRATION_TAG, activity.getString(R.string.error_getting_family_data), task.exception)
          }
        }
  }

  private fun getChild(document: DocumentSnapshot): Child? {
    return if (document.contains(CHILD)) {
      val gson = Gson()
      gson.fromJson<Child>(gson.toJsonTree(document[CHILD]), Child::class.java)
    } else {
      null
    }
  }

  private fun getBilling(document: DocumentSnapshot): Billing? {
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

  private fun getMedications(document: DocumentSnapshot): ArrayList<Medication>? {
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

      uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
          throw task.exception!!
        }

        ref.downloadUrl
      }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
          activity.hideProgress()
          activity.onChildImageUploaded(task.result.toString())
          Log.d(FIRESTORE_TAG + REGISTRATION_TAG, activity.getString(R.string.success_uploading_child_image), task.exception)
        } else {
          activity.hideProgress()
          Log.d(FIRESTORE_TAG + REGISTRATION_TAG, activity.getString(R.string.error_uploading_child_image), task.exception)
        }
      }
    } else {
      Log.d(REGISTRATION_TAG, activity.getString(R.string.null_filepath))
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
            if (task.result.isEmpty) {
              activity.hideProgress()
              activity.onNoFamilyNamesRetrieved()
              Log.d(FIRESTORE_TAG + REGISTRATION_TAG, activity.getString(R.string.no_family_names_to_get))
            } else {
              for (document in task.result) {
                if (document.contains(FAMILY_DATA)) {
                  if (!familyNames.contains(document.id)) {
                    familyNames.add(document.id)
                  }
                }
              }
              val names = familyNames.toArray(arrayOfNulls<String>(familyNames.size))
              activity.onFamilyNamesRetrieved(names)
              activity.hideProgress()
              Log.d(FIRESTORE_TAG + REGISTRATION_TAG, activity.getString(R.string.retrieved_famiy_names) + familyNames.toString())
            }


          } else {
            activity.hideProgress()
            Log.d(FIRESTORE_TAG + REGISTRATION_TAG, activity.getString(R.string.error_getting_family_names), task.exception)
          }
        }
  }

  fun getFamilyData(familyId: String) {
    activity.showProgress()
    val childData = ChildData()

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
                .addOnCompleteListener {
                  if (it.isSuccessful) {
                    for (document in it.result) {

                      val newChild = getChild(document)
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
                      Log.d(FIRESTORE_TAG + REGISTRATION_TAG, document.id + " => " + document.data)
                      break
                    }
                    activity.hideProgress()
                  } else {
                    activity.hideProgress()
                    //todo handle fail
                    Log.d(FIRESTORE_TAG + REGISTRATION_TAG, activity.getString(R.string.error_getting_child_data), it.exception)
                  }
                }
            activity.hideProgress()
          } else {
            activity.hideProgress()
            Log.d(FIRESTORE_TAG + REGISTRATION_TAG, activity.getString(R.string.error_getting_family_data), task.exception)

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

  fun saveNewPediatricianDataDocument(currentUser: FirebaseUser?, familyId: String, pediatricianMap: HashMap<String, Any>) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId)
        .update(PEDIATRICIAN, pediatricianMap)
        .addOnSuccessListener {
          Log.d(FIRESTORE_TAG, activity.applicationContext.getString(R.string.pediatrician_data_succeeded))
        }.addOnFailureListener {
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
