/* (C)2023 */
package com.github.caiosilva.hibp.api;

import static org.junit.jupiter.api.Assertions.*;

import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HaveIBeenPwndApiExceptionsTest {
    private final String PWND_PASSWORD = "password";
    private final String PWND_PASSWORD_HASH = "5BAA61E4C9B93F3F0682250B6CF8331B7EE68FD8";

    private final HaveIBeenPwndApi noAPIKeyUnderTest =
            HaveIBeenPwndBuilder.create("something").addPadding(true).build();

    @Nested
    class GetAllBreachesForAccount {
        @Test
        void getAllBreachesForAccountThrowsNoAPIKeyProvidedException()
                throws HaveIBeenPwndException {
            assertThrows(
                    HaveIBeenPwndException.NoAPIKeyProvidedException.class,
                    () -> noAPIKeyUnderTest.getAllBreachesForAccount("adobe"));
        }
    }

    @Nested
    class GetAllBreachedSites {
        @Test
        void getAllBreachedSites() throws HaveIBeenPwndException, IOException {
            boolean result = noAPIKeyUnderTest.getAllBreachedSites().size() > 0;
            assertTrue(result);
        }
    }

    @Nested
    class GetBreachByName {
        @Test
        void getBreachByNameFound() throws HaveIBeenPwndException {
            boolean result = noAPIKeyUnderTest.getBreachByName("adobe").isPresent();
            assertTrue(result);
        }

        @Test
        void getBreachByNameThrowsNotFoundException() throws HaveIBeenPwndException {
            assertThrows(
                    HaveIBeenPwndException.NotFoundException.class,
                    () -> noAPIKeyUnderTest.getBreachByName("fanduel"));
        }
    }

    @Nested
    class GetAllDataClasses {
        @Test
        void getAllDataClasses() throws HaveIBeenPwndException, IOException {
            boolean result = noAPIKeyUnderTest.getAllDataClasses().size() > 0;
            assertTrue(result);
        }
    }

    @Nested
    class GetAllPastesForAccount {
        @Test
        void getAllPastesForAccountThrowsNoAPIKeyProvidedException() {
            assertThrows(
                    HaveIBeenPwndException.NoAPIKeyProvidedException.class,
                    () -> noAPIKeyUnderTest.getAllPastesForAccount("adobe"));
        }
    }

    @Nested
    class SearchByRange {
        @Test
        void searchByRangeTrue() throws HaveIBeenPwndException, IOException {
            boolean result = noAPIKeyUnderTest.searchByRange(PWND_PASSWORD_HASH).size() > 0;
            assertTrue(result);
        }
    }

    @Nested
    class IsAccountPwned {
        @Test
        void isAccountPwnedThrows() throws HaveIBeenPwndException {
            assertThrows(
                    HaveIBeenPwndException.NoAPIKeyProvidedException.class,
                    () -> noAPIKeyUnderTest.isAccountPwned("adobe"));
        }
    }

    @Nested
    class IsPlainPasswordPwned {
        @Test
        void isPlainPasswordPwnedTrue() throws HaveIBeenPwndException, IOException {
            boolean password = noAPIKeyUnderTest.isPlainPasswordPwned(PWND_PASSWORD);
            assertTrue(password);
        }

        @Test
        void isPlainPasswordPwnedFalse() throws HaveIBeenPwndException, IOException {
            boolean password =
                    noAPIKeyUnderTest.isPlainPasswordPwned(PWND_PASSWORD + UUID.randomUUID());
            assertFalse(password);
        }
    }

    @Nested
    class IsHashPasswordPwned {
        @Test
        void isHashPasswordPwnedTrue() throws HaveIBeenPwndException, IOException {
            boolean password =
                    noAPIKeyUnderTest.isHashPasswordPwned(
                            noAPIKeyUnderTest.makeHash(PWND_PASSWORD));
            assertTrue(password);
        }

        @Test
        void isHashPasswordPwnedFalse() throws HaveIBeenPwndException, IOException {
            boolean password =
                    noAPIKeyUnderTest.isHashPasswordPwned(
                            noAPIKeyUnderTest.makeHash(PWND_PASSWORD + UUID.randomUUID()));
            assertFalse(password);
        }
    }

    @Nested
    class MakeHash {
        @Test
        void testMakeHash() {
            String result = noAPIKeyUnderTest.makeHash(PWND_PASSWORD);

            assertEquals(PWND_PASSWORD_HASH, result);
        }
    }

    @Nested
    class ValidateAPIKey {
        @Test
        void validateAPIKeyThrowsNoAPIKeyProvidedException() {
            assertThrows(
                    HaveIBeenPwndException.NoAPIKeyProvidedException.class,
                    noAPIKeyUnderTest::validateAPIKey);
        }
    }
}
