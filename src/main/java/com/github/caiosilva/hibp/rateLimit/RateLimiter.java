/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.rateLimit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import com.github.caiosilva.hibp.entity.APIAccount;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RateLimiter {
	private final List<RateLimiterEntity> rateLimiters;

	public RateLimiter( List<RateLimiterEntity> rateLimiters ) {
		this.rateLimiters = rateLimiters;
	}

	public String getApiKey() {

		if ( rateLimiters.size() == 1 ) {
			rateLimiters.get( 0 )
					.getBucket()
					.asBlocking()
					.tryConsumeUninterruptibly( 1, Duration.of( 60, ChronoUnit.SECONDS ) );
			logger.warn( "giving key" );
			return rateLimiters.get( 0 ).getApiAccount().getKey();
		}

		Optional<String> apiKey = Optional.empty();
		while ( apiKey.isEmpty() ) {
			apiKey = rateLimiters.stream()
					.filter( rt -> rt.getBucket().tryConsume( 1 ) )
					.findAny()
					.map( RateLimiterEntity::getApiAccount )
					.map( APIAccount::getKey );
		}

		logger.warn( "giving key" );
		return apiKey.get();
	}

	public boolean hasValidKey() {

		if ( rateLimiters == null ) {
			return false;
		}

		return rateLimiters.stream().anyMatch( rt -> !rt.getApiAccount().getKey().isEmpty() );
	}
}
