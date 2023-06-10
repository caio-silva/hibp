/* (C)2023 */
package com.github.caiosilva.hibp.rateLimit;

import com.github.caiosilva.hibp.account.APIAccount;
import io.github.bucket4j.Bucket;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RateLimiterEntity {
    private final APIAccount apiAccount;
    private final Bucket bucket;
}
