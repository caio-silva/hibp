/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.validation;

import static java.util.Objects.isNull;

import static com.github.caiosilva.hibp.exception.HaveIBeenPwndException.*;

import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException.BadRequestException;

import retrofit2.Response;

public interface ResponseValidation {
	static void validate( Response<?> res ) throws HaveIBeenPwndException {
		if ( !res.isSuccessful() ) {
			switch ( res.code() ) {
			case 400:
				throw new BadRequestException();
			case 401:
				throw new UnauthorizedException();
			case 403:
				throw new ForbiddenException();
			case 404:
				throw new NotFoundException();
			case 429:
				throw new TooManyRequestsException();
			case 503:
				final String msg = !isNull( res.body() ) ? res.body().toString() : "";
				throw new HaveIBeenPwndException.ServiceUnavailableException( msg );
			default:
				throw new UnknownErrorCodeException(
						"Error [" + res.code() + "]: " + res.errorBody() );
			}
		}
	}
}
