package com.manisoft.scraprushapp.utils

import android.text.TextUtils
import android.util.Patterns
import androidx.core.text.isDigitsOnly
import java.util.regex.Pattern

object Validator {

    fun checkPassword(pwd: String): Exception? {
        val s = pwd.trim()
        return when {
            s.isEmpty() -> Exceptions.EmptyPasswordException()
            s.length < 6 -> Exceptions.LengthException()
            TextUtils.isDigitsOnly(s) -> Exceptions.WeakPasswordException()
            else -> null
        }
    }

    fun checkPasswordIsNotEmpty(pwd: String): Exception? {
        return if (pwd.trim().isEmpty()) Exceptions.EmptyPasswordException()
        else null
    }

    fun checkEmail(email: String): Exception? {
        val s = email.trim()
        return when {
            s.isEmpty() -> Exceptions.EmptyEmailException()
            !Pattern.matches(Patterns.EMAIL_ADDRESS.toRegex().toString(), s) -> Exceptions.InvalidEmailException()
            else -> null
        }
    }


    fun checkPhoneNumber(phone: String, code: String): Exception? {
        if (code.trim().isEmpty() || !code.trim().isDigitsOnly()) {
            return Exceptions.InvalidCountryCodeException()
        }
        val s = phone.trim()
        return when {
            s.isEmpty() -> Exceptions.EmptyPhoneNumberException()
            !s.isDigitsOnly() || s.length < 10 -> Exceptions.InvalidPhoneNumberException()
            else -> null
        }
    }

    fun checkUsername(username: String, signup: Boolean = false): Exception? {
        val s = username.trim()
        return when {
            s.isEmpty() -> Exceptions.EmptyUsernameException()
            !Pattern.matches(USERNAME_REGEX.toString(), s) -> {
                if (!signup) Exceptions.InvalidUsernameException()
                else Exceptions.InvalidUsernameGenerationException()
            }

            else -> null
        }
    }

    fun isUsername(username: String): Boolean = Pattern.matches(USERNAME_REGEX.toString(), username)

    fun isEmailAddress(emailAddress: String): Boolean = Pattern.matches(Patterns.EMAIL_ADDRESS.toRegex().toString(), emailAddress)

    class Exceptions {
        class EmptyUsernameEmailAddressException : Exception("Please enter your email address or username to login.")

        class EmptyEmailException : Exception("The email address is empty.")
        class EmptyUsernameException : Exception("The username is empty.")
        class EmptyPhoneNumberException : Exception("The phone number provided is empty.")
        class InvalidEmailException : Exception("The email address is invalid.")
        class InvalidPhoneNumberException : Exception("The phone number is invalid.")
        class InvalidCountryCodeException : Exception("The country code is invalid.")
        class EmptyPasswordException : Exception("The password is empty.")
        class InvalidUsernameException : Exception("The username is invalid.")
        class InvalidUsernameGenerationException : Exception("The username is invalid. The username can only contain characters, numbers, underscores and dots. Eg: grego_team")

        class LengthException : Exception("The password should be at least 6 characters long.")
        class WeakPasswordException : Exception("Please enter a stronger password having combination of letters, numbers and symbols.")
    }

    private val USERNAME_REGEX: Regex = """^(?!.*[.]{2,})(?!.*[_]{2,})[\w.]+$""".toRegex()

}