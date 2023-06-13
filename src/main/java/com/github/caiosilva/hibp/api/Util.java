/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.api;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import com.github.caiosilva.hibp.entity.PwnedHash;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.validation.ResponseValidation;

import lombok.experimental.UtilityClass;
import retrofit2.Call;
import retrofit2.Response;

@UtilityClass
public class Util {

	public static <T> Optional<T> execute( Call<T> call ) throws HaveIBeenPwndException {
		try {
			Response<T> res = call.execute();
			ResponseValidation.validate( res );
			return ofNullable( res.body() );
		} catch ( IOException e ) {
			throw new HaveIBeenPwndException.IOException( e.getMessage(), e );
		}
	}

	public static String makeHash( String password ) {
		return DigestUtils.sha1Hex( password ).toUpperCase();
	}

	public static List<PwnedHash> pwnedHashesMapper( String candidate ) {

		return Arrays.stream( candidate.split( "\n" ) )
				.map( line -> line.replace( "\r", "" ).split( ":" ) )
				.filter( arr -> arr.length == 2 )
				.map( parts -> new PwnedHash( parts[0], Integer.parseInt( parts[1] ) ) )
				.collect( toList() );
	}

}
