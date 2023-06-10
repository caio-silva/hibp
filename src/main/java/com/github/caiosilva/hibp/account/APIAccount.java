/* (C)2023 */
package com.github.caiosilva.hibp.account;

import com.github.caiosilva.hibp.plan.APIPlan;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class APIAccount {
    private final String key;
    private final APIPlan plan;
}
