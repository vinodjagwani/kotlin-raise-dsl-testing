package com.example.raise.service

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.raise.domain.DomainError
import com.example.raise.domain.User
import com.example.raise.domain.isValidEmail
import com.example.raise.repository.UserRepository

class UserService(private val repo: UserRepository) {

    /** Public API: run the Raise program and return Either */
    fun createUser(id: Int, email: String): Either<DomainError, User> = either {
        createUserInternal(id, email)
    }

    /** Raise-based program */
    private fun Raise<DomainError>.createUserInternal(id: Int, email: String):
            User {
        ensure(id > 0) { DomainError.InvalidId(id) }
        ensure(isValidEmail(email)) { DomainError.InvalidEmail(email) }
        val existing = repo.find(id)
        ensure(existing == null) { DomainError.AlreadyExists(id) }
        val user = User(id, email)
        val saved = repo.save(user)
        if (!saved) raise(DomainError.RepositoryFailure("save returned false"))
        return user
    }

    /** Traditional-Either-based program */
    fun createUserWithoutRaise(id: Int, email: String): Either<DomainError, User> {
        return if (id <= 0) Either.Left(DomainError.InvalidId(id))
        else if (!isValidEmail(email)) Either.Left(DomainError.InvalidEmail(email))
        else {
            val existing = repo.find(id)
            if (existing != null) Either.Left(DomainError.AlreadyExists(id))
            else {
                val user = User(id, email)
                val saved = repo.save(user)
                if (!saved) Either.Left(DomainError.RepositoryFailure("save returned false"))
                else Either.Right(user)
            }
        }
    }
}