/* (C)2023 */
package com.github.caiosilva.hibp.executor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.github.caiosilva.hibp.account.APIAccount;
import com.github.caiosilva.hibp.client.HIBPHttpClient;
import com.github.caiosilva.hibp.entity.Breach;
import com.github.caiosilva.hibp.entity.Paste;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.plan.APIPlan;
import com.github.caiosilva.hibp.rateLimit.RateLimiterBuilder;
import com.github.caiosilva.hibp.rateLimit.RateLimiterEntity;
import io.github.bucket4j.Bucket;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Call;
import retrofit2.Response;

@ExtendWith( MockitoExtension.class )
class RateLimiterHttpCallExecutorTest {

    @Mock
    private HIBPHttpClient hibpService;

    @Mock
    private Call<List<Object>> breachCall;

    @Mock
    private Call<List<Object>> pasteCall;

    private MockedStatic<HttpCallExecutor> httpCallExecutorMockedStatic;

    @BeforeEach
    void setup( ) {
        httpCallExecutorMockedStatic = mockStatic( HttpCallExecutor.class );
    }

    @AfterEach
    void cleanup( ) {
        httpCallExecutorMockedStatic.close();
    }

    @ParameterizedTest
    @EnumSource( value = APIPlan.class )
    void getAllPastesForAccount( APIPlan apiPlan ) throws HaveIBeenPwndException {
        final CallWrapper callWrapper = CallWrapper.builder().isGetAllPastesForAccount( true ).account( "account" ).build();
        final APIAccount account = APIAccount.builder().key( "test-key" ).plan( apiPlan ).build();
        final List<RateLimiterEntity> rateLimiter = spy( RateLimiterBuilder.from( account ) );
        final RateLimiterHttpCallExecutor underTest = new RateLimiterHttpCallExecutor( hibpService, rateLimiter );
        List<Paste> expected = Response.success( List.of( Paste.builder().title( "test-response" ).build() ) ).body();
        Optional<List<Paste>> response = Optional.of( expected );

        when( hibpService.getAllPastesForAccount( "test-key", "account" ) ).thenReturn( pasteCall );
        httpCallExecutorMockedStatic.when( ( ) -> HttpCallExecutor.callService( pasteCall ) ).thenReturn( response );

        List<?> result = underTest.execute( callWrapper );

        verify( hibpService ).getAllPastesForAccount( "test-key", "account" );
        httpCallExecutorMockedStatic.verify( ( ) -> HttpCallExecutor.callService( pasteCall ) );

        assertEquals( expected, result );
    }

    @ParameterizedTest
    @EnumSource( APIPlan.class )
    void getAllPastesForAccountDoesNotThrowsHaveIBeenPwndExceptionTooManyRequestsException(
                                                                                            APIPlan apiPlan ) throws HaveIBeenPwndException {
        final CallWrapper callWrapper = CallWrapper.builder().isGetAllPastesForAccount( true ).account( "account" ).build();
        final APIAccount account = APIAccount.builder().key( "test-key" ).plan( apiPlan ).build();
        final List<RateLimiterEntity> rateLimiter = spy( RateLimiterBuilder.from( account ) );
        final RateLimiterHttpCallExecutor underTest = new RateLimiterHttpCallExecutor( hibpService, rateLimiter );

        when( hibpService.getAllPastesForAccount( "test-key", "account" ) ).thenReturn( pasteCall );
        httpCallExecutorMockedStatic.when( ( ) -> HttpCallExecutor.callService( pasteCall ) ).thenThrow( new HaveIBeenPwndException.TooManyRequestsException() );

        int max = apiPlan.getRequestsPerMinute() + 15;
        for ( int i = 0 ; i < max ; i++ ) {
            underTest.execute( callWrapper );
        }

        verify( hibpService, times( apiPlan.getRequestsPerMinute() ) ).getAllPastesForAccount( "test-key", "account" );
        httpCallExecutorMockedStatic.verify(
                ( ) -> HttpCallExecutor.callService( pasteCall ), times( apiPlan.getRequestsPerMinute() ) );
    }

    @ParameterizedTest
    @EnumSource( APIPlan.class )
    void getAllPastesForAccountDoesNotInteract( APIPlan apiPlan ) {
        final CallWrapper callWrapper = CallWrapper.builder().isGetAllPastesForAccount( true ).account( "account" ).build();
        final List<RateLimiterEntity> rateLimiter = mock( List.class );
        final RateLimiterHttpCallExecutor underTest = new RateLimiterHttpCallExecutor( hibpService, rateLimiter );
        final Bucket bucket = mock( Bucket.class );
        final Iterator<RateLimiterEntity> iterator = mock( Iterator.class );
        RateLimiterEntity rateLimiterEntity = mock( RateLimiterEntity.class );

        when( rateLimiter.iterator() ).thenReturn( iterator );
        when( iterator.hasNext() ).thenReturn( true, false );
        when( iterator.next() ).thenReturn( rateLimiterEntity );
        when( rateLimiterEntity.getBucket() ).thenReturn( bucket );
        when( bucket.tryConsume( anyLong() ) ).thenReturn( false );

        int max = apiPlan.getRequestsPerMinute() + 15;
        for ( int i = 0 ; i < max ; i++ ) {
            assertDoesNotThrow(
                    ( ) -> {
                        underTest.execute( callWrapper );
                    } );
        }

        httpCallExecutorMockedStatic.verifyNoInteractions();
        verifyNoInteractions( hibpService );
    }

    @ParameterizedTest
    @EnumSource( APIPlan.class )
    void getAllBreachesForAccount( APIPlan apiPlan ) throws HaveIBeenPwndException {
        final CallWrapper callWrapper = CallWrapper.builder().isGetAllBreachesForAccount( true ).account( "account" ).truncateResponse( false ).includeUnverified( false ).build();
        final APIAccount account = APIAccount.builder().key( "test-key" ).plan( apiPlan ).build();
        final List<RateLimiterEntity> rateLimiter = spy( RateLimiterBuilder.from( account ) );
        final RateLimiterHttpCallExecutor underTest = new RateLimiterHttpCallExecutor( hibpService, rateLimiter );
        List<Breach> expected = Response.success( List.of( Breach.builder().name( "test-response" ).build() ) ).body();
        Optional<List<Breach>> response = Optional.of( expected );

        when( hibpService.getAllBreachesForAccount( "test-key", "account", false, false, null ) ).thenReturn( breachCall );
        httpCallExecutorMockedStatic.when( ( ) -> HttpCallExecutor.callService( breachCall ) ).thenReturn( response );

        List<?> result = underTest.execute( callWrapper );

        verify( hibpService, times( 1 ) ).getAllBreachesForAccount( "test-key", "account", false, false, null );
        httpCallExecutorMockedStatic.verify( ( ) -> HttpCallExecutor.callService( breachCall ) );

        assertEquals( expected, result );
    }

    @ParameterizedTest
    @EnumSource( APIPlan.class )
    void getAllBreachesForAccountDoesNotInteract( APIPlan apiPlan ) {
        final CallWrapper callWrapper = CallWrapper.builder().isGetAllBreachesForAccount( true ).account( "account" ).truncateResponse( false ).includeUnverified( false ).build();
        final List<RateLimiterEntity> rateLimiter = mock( List.class );
        final RateLimiterHttpCallExecutor underTest = new RateLimiterHttpCallExecutor( hibpService, rateLimiter );
        final Bucket bucket = mock( Bucket.class );
        final Iterator<RateLimiterEntity> iterator = mock( Iterator.class );
        RateLimiterEntity rateLimiterEntity = mock( RateLimiterEntity.class );

        when( rateLimiter.iterator() ).thenReturn( iterator );
        when( iterator.hasNext() ).thenReturn( true, false );
        when( iterator.next() ).thenReturn( rateLimiterEntity );
        when( rateLimiterEntity.getBucket() ).thenReturn( bucket );
        when( bucket.tryConsume( anyLong() ) ).thenReturn( false );

        int max = apiPlan.getRequestsPerMinute() + 15;
        for ( int i = 0 ; i < max ; i++ ) {
            assertDoesNotThrow(
                    ( ) -> {
                        underTest.execute( callWrapper );
                    } );
        }

        httpCallExecutorMockedStatic.verifyNoInteractions();
        verifyNoInteractions( hibpService );
    }

    @ParameterizedTest
    @EnumSource( APIPlan.class )
    void getAllBreachesForAccountOverLimit( APIPlan apiPlan ) throws HaveIBeenPwndException {
        final CallWrapper callWrapper = CallWrapper.builder().isGetAllBreachesForAccount( true ).account( "account" ).truncateResponse( false ).includeUnverified( false ).build();
        final APIAccount account = APIAccount.builder().key( "test-key" ).plan( apiPlan ).build();
        final List<RateLimiterEntity> rateLimiter = spy( RateLimiterBuilder.from( account ) );
        final RateLimiterHttpCallExecutor underTest = new RateLimiterHttpCallExecutor( hibpService, rateLimiter );
        List<Breach> expected = Response.success( List.of( Breach.builder().name( "test-response" ).build() ) ).body();
        Optional<List<Breach>> response = Optional.of( expected );

        when( hibpService.getAllBreachesForAccount( "test-key", "account", false, false, null ) ).thenReturn( breachCall );
        httpCallExecutorMockedStatic.when( ( ) -> HttpCallExecutor.callService( breachCall ) ).thenReturn( response );

        int max = apiPlan.getRequestsPerMinute() + 15;
        for ( int i = 0 ; i < max ; i++ ) {
            underTest.execute( callWrapper );
        }

        verify( hibpService, times( apiPlan.getRequestsPerMinute() ) ).getAllBreachesForAccount( "test-key", "account", false, false, null );
        httpCallExecutorMockedStatic.verify(
                ( ) -> HttpCallExecutor.callService( breachCall ), times( apiPlan.getRequestsPerMinute() ) );
    }

    @ParameterizedTest
    @EnumSource( value = APIPlan.class )
    void getAllBreachesForAccountDoesNotThrowsHaveIBeenPwndExceptionTooManyRequestsException(
                                                                                              APIPlan apiPlan ) throws HaveIBeenPwndException {
        final CallWrapper callWrapper = CallWrapper.builder().isGetAllBreachesForAccount( true ).account( "account" ).truncateResponse( false ).includeUnverified( false ).build();
        final APIAccount account = APIAccount.builder().key( "test-key" ).plan( apiPlan ).build();
        final List<RateLimiterEntity> rateLimiter = spy( RateLimiterBuilder.from( account ) );
        final RateLimiterHttpCallExecutor underTest = new RateLimiterHttpCallExecutor( hibpService, rateLimiter );

        when( hibpService.getAllBreachesForAccount( "test-key", "account", false, false, null ) ).thenReturn( breachCall );
        httpCallExecutorMockedStatic.when( ( ) -> HttpCallExecutor.callService( breachCall ) ).thenThrow( new HaveIBeenPwndException.TooManyRequestsException() );

        int max = apiPlan.getRequestsPerMinute() + 15;
        for ( int i = 0 ; i < max ; i++ ) {
            underTest.execute( callWrapper );
        }

        verify( hibpService, times( apiPlan.getRequestsPerMinute() ) ).getAllBreachesForAccount( "test-key", "account", false, false, null );
        httpCallExecutorMockedStatic.verify(
                ( ) -> HttpCallExecutor.callService( breachCall ), times( apiPlan.getRequestsPerMinute() ) );
    }
}
