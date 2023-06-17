/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.github.caiosilva.hibp.entity.PwnedHash;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;

import retrofit2.Call;
import retrofit2.Response;

class UtilTest {
	private final Call CALL = mock( Call.class );
	private final Response RESPONSE = mock( Response.class );

	@Nested
	class Execute {
		@Test
		void execute() throws IOException {
			final MockedStatic<HIPB> hipbMockedStatic = mockStatic( HIPB.class );

			when( CALL.execute() ).thenReturn( RESPONSE );
			when( RESPONSE.isSuccessful() ).thenReturn( true );
			when( RESPONSE.body() ).thenReturn( "string" );

			assertDoesNotThrow( () -> {
				hipbMockedStatic.when( () -> Util.execute( CALL ) )
						.thenReturn( Optional.of( RESPONSE ) );
			} );
		}

		@Test
		void executeThrows() throws IOException {

			when( CALL.execute() ).thenReturn( RESPONSE );
			when( RESPONSE.isSuccessful() ).thenReturn( false );

			when( RESPONSE.code() ).thenReturn( 400 );
			assertThrows( HaveIBeenPwndException.BadRequestException.class,
					() -> Util.execute( CALL ) );

			when( RESPONSE.code() ).thenReturn( 401 );
			assertThrows( HaveIBeenPwndException.UnauthorizedException.class,
					() -> Util.execute( CALL ) );

			when( RESPONSE.code() ).thenReturn( 403 );
			assertThrows( HaveIBeenPwndException.ForbiddenException.class,
					() -> Util.execute( CALL ) );

			when( RESPONSE.code() ).thenReturn( 429 );
			assertThrows( HaveIBeenPwndException.TooManyRequestsException.class,
					() -> Util.execute( CALL ) );

			when( RESPONSE.code() ).thenReturn( 503 );
			assertThrows( HaveIBeenPwndException.ServiceUnavailableException.class,
					() -> Util.execute( CALL ) );

			when( RESPONSE.code() ).thenReturn( 999 );
			assertThrows( HaveIBeenPwndException.UnknownErrorCodeException.class,
					() -> Util.execute( CALL ) );

			when( CALL.execute() ).thenThrow( IOException.class );
			assertThrows( HaveIBeenPwndException.IOException.class, () -> Util.execute( CALL ) );
		}
	}

	@Nested
	class MakeHash {
		@Test
		void makeHash() {
			String PWND_PASSWORD = "password";
			String result = Util.makeHash( PWND_PASSWORD );
			String PWND_PASSWORD_HASH = "5BAA61E4C9B93F3F0682250B6CF8331B7EE68FD8";
			assertEquals( PWND_PASSWORD_HASH, result );
		}
	}

	@Nested
	class PwnedHashesMapper {
		@Test
		void pwnedHashesMapper() {
			PwnedHash pwnedHash1 = new PwnedHash( Util.makeHash( "pass1" ), 0 );
			PwnedHash pwnedHash2 = new PwnedHash( Util.makeHash( "pass1" ), 0 );
			final String candidate1 = pwnedHash1.toString();
			final String candidate2 = pwnedHash2.toString();
			final String candidate = candidate1 + "\n" + candidate2;

			List<PwnedHash> pwnedHashes = Util.pwnedHashesMapper( candidate );
			assertEquals( 2, pwnedHashes.size() );
			assertTrue( pwnedHashes.contains( pwnedHash1 ) );
			assertTrue( pwnedHashes.contains( pwnedHash2 ) );

			assertTrue( Util.pwnedHashesMapper( "" ).isEmpty() );
		}
	}
}
