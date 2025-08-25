package com.example.raise.kotest

import com.example.raise.domain.DomainError
import com.example.raise.domain.User
import com.example.raise.repository.UserRepository
import com.example.raise.service.UserService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UserServiceKotestSpec : BehaviorSpec({

    val repo = mockk<UserRepository>(relaxed = true)
    val service = UserService(repo)

    given("a user creation service") {
        `when`("data is valid and repository saves") {
            every { repo.find(1) } returns null
            every { repo.save(User(1, "a@b.com")) } returns true
            then("it returns the created user (Right)") {
                val result = service.createUser(1, "a@b.com")
                result.getOrNull().shouldNotBeNull()
                result.leftOrNull().shouldBeNull()
                verify { repo.find(1) }
                verify { repo.save(User(1, "a@b.com")) }
                confirmVerified(repo)
            }
        }
        `when`("id is invalid") {
            then("it raises InvalidId") {
                val res = service.createUser(0, "a@b.com")
                res.leftOrNull().shouldBeInstanceOf<DomainError.InvalidId>()
                verify(exactly = 0) { repo.find(any()) }
                verify(exactly = 0) { repo.save(any()) }
                7
            }
        }
        `when`("email is invalid") {
            then("it raises InvalidEmail") {
                val res = service.createUser(10, "bad")
                res.leftOrNull().shouldBeInstanceOf<DomainError.InvalidEmail>()
                verify(exactly = 0) { repo.find(any()) }
                verify(exactly = 0) { repo.save(any()) }
            }
        }
        `when`("user already exists") {
            every { repo.find(42) } returns User(42, "x@y.com")
            then("it raises AlreadyExists and does not call save") {
                val res = service.createUser(42, "x@y.com")
                res.leftOrNull().shouldBeInstanceOf<DomainError.AlreadyExists>()
                verify { repo.find(42) }
                verify(exactly = 0) { repo.save(any()) }
            }
        }
        `when`("repository save fails") {
            every { repo.find(5) } returns null
            every { repo.save(User(5, "ok@ok.com")) } returns false
            then("it raises RepositoryFailure") {
                val res = service.createUser(5, "ok@ok.com")
                res.leftOrNull().shouldBeInstanceOf<DomainError.RepositoryFailure>()
                verify { repo.find(5) }
                verify { repo.save(User(5, "ok@ok.com")) }
            }
        }
    }
})