/* https://github.com/caio-silva/hibp (C)2023 */
package com.github.caiosilva.hibp.entity;

public enum APIPlan {
	RPM10( 10 ),
	RPM50( 50 ),
	RPM100( 100 ),
	RPM500( 500 );

	private final int requestsPerMinute;

	APIPlan( int requestsPerMinute ) {
		this.requestsPerMinute = requestsPerMinute;
	}

	public int getRequestsPerMinute() {
		return requestsPerMinute;
	}
}
