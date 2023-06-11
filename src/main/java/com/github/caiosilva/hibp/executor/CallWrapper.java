/* (C)2023 */
package com.github.caiosilva.hibp.executor;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CallWrapper {
    private final String account;
    private final String domain;
    private final boolean truncateResponse;
    private final boolean includeUnverified;
    private final boolean isGetAllPastesForAccount;
    private final boolean isGetAllBreachesForAccount;
}
