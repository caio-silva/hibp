/* (C)2023 */
package com.github.caiosilva.hibp.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface PwnedPasswordsClient {

    @GET("range/{hash5}")
    Call<String> searchByRange(
            @Path("hash5") String hash5, @Header("Add-Padding") boolean addPadding);
}
