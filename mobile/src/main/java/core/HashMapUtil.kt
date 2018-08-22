package core

import registration.*


class HashMapUtil {

  fun createChildMap(card: RegistrationCardItem<Child>): HashMap<String, Any> {

    val childMap = HashMap<String, Any>()
    card.`object`.childId.let { childMap.put("childId", it) }
    card.`object`.childImageUrl.let { childMap.put("childImageUrl", it) }
    card.`object`.firstName.let { childMap.put("firstName", it) }
    card.`object`.lastName.let { childMap.put("lastName", it) }
    card.`object`.birthDate.let { childMap.put("birthDate", it) }
    card.`object`.isActive?.let { childMap.put("isActive", it) }
    card.`object`.enrollmentDate.let { childMap.put("enrollmentDate", it) }
    card.`object`.addressLn1.let { childMap.put("addressLn1", it) }
    card.`object`.addressLn2.let { childMap.put("addressLn2", it) }
    card.`object`.addressCity.let { childMap.put("addressCity", it) }
    card.`object`.addressState.let { childMap.put("addressState", it) }
    card.`object`.addressZip.let { childMap.put("addressZip", it) }

    return childMap
  }

  fun createParentMap(card: RegistrationCardItem<Parent>): HashMap<String, Any> {

    val parentMap = HashMap<String, Any>()
    card.`object`.firstName.let { parentMap.put("firstName", it) }
    card.`object`.lastName.let { parentMap.put("lastName", it) }
    card.`object`.emailAddress.let { parentMap.put("emailAddress", it) }
    card.`object`.phoneNumber1.let { parentMap.put("phoneNumber1", it) }
    card.`object`.phoneNumber2.let { parentMap.put("phoneNumber2", it) }

    return parentMap
  }

  fun createPediatricianMap(card: RegistrationCardItem<Pediatrician>): HashMap<String, Any> {
    val pediatricianMap = HashMap<String, Any>()
    card.`object`.pedName.let { pediatricianMap.put("pedName", it) }
    card.`object`.pedOfficeName.let { pediatricianMap.put("pedOfficeName", it) }
    card.`object`.pedOfficeNum.let { pediatricianMap.put("pedOfficeNumber", it) }

    return pediatricianMap

  }

  fun createMedicationMap(card: RegistrationCardItem<Medication>): HashMap<String, Any> {
    val medicationMap = HashMap<String, Any>()
    card.`object`.medName.let { medicationMap.put("medicationName", it) }
    card.`object`.medTime.let { medicationMap.put("medicationTime", it) }
    card.`object`.medDose.let { medicationMap.put("medicationDose", it) }

    return medicationMap
  }

}
