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

  fun createFamilyMap(familyId: String, familyName: String): HashMap<String, Any> {

    val familyMap = HashMap<String, Any>()
    familyId.let { familyMap.put(FAMILY_ID, it) }
    familyName.let { familyMap.put(FAMILY_NAME, it) }

    return familyMap
  }

  fun createParentMap(card: RegistrationCardItem<Guardian>): HashMap<String, Any> {

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
    card.`object`.pedOfficeNumber.let { pediatricianMap.put("pedOfficeNumber", it) }

    return pediatricianMap

  }

  fun createMedicationMap(card: RegistrationCardItem<Medication>): HashMap<String, Any> {
    val medicationMap = HashMap<String, Any>()
    card.`object`.medicationName.let { medicationMap.put("medicationName", it) }
    card.`object`.medicationTime.let { medicationMap.put("medicationTime", it) }
    card.`object`.medicationDose.let { medicationMap.put("medicationDose", it) }

    return medicationMap
  }

  fun createChildMap(card: Child): HashMap<String, Any> {
    val childMap = HashMap<String, Any>()
    card.childId.let { childMap.put("childId", it) }
    card.childImageUrl.let { childMap.put("childImageUrl", it) }
    card.firstName.let { childMap.put("firstName", it) }
    card.lastName.let { childMap.put("lastName", it) }
    card.birthDate.let { childMap.put("birthDate", it) }
    card.isActive?.let { childMap.put("isActive", it) }
    card.enrollmentDate.let { childMap.put("enrollmentDate", it) }
    card.addressLn1.let { childMap.put("addressLn1", it) }
    card.addressLn2.let { childMap.put("addressLn2", it) }
    card.addressCity.let { childMap.put("addressCity", it) }
    card.addressState.let { childMap.put("addressState", it) }
    card.addressZip.let { childMap.put("addressZip", it) }

    return childMap

  }

  fun createBillingMap(card: RegistrationCardItem<Billing>): java.util.HashMap<String, Any> {
    val billingMap = HashMap<String, Any>()

    card.`object`.billingCycle.let { billingMap.put("billingCycle", it) }
    card.`object`.billingType.let { billingMap.put("billingType", it) }
    card.`object`.flatRateAmount.let { billingMap.put("flatRateAmount", it) }
    card.`object`.hourlyMaxBillableHours.let { billingMap.put("hourlyMaxBillableHours", it) }
    card.`object`.hourlyMinBillableTime.let { billingMap.put("hourlyMinBillableTime", it) }
    card.`object`.hourlyRateAmount.let { billingMap.put("hourlyRateAmount", it) }
    card.`object`.hourlyRoundUpRule.let { billingMap.put("hourlyRoundUpRule", it) }
    card.`object`.discountPercent.let { billingMap.put("discountPercent", it) }
    card.`object`.discountType.let { billingMap.put("discountType", it) }

    return billingMap
  }

}
