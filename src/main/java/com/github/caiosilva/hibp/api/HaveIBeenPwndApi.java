/* (C)2023 */
package com.github.caiosilva.hibp.api;

import static com.github.caiosilva.hibp.executor.HttpCallExecutor.callService;
import static java.util.Objects.isNull;

import com.github.caiosilva.hibp.client.HIBPHttpClient;
import com.github.caiosilva.hibp.client.PwnedPasswordsClient;
import com.github.caiosilva.hibp.entity.Breach;
import com.github.caiosilva.hibp.entity.Paste;
import com.github.caiosilva.hibp.entity.PwnedHash;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.executor.CallWrapper;
import com.github.caiosilva.hibp.executor.RateLimiterHttpCallExecutor;
import com.github.caiosilva.hibp.rateLimit.RateLimiterEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.codec.digest.DigestUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HaveIBeenPwndApi implements HaveIBeenPwndAPI {

    private final HIBPHttpClient hibpService;
    private final PwnedPasswordsClient ppwService;
    private final boolean addPadding;
    private final RateLimiterHttpCallExecutor rateLimiterHttpCallExecutor;
    private final List<RateLimiterEntity> rateLimiterEntity;

    @Builder
    protected HaveIBeenPwndApi(
                                String hibpUrl, String ppwUrl, boolean addPadding, String userAgent, Proxy proxy, List<RateLimiterEntity> rateLimiterEntity ) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(
                chain -> {
                    Request request = chain.request().newBuilder().addHeader( "User-Agent", userAgent ).build();
                    return chain.proceed( request );
                } );
        if ( proxy != null ) {
            builder.proxy( proxy );
        }
        OkHttpClient client = builder.build();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl( hibpUrl ).addConverterFactory( GsonConverterFactory.create( gson ) ).client( client ).build();
        hibpService = retrofit.create( HIBPHttpClient.class );
        retrofit = new Retrofit.Builder().baseUrl( ppwUrl ).addConverterFactory( ScalarsConverterFactory.create() ).client( client ).build();
        ppwService = retrofit.create( PwnedPasswordsClient.class );
        this.addPadding = addPadding;
        this.rateLimiterEntity = rateLimiterEntity;
        this.rateLimiterHttpCallExecutor = new RateLimiterHttpCallExecutor( hibpService, rateLimiterEntity );
    }

    public List<Breach> getAllBreachesForAccount( String account ) throws HaveIBeenPwndException {
        return getAllBreachesForAccount( account, null, false, false );
    }

    private List<Breach> getAllBreachesForAccount(
                                                   String account, String domain, boolean truncateResponse, boolean includeUnverified ) throws HaveIBeenPwndException {
        validateAPIKey();
        final CallWrapper callWrapper = CallWrapper.builder().isGetAllBreachesForAccount( true ).account( account ).domain( domain ).truncateResponse( truncateResponse ).includeUnverified( includeUnverified ).build();

        return rateLimiterHttpCallExecutor.execute( callWrapper ).stream().map( Breach.class::cast ).collect( Collectors.toList() );
    }

    public List<Breach> getAllBreachedSites( ) throws HaveIBeenPwndException {
        return getAllBreachedSites( null );
    }

    public List<Breach> getAllBreachedSites( String domain ) throws HaveIBeenPwndException {
        return callService( hibpService.getBreaches( domain ) ).orElse( List.of() );
    }

    public Optional<Breach> getBreachByName( String breach ) throws HaveIBeenPwndException {
        return callService( hibpService.getBreach( breach ) );
    }

    public List<String> getAllDataClasses( ) throws HaveIBeenPwndException {
        return callService( hibpService.getDataClasses() ).orElse( List.of() );
    }

    public List<Paste> getAllPastesForAccount( String account ) throws HaveIBeenPwndException {
        validateAPIKey();
        List<Object> execute = rateLimiterHttpCallExecutor.execute(
                CallWrapper.builder().isGetAllPastesForAccount( true ).account( account ).build() );

        return execute.stream().map( Paste.class::cast ).collect( Collectors.toList() );
    }

    public List<PwnedHash> searchByRange( String hash5 ) throws HaveIBeenPwndException {
        if ( hash5.length() > 5 ) {
            hash5 = hash5.substring( 0, 5 );
        }

        String res = callService( ppwService.searchByRange( hash5, addPadding ) ).orElse( "" );
        if ( ! res.isEmpty() ) {
            Stream<String> lines = Arrays.stream( res.split( "\n" ) );
            return lines.map( line -> line.replace( "\r", "" ).split( ":" ) ).map( parts -> new PwnedHash( parts[ 0 ], Integer.parseInt( parts[ 1 ] ) ) ).collect( Collectors.toList() );
        }
        return List.of();
    }

    public boolean isAccountPwned( String account ) throws HaveIBeenPwndException {
        validateAPIKey();
        return ! getAllBreachesForAccount( account ).isEmpty();
    }

    public boolean isPlainPasswordPwned( String password ) throws HaveIBeenPwndException {
        return isHashPasswordPwned( makeHash( password ) );
    }

    public boolean isHashPasswordPwned( String pwHash ) throws HaveIBeenPwndException {
        String hash5 = pwHash.substring( 0, 5 );
        List<PwnedHash> hashes = searchByRange( hash5 ).stream().filter( hash -> hash.getCount() > 0 ).collect( Collectors.toList() );
        return hashes.stream().anyMatch( hash -> ( hash5 + hash.getHash() ).equals( pwHash ) );
    }

    public String makeHash( String password ) {
        return DigestUtils.sha1Hex( password ).toUpperCase();
    }

    @Override
    public void validateAPIKey( ) throws HaveIBeenPwndException {
        if ( isNull( rateLimiterEntity ) || rateLimiterEntity.isEmpty() )
            throw new HaveIBeenPwndException.NoAPIKeyProvidedException();
    }
}
