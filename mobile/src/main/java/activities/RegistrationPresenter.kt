package activities

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import core.COLLECTION_PARENTS
import core.COLLECTION_REGISTRATION_DATA
import core.COLLECTION_USER_DATA
import core.PREFIX_UID
import registration.Child
import registration.FullChildRegistrationData
import registration.Parent

class RegistrationPresenter {

  lateinit var activity: RegistrationActivity
  var childToLoad: String = ""

  fun loadChild() {
    var childRef = childToLoad
    var parentList: ArrayList<Parent> = ArrayList()
    var child = Child("", "", "", "", "", "", "", "", "")

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            for (document in task.result) {
              if (document.id == childRef) {

                child = document.toObject(Child::class.java)

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
                    }
              }
              Log.d("", document.id + " => " + document.data)
            }
          } else {
            Log.d("", "Error getting documents: ", task.exception)
          }
        }

  }

  fun setUp(registrationActivity: RegistrationActivity, childToLoad: String) {
    this.activity = registrationActivity
    this.childToLoad = childToLoad
  }
}
