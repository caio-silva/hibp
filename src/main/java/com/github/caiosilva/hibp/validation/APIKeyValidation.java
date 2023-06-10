/* (C)2023 */
package com.github.caiosilva.hibp.validation;

import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;

public interface APIKeyValidation {
    void validateAPIKey() throws HaveIBeenPwndException;
}
