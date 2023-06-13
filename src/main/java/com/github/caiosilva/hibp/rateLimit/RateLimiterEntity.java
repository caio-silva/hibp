/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.rateLimit;

import io.github.bucket4j.Bucket;

import com.github.caiosilva.hibp.entity.APIAccount;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class RateLimiterEntity {
	@NonNull
	private final APIAccount apiAccount;
	@NonNull
	private final Bucket bucket;
}
