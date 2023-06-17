/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.rateLimit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import com.github.caiosilva.hibp.entity.APIAccount;
import com.github.caiosilva.hibp.entity.APIPlan;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RateLimiterBuilder {

	public static List<RateLimiterEntity> from( List<APIAccount> accounts ) {
		return from( accounts.toArray( new APIAccount[0] ) );
	}

	public static List<RateLimiterEntity> from( APIAccount... accounts ) {
		List<RateLimiterEntity> entities = new ArrayList<>();

		for ( APIAccount account : accounts ) {
			entities.add( RateLimiterEntity.builder()
					.apiAccount( account )
					.bucket( createBucket( account.getPlan() ) )
					.build() );
		}

		return entities;
	}

	public static Bucket createBucket( APIPlan apiPlan ) {
		final long maxRequestsPerMinute = apiPlan.getRequestsPerMinute();
		final long timePerRequestInMillis = 60_000 / maxRequestsPerMinute;

		Refill refillPerMin = Refill.intervally( 1,
				Duration.ofMillis( timePerRequestInMillis + 120 ) );
		//		Refill refillPerMin = Refill.intervally(1, Duration.ofSeconds(6));
		Bandwidth limitPerMin = Bandwidth.classic( 1, refillPerMin ).withInitialTokens( 0 );

		return Bucket.builder().addLimit( limitPerMin ).build();
	}
}
