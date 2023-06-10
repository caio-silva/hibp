/* (C)2023 */
package com.github.caiosilva.hibp.rateLimit;

import com.github.caiosilva.hibp.account.APIAccount;
import com.github.caiosilva.hibp.plan.APIPlan;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RateLimiterBuilder {

    public static List<RateLimiterEntity> from(List<APIAccount> accounts) {
        return from(accounts.toArray(new APIAccount[0]));
    }

    public static List<RateLimiterEntity> from(APIAccount... accounts) {
        List<RateLimiterEntity> entities = new ArrayList<>();

        for (APIAccount account : accounts) {
            entities.add(
                    RateLimiterEntity.builder()
                            .apiAccount(account)
                            .bucket(createBucket(account.getPlan()))
                            .build());
        }

        return entities;
    }

    public static Bucket createBucket(APIPlan apiPlan) {
        Refill refill = Refill.intervally(apiPlan.getRequestsPerMinute(), Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(apiPlan.getRequestsPerMinute(), refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
