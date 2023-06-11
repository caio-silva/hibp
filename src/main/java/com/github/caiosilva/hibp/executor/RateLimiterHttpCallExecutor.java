/* (C)2023 */
package com.github.caiosilva.hibp.executor;

import static com.github.caiosilva.hibp.executor.HttpCallExecutor.callService;

import com.github.caiosilva.hibp.client.HIBPHttpClient;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.rateLimit.RateLimiterEntity;
import com.github.caiosilva.hibp.validation.ResponseValidation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class RateLimiterHttpCallExecutor implements ResponseValidation {
    private final HIBPHttpClient hibpService;
    private final List<RateLimiterEntity> rateLimiter;

    public RateLimiterHttpCallExecutor(
                                        HIBPHttpClient hibpService, List<RateLimiterEntity> rateLimiterEntityList ) {
        this.hibpService = hibpService;
        rateLimiter = rateLimiterEntityList;
    }

    public List<Object> execute( CallWrapper callWrapper ) throws HaveIBeenPwndException {
        List<Object> result = new ArrayList<>();
        boolean success = false;
        RateLimiterEntity rateLimiterEntity;
        String key;

        Iterator<RateLimiterEntity> it = rateLimiter.iterator();
        while ( it.hasNext() && ! success ) {
            rateLimiterEntity = it.next();
            if ( rateLimiterEntity.getBucket().tryConsume( 1 ) ) {
                try {
                    key = rateLimiterEntity.getApiAccount().getKey();
                    Optional<List<Object>> response = Optional.empty();
                    if ( callWrapper.isGetAllBreachesForAccount() ) {
                        response = callService(
                                hibpService.getAllBreachesForAccount(
                                        key, callWrapper.getAccount(), callWrapper.isIncludeUnverified(), callWrapper.isTruncateResponse(), callWrapper.getDomain() ) );
                    } else if ( callWrapper.isGetAllPastesForAccount() ) {
                        response = callService(
                                hibpService.getAllPastesForAccount(
                                        key, callWrapper.getAccount() ) );
                    }

                    success = true;

                    if ( response.isPresent() ) {
                        result = response.get();
                    }
                } catch ( HaveIBeenPwndException.TooManyRequestsException e ) {
                }
            }
        }

        return result;
    }
}
