package core

import registration.*


class HashMapUtil {

  fun createChildMap(card: RegistrationCardItem<Child>): HashMap<String, Any> {

    val childMap = HashMap<String, Any>()
    card.`object`.childId.let { childMap.put(CHILD_ID, it) }
    card.`object`.childImageUrl.let { childMap.put(CHILD_IMAGE_URL, it) }
    card.`object`.firstName.let { childMap.put(FIRST_NAME, it) }
    card.`object`.lastName.let { childMap.put(LAST_NAME, it) }
    card.`object`.birthDate.let { childMap.put(BIRTH_DATE, it) }
    card.`object`.isActive?.let { childMap.put(IS_ACTIVE, it) }
    card.`object`.enrollmentDate.let { childMap.put(ENROLLMENT_DATE, it) }
    card.`object`.addressLn1.let { childMap.put(ADDRESS_LN_1, it) }
    card.`object`.addressLn2.let { childMap.put(ADDRESS_LN_2, it) }
    card.`object`.addressCity.let { childMap.put(ADDRESS_CITY, it) }
    card.`object`.addressState.let { childMap.put(ADDRESS_STATE, it) }
    card.`object`.addressZip.let { childMap.put(ADDRESS_ZIP, it) }

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
    card.`object`.firstName.let { parentMap.put(FIRST_NAME, it) }
    card.`object`.lastName.let { parentMap.put(LAST_NAME, it) }
    card.`object`.emailAddress.let { parentMap.put(EMAIL_ADDRESS, it) }
    card.`object`.phoneNumber1.let { parentMap.put(PHONE_NUMBER_1, it) }
    card.`object`.phoneNumber2.let { parentMap.put(PHONE_NUMBER_2, it) }

    return parentMap
  }

  fun createPediatricianMap(card: RegistrationCardItem<Pediatrician>): HashMap<String, Any> {
    val pediatricianMap = HashMap<String, Any>()
    card.`object`.pediatricianName.let { pediatricianMap.put(PEDIATRICIAN_NAME, it) }
    card.`object`.pediatricianOfficeName.let { pediatricianMap.put(PEDIATRICIAN_OFFICE, it) }
    card.`object`.pediatricianOfficeNumber.let { pediatricianMap.put(PEDIATRICIAN_NUMBER, it) }

    return pediatricianMap

  }

  fun createMedicationMap(card: RegistrationCardItem<Medication>): HashMap<String, Any> {
    val medicationMap = HashMap<String, Any>()
    card.`object`.medicationName.let { medicationMap.put(MEDICATION_NAME, it) }
    card.`object`.medicationTime.let { medicationMap.put(MEDICATION_TIME, it) }
    card.`object`.medicationDose.let { medicationMap.put(MEDICATIONN_DOSE, it) }

    return medicationMap
  }

  fun createChildMap(card: Child): HashMap<String, Any> {
    val childMap = HashMap<String, Any>()
    card.childId.let { childMap.put(CHILD_ID, it) }
    card.childImageUrl.let { childMap.put(CHILD_IMAGE_URL, it) }
    card.firstName.let { childMap.put(FIRST_NAME, it) }
    card.lastName.let { childMap.put(LAST_NAME, it) }
    card.birthDate.let { childMap.put(BIRTH_DATE, it) }
    card.isActive?.let { childMap.put(IS_ACTIVE, it) }
    card.enrollmentDate.let { childMap.put(ENROLLMENT_DATE, it) }
    card.addressLn1.let { childMap.put(ADDRESS_LN_1, it) }
    card.addressLn2.let { childMap.put(ADDRESS_LN_2, it) }
    card.addressCity.let { childMap.put(ADDRESS_CITY, it) }
    card.addressState.let { childMap.put(ADDRESS_STATE, it) }
    card.addressZip.let { childMap.put(ADDRESS_ZIP, it) }

    return childMap

  }

  fun createBillingMap(card: RegistrationCardItem<Billing>): java.util.HashMap<String, Any> {
    val billingMap = HashMap<String, Any>()

    card.`object`.billingCycle.let { billingMap.put(BILLING_CYCLE, it) }
    card.`object`.billingType.let { billingMap.put(BILLING_TYPE, it) }
    card.`object`.flatRateAmount.let { billingMap.put(BILLING_FLAT_RATE, it) }
    card.`object`.hourlyMaxBillableHours.let { billingMap.put(BILLING_HOURLY_MAX_HOURS, it) }
    card.`object`.hourlyMinBillableTime.let { billingMap.put(BILLING_HOURLY_MIN_TIME, it) }
    card.`object`.hourlyRateAmount.let { billingMap.put(BILLING_HOURLY_RATE, it) }
    card.`object`.hourlyRoundUpRule.let { billingMap.put(BILLING_HOURLY_ROUND_UP, it) }
    card.`object`.discountPercent.let { billingMap.put(BILLING_DISCOUNT, it) }
    card.`object`.discountType.let { billingMap.put(BILLING_DISCOUNT_TYPE, it) }

    return billingMap
  }

}
