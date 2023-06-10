/* (C)2023 */
package com.github.caiosilva.hibp.executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

class HttpCallExecutorTest {
    @Test
    void callService_ResultSentToConsumer( ) throws HaveIBeenPwndException, IOException {
        Call<String> call = mock( Call.class );

        Response<String> response = Response.success( "Response Body" );
        when( call.execute() ).thenReturn( response );

        Consumer<Response<?>> consumer = mock( Consumer.class );

        HttpCallExecutor.callService( call, consumer );

        verify( consumer ).accept( response );
    }

    @Test
    void callService_ResultSentToConsumerThrowsHaveIBeenPwndExceptionIOException( ) throws HaveIBeenPwndException, IOException {
        Call<String> call = mock( Call.class );
        Consumer<Response<?>> consumer = mock( Consumer.class );
        when( call.execute() ).thenThrow( new IOException() );

        assertThrows(
                HaveIBeenPwndException.IOException.class, ( ) -> HttpCallExecutor.callService( call, consumer ) );
    }

    @Test
    void callService_ResultReturned( ) throws HaveIBeenPwndException, IOException {
        Call<String> call = mock( Call.class );

        final String BODY = "Response Body";
        Response<String> response = Response.success( BODY );
        when( call.execute() ).thenReturn( response );

        Optional<String> result = HttpCallExecutor.callService( call );

        assertTrue( result.isPresent() );
        assertEquals( result.get(), BODY );
    }

    @Test
    void callService_ResultReturnedThrowsHaveIBeenPwndExceptionIOException( ) throws HaveIBeenPwndException, IOException {
        Call<String> call = mock( Call.class );
        when( call.execute() ).thenThrow( new IOException() );

        assertThrows(
                HaveIBeenPwndException.IOException.class, ( ) -> HttpCallExecutor.callService( call ) );
    }
}
