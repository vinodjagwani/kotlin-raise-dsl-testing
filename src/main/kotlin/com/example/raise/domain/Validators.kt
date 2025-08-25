package com.example.raise.domain

private val EMAIL_REGEX = """^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$""".toRegex()

fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email)