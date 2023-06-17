/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.github.caiosilva.hibp.api.Util.makeHash;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.caiosilva.hibp.entity.*;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;

@ExtendWith(MockitoExtension.class)
class HIPBTest {

	private final APIAccount apiAccount;
	private final HIPB underTest;
	private String apiKey;
	private final String PWND_PASSWORD = "password";
	private final String PWND_PASSWORD_HASH = "5BAA61E4C9B93F3F0682250B6CF8331B7EE68FD8";

	HIPBTest() {
		if ( isApiKeyAvailable() ) {
			apiAccount = APIAccount.builder().key( apiKey ).plan( APIPlan.RPM10 ).build();
		} else {
			apiAccount = APIAccount.builder().key( "key" ).plan( APIPlan.RPM10 ).build();
		}

		underTest = HaveIBeenPwndBuilder.create()
				.withAccount( apiAccount )
				.withUserAgent( "something" )
				.addPadding( true )
				.build();
	}

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	@EnabledIf("isApiKeyAvailable")
	void getAllBreachesForAccount() {
		assertDoesNotThrow( () -> {
			List<Breach> adobe = underTest.getAllBreachesForAccount( "adobe" );
			assertTrue( adobe.size() > 0 );
		} );
	}

	@Test
	void getAllBreachedSites() throws HaveIBeenPwndException {
		List<Breach> adobe = underTest.getAllBreaches();
		assertTrue( adobe.size() > 0 );
	}

	@Test
	void getBreachByName() throws HaveIBeenPwndException {
		Optional<Breach> adobe = underTest.getBreachByName( "adobe" );

		assertTrue( adobe.isPresent() );
		Breach breach = adobe.orElse( null );
		assertNotNull( breach );
		assertEquals( "Adobe", breach.getName() );
		assertEquals( "Adobe", breach.getTitle() );
		assertEquals( "adobe.com", breach.getDomain() );
	}

	@Test
	void getAllDataClasses() throws HaveIBeenPwndException {
		List<String> allDataClasses = underTest.getAllDataClasses();
		assertNotNull( allDataClasses );
		assertTrue( allDataClasses.size() > 0 );
	}

	@Test
	@EnabledIf("isApiKeyAvailable")
	void getAllPastesForAccount() {
		assertDoesNotThrow( () -> {
			List<Paste> result = underTest
					.getAllPastesForAccount( "account-exists@hibp-integration-tests.com" );
			assertFalse( result.isEmpty() );
		} );

		// account-exists@hibp-integration-tests.com
		// Returns one breach and one paste.

		//		for (int i = 0; i < 11; i++) {
		//			try {
		//				underTest.getAllPastesForAccount( "account-exists@hibp-integration-tests.com" );
		//			} catch (HaveIBeenPwndException ignore) {
		//			}
		//		}
	}

	@Test
	void searchByRangeFound() throws HaveIBeenPwndException {
		List<PwnedHash> password = underTest.searchByRange( makeHash( PWND_PASSWORD ) );
		assertTrue( password.size() > 0 );
	}

	@Test
	void searchByRangeNotFound() throws HaveIBeenPwndException {
		try ( MockedStatic<Util> utilMockedStatic = mockStatic( Util.class ) ) {
			utilMockedStatic.when( () -> Util.execute( any() ) ).thenReturn( Optional.empty() );

			List<PwnedHash> pass = underTest.searchByRange( "fdsfsfds" );
			assertTrue( pass.isEmpty() );
		}
	}

	@Test
	@EnabledIf("isApiKeyAvailable")
	void isAccountPwned() throws HaveIBeenPwndException {
		//		boolean accountPwned = underTest.isAccountPwned( "caiogustavo15@hotmail.com" );
		//		assertFalse( accountPwned );
	}

	@Test
	void isPlainPasswordPwned() {
		assertDoesNotThrow( () -> assertTrue( underTest.isPlainPasswordPwned( PWND_PASSWORD ) ) );
	}

	@Test
	void isHashPasswordPwned() {
		assertDoesNotThrow(
				() -> assertTrue( underTest.isHashPasswordPwned( PWND_PASSWORD_HASH ) ) );
	}

	@Test
	void validateAPIKey() {
		assertDoesNotThrow( underTest::validateAPIKey );
	}

	@Test
	//	@EnabledIf("isApiKeyAvailable")
	void executeWithRateLimiter() {
		boolean apiKeyAvailable = isApiKeyAvailable();
		assertTrue( apiKeyAvailable );
	}

	private boolean isApiKeyAvailable() {
		apiKey = System.getenv( "HIBP_API_KEY" );

		return apiKey != null && !apiKey.isEmpty();
	}
}
