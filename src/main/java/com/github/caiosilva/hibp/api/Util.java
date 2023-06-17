/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.api;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import com.github.caiosilva.hibp.entity.PwnedHash;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.validation.ResponseValidation;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;

@UtilityClass
@Slf4j
public class Util {

	public static <T> Optional<T> execute( Call<T> call ) throws HaveIBeenPwndException {
		try {
			logger.info( "Before making call :" + call );
			Response<T> res = call.execute();
			ResponseValidation.validate( res );
			logger.info( "Call was successful :" + call );
			return ofNullable( res.body() );
		} catch ( HaveIBeenPwndException.TooManyRequestsException e ) {
			logger.error( "TooManyRequestsException :" + call );
			throw new HaveIBeenPwndException.TooManyRequestsException();
		} catch ( HaveIBeenPwndException.NotFoundException e ) {
			return empty();
		} catch ( IOException e ) {
			throw new HaveIBeenPwndException.IOException( e.getMessage(), e );
		}
	}

	public static String makeHash( String password ) {
		return DigestUtils.sha1Hex( password ).toUpperCase();
	}

	public static String urlEncode( String toEncode ) {
		return URLEncoder.encode( toEncode, StandardCharsets.UTF_8 );
	}

	public static List<PwnedHash> pwnedHashesMapper( String candidate ) {

		return Arrays.stream( candidate.split( "\n" ) )
				.map( line -> line.replace( "\r", "" ).split( ":" ) )
				.filter( arr -> arr.length == 2 )
				.map( parts -> new PwnedHash( parts[0], Integer.parseInt( parts[1] ) ) )
				.collect( toList() );
	}

}
