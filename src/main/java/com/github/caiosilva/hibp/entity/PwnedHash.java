/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.entity;

import lombok.Data;

@Data
public class PwnedHash {

	private final String hash;
	private final int count;

	public PwnedHash( String hash, int count ) {
		this.hash = hash;
		this.count = count;
	}

	@Override
	public String toString() {
		return hash + ":" + count;
	}
}
