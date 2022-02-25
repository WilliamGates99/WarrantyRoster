package com.xeniac.warrantyroster_manager.firebase

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xeniac.warrantyroster_manager.utils.Constants

object FirestoreInstance {

    val categoriesCollectionRef by lazy {
        Firebase.firestore.collection(Constants.COLLECTION_CATEGORIES)
    }

    val warrantiesCollectionRef by lazy {
        Firebase.firestore.collection(Constants.COLLECTION_WARRANTIES)
    }
}