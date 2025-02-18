package com.togethersafe.app.data

data class Incident(
    val category: String,
    val riskLevel: String,
    val description: String,
    val location: String,
    val dateTime: String,
    val status: String,
    val reportCount: Int,
    val mediaUrls: List<String>,
    val reports: List<String>
)
