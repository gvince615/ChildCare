package core

import registration.Child
import registration.Parent
import registration.RegistrationCardItem



class HashMapUtil {

  fun createChildMap(card: RegistrationCardItem<Child>): HashMap<String, Any> {

    val childMap = HashMap<String, Any>()
    card.`object`.firstName?.let { childMap.put("first_name", it) }
    card.`object`.lastName?.let { childMap.put("last_name", it) }

    card.`object`.birthDate?.let { childMap.put("birth_date", it) }
    card.`object`.enrollmentDate?.let { childMap.put("enrollment_date", it) }

    card.`object`.addressLn1?.let { childMap.put("address_ln_1", it) }
    card.`object`.addressLn2?.let { childMap.put("address_ln_2", it) }
    card.`object`.addressCity?.let { childMap.put("address_city", it) }
    card.`object`.addressState?.let { childMap.put("address_state", it) }
    card.`object`.addressZip?.let { childMap.put("address_zip", it) }

    return childMap
  }

  fun createParentMap(card: RegistrationCardItem<Parent>): HashMap<String, Any> {

    val parentMap = HashMap<String, Any>()
    card.`object`.firstName?.let { parentMap.put("first_name", it) }
    card.`object`.lastName?.let { parentMap.put("last_name", it) }

    card.`object`.addressLn1?.let { parentMap.put("address_ln_1", it) }
    card.`object`.addressLn2?.let { parentMap.put("address_ln_2", it) }
    card.`object`.addressCity?.let { parentMap.put("address_city", it) }
    card.`object`.addressState?.let { parentMap.put("address_state", it) }
    card.`object`.addressZip?.let { parentMap.put("address_zip", it) }

    return parentMap
  }

}
