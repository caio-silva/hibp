/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.api;

import static java.util.Objects.isNull;

import static com.github.caiosilva.hibp.api.Util.*;

import java.net.Proxy;
import java.util.List;
import java.util.Optional;

import com.github.caiosilva.hibp.client.HIBPHttpClient;
import com.github.caiosilva.hibp.client.PwnedPasswordsClient;
import com.github.caiosilva.hibp.entity.Breach;
import com.github.caiosilva.hibp.entity.Paste;
import com.github.caiosilva.hibp.entity.PwnedHash;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.rateLimit.RateLimiter;
import com.github.caiosilva.hibp.rateLimit.RateLimiterEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Slf4j
public class HIPB implements HIPBAPI {

	private final HIBPHttpClient hibpService;
	private final PwnedPasswordsClient ppwService;
	private final boolean addPadding;
	private final RateLimiter rateLimiter;

	@Builder
	public HIPB( String hibpUrl, String ppwUrl, boolean addPadding, String userAgent, Proxy proxy,
			List<RateLimiterEntity> rateLimiterEntity ) {

		OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor( chain -> {
			Request request = chain.request()
					.newBuilder()
					.addHeader( "User-Agent", userAgent )
					.build();
			return chain.proceed( request );
		} );

		if ( proxy != null ) {
			builder.proxy( proxy );
		}

		OkHttpClient client = builder.build();
		Gson gson = new GsonBuilder().setLenient().create();

		Retrofit retrofit = new Retrofit.Builder().baseUrl( hibpUrl )
				.addConverterFactory( GsonConverterFactory.create( gson ) )
				.client( client )
				.build();
		hibpService = retrofit.create( HIBPHttpClient.class );
		retrofit = new Retrofit.Builder().baseUrl( ppwUrl )
				.addConverterFactory( ScalarsConverterFactory.create() )
				.client( client )
				.build();

		ppwService = retrofit.create( PwnedPasswordsClient.class );
		this.addPadding = addPadding;
		rateLimiter = new RateLimiter( rateLimiterEntity );
	}

	public List<Breach> getAllBreachesForAccount( String account ) throws HaveIBeenPwndException {
		return getAllBreachesForAccount( account, null, false, false );
	}

	private List<Breach> getAllBreachesForAccount( String account, String domain,
			boolean truncateResponse, boolean includeUnverified ) throws HaveIBeenPwndException {
		validateAPIKey();
		final String apiKey = rateLimiter.getApiKey();
		return execute( hibpService.getAllBreachesForAccount( apiKey, urlEncode( account ),
				includeUnverified, truncateResponse, domain ) ).orElse( List.of() );
	}

	public List<Breach> getAllBreaches() throws HaveIBeenPwndException {
		return execute( hibpService.getBreaches() ).orElse( List.of() );
	}

	public Optional<Breach> getBreachByName( String breach ) throws HaveIBeenPwndException {
		return execute( hibpService.getBreach( breach ) );
	}

	public List<String> getAllDataClasses() throws HaveIBeenPwndException {
		return execute( hibpService.getDataClasses() ).orElse( List.of() );
	}

	public List<Paste> getAllPastesForAccount( String account ) throws HaveIBeenPwndException {
		validateAPIKey();
		final String apiKey = rateLimiter.getApiKey();
		return execute( hibpService.getAllPastesForAccount( apiKey, account ) ).orElse( List.of() );
	}

	public boolean isAccountPwned( String account ) throws HaveIBeenPwndException {
		validateAPIKey();
		return !getAllBreachesForAccount( account ).isEmpty();
	}

	public boolean isPlainPasswordPwned( String password ) throws HaveIBeenPwndException {
		return isHashPasswordPwned( makeHash( password ) );
	}

	public boolean isHashPasswordPwned( String pwHash ) throws HaveIBeenPwndException {
		String hash5 = pwHash.substring( 0, 5 );

		return searchByRange( pwHash ).stream()
				.filter( hash -> hash.getCount() > 0 )
				.anyMatch( hash -> ( hash5 + hash.getHash() ).equals( pwHash ) );
	}

	public List<PwnedHash> searchByRange( String hash5 ) throws HaveIBeenPwndException {
		if ( hash5.length() > 5 ) {
			hash5 = hash5.substring( 0, 5 );
		}

		String res = execute( ppwService.searchByRange( hash5, addPadding ) ).orElse( "" );
		return pwnedHashesMapper( res );
	}

	public void validateAPIKey() throws HaveIBeenPwndException {
		if ( isNull( rateLimiter ) || !rateLimiter.hasValidKey() )
			throw new HaveIBeenPwndException.NoAPIKeyProvidedException();
	}
}
