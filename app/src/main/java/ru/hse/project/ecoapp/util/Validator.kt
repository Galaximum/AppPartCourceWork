package ru.hse.project.ecoapp.util

import android.util.Patterns
import ru.hse.project.ecoapp.R
import java.util.regex.Pattern

class Validator {
    companion object {

        private val NICKNAME_LAT_PATTERN = Pattern.compile("""^[_A-z-_]*([_A-z])*${'$'}""")
        private val USERNAME_KIR_PATTERN = Pattern.compile("""^[_А-я]*([_А-я])*${'$'}""")
        private val USERNAME_LAT_PATTERN = Pattern.compile("""^[_A-z]*([_A-z])*${'$'}""")
        private val ADDRESS_LAT_PATTERN = Pattern.compile("""^[_A-z0-9,. ]*([_A-z0-9])*${'$'}""")
        private val ADDRESS_KIR_PATTERN = Pattern.compile("""^[_А-я0-9,. ]*([_А-я0-9])*${'$'}""")
        private val TITLE_KIR_PATTERN = Pattern.compile("""^[_А-я "]*([_А-я])*${'$'}""")
        private val TITLE_LAT_PATTERN = Pattern.compile("""^[_A-z "]*([_A-z])*${'$'}""")
        private val PASSWORD_LAT_PATTERN =
            Pattern.compile("""^[_A-z0-9@%#?-_]*([_A-z0-9])*${'$'}""")


        fun nickNameValidation(nickName: String): Int {
            if (nickName.isBlank()) {
                return R.string.invalid_nick_name_is_blank
            }


            if (!NICKNAME_LAT_PATTERN.matcher(nickName).matches()) {
                return R.string.invalid_nick_name_not_pattern
            }

            return 0
        }

        fun userNameValidation(userName: String): Int {
            if (userName.isBlank()) {
                return R.string.invalid_user_name_is_blank
            }

            val kirMatches = USERNAME_KIR_PATTERN.matcher(userName).matches()
            val latMatches = USERNAME_LAT_PATTERN.matcher(userName).matches()

            if (!kirMatches.xor(latMatches)) {
                return R.string.invalid_user_name_not_patterns_or_both
            }

            return 0
        }

        fun emailValidation(email: String): Int {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return R.string.invalid_email_not_pattern
            }
            return 0
        }

        fun passwordValidation(password: String): Int {
            if (password.isBlank()) {
                return R.string.invalid_password_is_blank
            }

            if (password.length < 5) {
                return R.string.invalid_password_too_short
            }

            if (!PASSWORD_LAT_PATTERN.matcher(password).matches()) {
                return R.string.invalid_password_not_pattern
            }

            return 0
        }

        fun titleValidation(title: String): Int {
            if (title.isBlank()) {
                return R.string.invalid_title_is_blank
            }

            if (title.length < 10) {
                return R.string.invalid_title_too_short
            }

            val kirMatches = TITLE_KIR_PATTERN.matcher(title).matches()
            val latMatches = TITLE_LAT_PATTERN.matcher(title).matches()

            if (!kirMatches.xor(latMatches)) {
                return R.string.invalid_title_not_patterns_or_both
            }


            return 0
        }

        fun addressValidation(address: String): Int {
            if (address.isBlank()) {
                return R.string.invalid_address_is_blank
            }

            if (address.length < 10) {
                return R.string.invalid_address_too_short
            }

            val kirMatches = ADDRESS_KIR_PATTERN.matcher(address).matches()
            val latMatches = ADDRESS_LAT_PATTERN.matcher(address).matches()

            if (!kirMatches.xor(latMatches)) {
                return R.string.invalid_address_not_patterns_or_both
            }


            return 0
        }

    }
}