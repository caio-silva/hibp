/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.api;

import java.net.Proxy;
import java.util.List;

import com.github.caiosilva.hibp.entity.APIAccount;
import com.github.caiosilva.hibp.rateLimit.RateLimiterBuilder;
import com.github.caiosilva.hibp.rateLimit.RateLimiterEntity;

public final class HaveIBeenPwndBuilder {

	private static final String HIBP_REST_URL = "https://haveibeenpwned.com/api/v3/";
	private static final String PPW_REST_URL = "https://api.pwnedpasswords.com/";

	private boolean addPadding = false;
	private String haveIbeenPwndUrl = HIBP_REST_URL;
	private String pwndPasswordsUrl = PPW_REST_URL;
	private String userAgent;
	private Proxy proxy = null;
	List<RateLimiterEntity> rateLimiterEntity;

	public static HaveIBeenPwndBuilder create() {
		return new HaveIBeenPwndBuilder();
	}

	public HaveIBeenPwndBuilder withUserAgent( String userAgent ) {
		this.userAgent = userAgent;
		return this;
	}

	public HaveIBeenPwndBuilder withAccount( APIAccount... accounts ) {
		rateLimiterEntity = RateLimiterBuilder.from( accounts );
		return this;
	}

	public HaveIBeenPwndBuilder withHaveIBeenPwndUrl( String url ) {
		this.haveIbeenPwndUrl = url;
		return this;
	}

	public HaveIBeenPwndBuilder withPwndPasswordsUrl( String url ) {
		this.pwndPasswordsUrl = url;
		return this;
	}

	public HaveIBeenPwndBuilder addPadding( boolean addPadding ) {
		this.addPadding = addPadding;
		return this;
	}

	public HaveIBeenPwndBuilder withProxy( Proxy proxy ) {
		this.proxy = proxy;
		return this;
	}

	public HIPB build() {
		HIPB.HIPBBuilder builder = HIPB.builder();
		builder.hibpUrl( haveIbeenPwndUrl );
		builder.ppwUrl( pwndPasswordsUrl );
		builder.addPadding( addPadding );
		builder.userAgent( userAgent );
		builder.rateLimiterEntity( rateLimiterEntity );
		builder.proxy( proxy );

		return builder.build();
	}

	private HaveIBeenPwndBuilder() {
	}
}
