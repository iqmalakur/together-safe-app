package com.togethersafe.app.data.model

data class Incident(
    val category: String,
    val date: String,
    val time: String,
    val riskLevel: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val mediaUrls: List<String>,
    val reports: List<IncidentReport>,
)
