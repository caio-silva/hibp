/* (C)2023 */
package com.github.caiosilva.hibp.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Response;

@ExtendWith( MockitoExtension.class )
class ResponseValidationTest {

    @Test
    @DisplayName( "Validate Response Throws BadRequestException (400)" )
    void validateResponseThrowsBadRequestException( ) {
        Response<?> response = Response.error( 400, ResponseBody.create( MediaType.parse( "application/json" ), "" ) );
        assertThrows(
                HaveIBeenPwndException.BadRequestException.class, ( ) -> ResponseValidation.validate( response ) );
    }

    @Test
    @DisplayName( "Validate Response Throws UnauthorizedException (401)" )
    void validateResponseThrowsUnauthorizedException( ) {
        Response<?> response = Response.error( 401, ResponseBody.create( MediaType.parse( "application/json" ), "" ) );
        assertThrows(
                HaveIBeenPwndException.UnauthorizedException.class, ( ) -> ResponseValidation.validate( response ) );
    }

    @Test
    @DisplayName( "Validate Response Throws ForbiddenException (403)" )
    void validateResponseThrowsForbiddenException( ) {
        Response<?> response = Response.error( 403, ResponseBody.create( MediaType.parse( "application/json" ), "" ) );
        assertThrows(
                HaveIBeenPwndException.ForbiddenException.class, ( ) -> ResponseValidation.validate( response ) );
    }

    @Test
    @DisplayName( "Validate Response Throws NotFoundException (404)" )
    void validateResponseThrowsNotFoundException( ) {
        Response<?> response = Response.error( 404, ResponseBody.create( MediaType.parse( "application/json" ), "" ) );
        assertThrows(
                HaveIBeenPwndException.NotFoundException.class, ( ) -> ResponseValidation.validate( response ) );
    }

    @Test
    @DisplayName( "Validate Response Throws TooManyRequestsException (429)" )
    void validateResponseThrowsTooManyRequestsException( ) {
        Response<?> response = Response.error( 429, ResponseBody.create( MediaType.parse( "application/json" ), "" ) );
        assertThrows(
                HaveIBeenPwndException.TooManyRequestsException.class, ( ) -> ResponseValidation.validate( response ) );
    }

    @Test
    @DisplayName( "Validate Response Throws ServiceUnavailableException (503) with body" )
    void validateResponseThrowsServiceUnavailableExceptionWithBody( ) {
        final String TEST_BODY = "Service unavailable - ";
        Response<?> response = Response.error(
                503, ResponseBody.create( MediaType.parse( "application/json" ), TEST_BODY ) );
        assertThrows(
                HaveIBeenPwndException.ServiceUnavailableException.class, ( ) -> {
                    try {
                        ResponseValidation.validate( response );
                    } catch ( HaveIBeenPwndException.ServiceUnavailableException ex ) {
                        assertEquals( TEST_BODY, ex.getMessage() );
                        throw ex; // Re-throw the exception after the assertion
                    }
                } );
    }

    @Test
    @DisplayName( "Validate Response Throws UnknownErrorCodeException" )
    void validateResponseThrowsUnknownErrorCodeException( ) {
        Response<?> response = Response.error( 999, ResponseBody.create( MediaType.parse( "application/json" ), "" ) );
        assertThrows(
                HaveIBeenPwndException.UnknownErrorCodeException.class, ( ) -> ResponseValidation.validate( response ) );
    }
}
