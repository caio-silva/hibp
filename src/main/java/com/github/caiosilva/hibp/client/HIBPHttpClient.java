/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.client;

import java.util.List;

import com.github.caiosilva.hibp.entity.Breach;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HIBPHttpClient {

	@GET("breachedaccount/{account}")
	Call<List<Object>> getAllBreachesForAccount( @Header("hibp-api-key") String apiKey,
			@Path(value = "account") String account,
			@Query("includeUnverified") boolean includeUnverified,
			@Query("truncateResponse") boolean truncateResponse, @Query("domain") String domain );

	@GET("breaches")
	Call<List<Breach>> getBreaches();

	@GET("breach/{name}")
	Call<Breach> getBreach( @Path(value = "name") String name );

	@GET("dataclasses")
	Call<List<String>> getDataClasses();

	@GET("pasteaccount/{account}")
	Call<List<Object>> getAllPastesForAccount( @Header("hibp-api-key") String apiKey,
			@Path(value = "account") String account );
}
