package org.insbaixcamp.lazycolumn2.data

import com.google.firebase.firestore.DocumentId

data class Horse(
    @set:DocumentId
    var id: String = "",
    var name: String = "",
    var age: Int = 0,
    var breed: String = "",
    var weight: Double = 100.0,
    var topSpeed: Double = 40.0,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)