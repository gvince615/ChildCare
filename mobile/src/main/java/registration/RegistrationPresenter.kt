package registration

import activities.RegistrationActivity
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import core.*
import java.io.File




class RegistrationPresenter {

  lateinit var activity: RegistrationActivity
  var childToLoad: String = ""

  fun loadChild() {
    var childRef = childToLoad
    var parentList: ArrayList<Parent> = ArrayList()
    var child: Child

    activity.showProgress()

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            for (document in task.result) {
              if (document.id == childRef) {

                child = document.toObject(Child::class.java)
                child.isActive = document["isActive"].toString()

                FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
                    PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
                    .collection(COLLECTION_REGISTRATION_DATA).document(childRef).collection(COLLECTION_PARENTS)
                    .get()
                    .addOnCompleteListener { task ->
                      if (task.isSuccessful) {
                        for (document in task.result) {
                          var parent = document.toObject(Parent::class.java)
                          parentList.add(parent)
                          Log.d("FIRESTORE", document.id + " => " + document.data)
                        }

                        var fullChildRegistrationData = FullChildRegistrationData(child, parentList)

                        activity.setDataCards(FullChildRegistrationData(child, parentList))


                        Log.d("CHILD_REG_DATA", "child data documents: " + fullChildRegistrationData.toString())

                      } else {
                        Log.d("FIRESTORE", "Error getting documents: ", task.exception)
                      }
                      activity.hideProgress()
                    }
              }
              Log.d("FIRESTORE", document.id + " => " + document.data)
            }
          } else {
            Log.d("FIRESTORE", "Error getting documents: ", task.exception)
          }
        }

  }

  fun uploadFile(filePath: Uri, storageReference: StorageReference) {

    //checking if file is available
    if (filePath != null) {
      //displaying progress dialog while image is uploading
      activity.showProgress()

      val file = Uri.fromFile(File(filePath.toString()))
      val ref = storageReference.child(FirebaseAuth.getInstance().currentUser?.uid + "/" + STORAGE_PATH_CHILD_IMAGES + file.lastPathSegment)
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
          //Toast.makeText(activity.applicationContext, task.result, Toast.LENGTH_LONG).show()
        }
      }


//
//      uploadTask
//        .addOnFailureListener {
//          activity.hideProgress()
//          Toast.makeText(activity.applicationContext, it.message, Toast.LENGTH_LONG).show()
//        }
//        .addOnSuccessListener {
//          //dismissing the progress dialog
//          activity.hideProgress()
//          //displaying success toast
//          Toast.makeText(activity.applicationContext, "File Uploaded ", Toast.LENGTH_LONG).show()
//
//          activity.onChildImageUploaded(it. downloadUrl.toString())
//
//        }
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
}
