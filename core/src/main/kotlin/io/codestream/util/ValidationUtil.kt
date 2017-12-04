package io.codestream.util

import javax.validation.Validation
import javax.validation.Validator

fun validator() = ValidationUtil.validator

class ValidationUtil {
    companion object {
        val validator: Validator by lazy {
            Validation.buildDefaultValidatorFactory().validator
        }
    }
}