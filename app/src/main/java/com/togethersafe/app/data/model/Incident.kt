package com.togethersafe.app.data.model

data class Incident(
    val category: String,
    val date: String,
    val time: String,
    val riskLevel: String,
    val location: String,
    val latitude: Float,
    val longitude: Float,
    val status: String,
)
