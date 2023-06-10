/* (C)2023 */
package com.github.caiosilva.hibp.validation;

import static java.util.Objects.isNull;

import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import retrofit2.Response;

public interface ResponseValidation {
    static void validate(Response<?> res) throws HaveIBeenPwndException {
        if (!res.isSuccessful()) {
            switch (res.code()) {
                case 400:
                    throw new HaveIBeenPwndException.BadRequestException();
                case 401:
                    throw new HaveIBeenPwndException.UnauthorizedException();
                case 403:
                    throw new HaveIBeenPwndException.ForbiddenException();
                case 404:
                    throw new HaveIBeenPwndException.NotFoundException();
                case 429:
                    throw new HaveIBeenPwndException.TooManyRequestsException();
                case 503:
                    final String msg = !isNull(res.body()) ? res.body().toString() : "";
                    throw new HaveIBeenPwndException.ServiceUnavailableException(msg);
                default:
                    throw new HaveIBeenPwndException.UnknownErrorCodeException(res.code());
            }
        }
    }
}
