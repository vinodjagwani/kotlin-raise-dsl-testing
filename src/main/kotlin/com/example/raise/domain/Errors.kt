package com.example.raise.domain

sealed interface DomainError {

    data class InvalidId(val id: Int) : DomainError

    data class InvalidEmail(val value: String) : DomainError

    data class AlreadyExists(val id: Int) : DomainError

    data class RepositoryFailure(
        val message: String, val cause: Throwable? =
            null
    ) : DomainError
}