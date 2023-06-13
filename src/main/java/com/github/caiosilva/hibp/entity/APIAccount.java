/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class APIAccount {
	private final String key;
	private final APIPlan plan;
}
