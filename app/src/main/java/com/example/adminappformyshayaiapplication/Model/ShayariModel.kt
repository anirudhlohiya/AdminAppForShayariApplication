package com.example.shyariapplicationbyanirudhlohiya.Model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ShayariModel(
    val id: String? = null,
    val data: String? = null,
    @ServerTimestamp
    val timestamp: Date? = null
)
