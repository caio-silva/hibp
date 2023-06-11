/* (C)2023 */
package com.github.caiosilva.hibp.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.caiosilva.hibp.account.APIAccount;
import com.github.caiosilva.hibp.plan.APIPlan;
import java.net.Proxy;
import org.junit.jupiter.api.Test;

class HaveIBeenPwndBuilderTest {

    @Test
    void create( ) {
        final APIAccount apiAccount = APIAccount.builder().key( "key" ).plan( APIPlan.RPM10 ).build();
        final String userAgent = "user-agent";
        final String haveIBeenPwndUrl = "https://www.HaveIBeenPwndUrl.com";
        final String pwndPasswordsUrl = "https://www.PwndPasswordsUrl.com";
        final boolean addPadding = true;
        Proxy proxy = mock( Proxy.class );

        HaveIBeenPwndBuilder underTest = spy( HaveIBeenPwndBuilder.create() );
        assertNotNull( underTest );

        underTest.withAccount( apiAccount );
        underTest.withUserAgent( userAgent );
        underTest.withHaveIBeenPwndUrl( haveIBeenPwndUrl );
        underTest.withPwndPasswordsUrl( pwndPasswordsUrl );
        underTest.addPadding( addPadding );
        underTest.withProxy( proxy );

        HaveIBeenPwndApi underTestAPI = underTest.build();

        assertNotNull( underTestAPI );
    }
}
