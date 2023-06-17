/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.api;

import static org.junit.jupiter.api.Assertions.*;

import static com.github.caiosilva.hibp.api.Util.makeHash;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;

class HIPBExceptionsTest {
	private final String PWND_PASSWORD = "password";
	private final String PWND_PASSWORD_HASH = "5BAA61E4C9B93F3F0682250B6CF8331B7EE68FD8";

	private final HIPB noAPIKeyUnderTest = HaveIBeenPwndBuilder.create()
			.withUserAgent( "something" )
			.addPadding( true )
			.build();

	@Nested
	class GetAllBreachesForAccount {
		@Test
		void getAllBreachesForAccountThrowsNoAPIKeyProvidedException() {
			assertThrows( HaveIBeenPwndException.NoAPIKeyProvidedException.class,
					() -> noAPIKeyUnderTest.getAllBreachesForAccount( "adobe" ) );
		}
	}

	@Nested
	class GetBreachByName {
		@Test
		void getBreachByNameFound() throws HaveIBeenPwndException {
			boolean result = noAPIKeyUnderTest.getBreachByName( "adobe" ).isPresent();
			assertTrue( result );
		}
	}

	@Nested
	class GetAllDataClasses {
		@Test
		void getAllDataClasses() throws HaveIBeenPwndException, IOException {
			boolean result = noAPIKeyUnderTest.getAllDataClasses().size() > 0;
			assertTrue( result );
		}
	}

	@Nested
	class GetAllPastesForAccount {
		@Test
		void getAllPastesForAccountThrowsNoAPIKeyProvidedException() {
			assertThrows( HaveIBeenPwndException.NoAPIKeyProvidedException.class,
					() -> noAPIKeyUnderTest.getAllPastesForAccount( "adobe" ) );
		}
	}

	@Nested
	class SearchByRange {
		@Test
		void searchByRangeTrue() throws HaveIBeenPwndException, IOException {
			boolean result = noAPIKeyUnderTest.searchByRange( PWND_PASSWORD_HASH ).size() > 0;
			assertTrue( result );
		}
	}

	@Nested
	class IsAccountPwned {
		@Test
		void isAccountPwnedThrows() throws HaveIBeenPwndException {
			assertThrows( HaveIBeenPwndException.NoAPIKeyProvidedException.class,
					() -> noAPIKeyUnderTest.isAccountPwned( "adobe" ) );
		}
	}

	@Nested
	class IsPlainPasswordPwned {
		@Test
		void isPlainPasswordPwnedTrue() throws HaveIBeenPwndException, IOException {
			boolean password = noAPIKeyUnderTest.isPlainPasswordPwned( PWND_PASSWORD );
			assertTrue( password );
		}

		@Test
		void isPlainPasswordPwnedFalse() throws HaveIBeenPwndException, IOException {
			boolean password = noAPIKeyUnderTest
					.isPlainPasswordPwned( PWND_PASSWORD + UUID.randomUUID() );
			assertFalse( password );
		}
	}

	@Nested
	class IsHashPasswordPwned {
		@Test
		void isHashPasswordPwnedTrue() throws HaveIBeenPwndException, IOException {
			boolean password = noAPIKeyUnderTest.isHashPasswordPwned( makeHash( PWND_PASSWORD ) );
			assertTrue( password );
		}

		@Test
		void isHashPasswordPwnedFalse() throws HaveIBeenPwndException, IOException {
			boolean password = noAPIKeyUnderTest
					.isHashPasswordPwned( makeHash( UUID.randomUUID() + PWND_PASSWORD ) );
			assertFalse( password );
		}
	}

	@Nested
	class ValidateAPIKey {
		@Test
		void validateAPIKeyThrowsNoAPIKeyProvidedException() {
			assertThrows( HaveIBeenPwndException.NoAPIKeyProvidedException.class,
					noAPIKeyUnderTest::validateAPIKey );
		}
	}
}
