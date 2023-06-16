package Controller;

import java.math.BigInteger;
import java.time.Duration;

public enum TimeUnit {
	MILLISECONDS {
		@Override
		public BigInteger transform(BigInteger time) {
			return time.divide(BigInteger.valueOf(1000));
		}

		@Override
		public Duration intoDuration(BigInteger time) {
			return Duration.ofMillis(time.longValue());
		}
	},
	SECONDS {
		@Override
		public BigInteger transform(BigInteger time) {
			return time.divide(BigInteger.valueOf(60));
		}

		@Override
		public Duration intoDuration(BigInteger time) {
			return Duration.ofSeconds(time.longValue());
		}
	},
	MINUTES {
		@Override
		public BigInteger transform(BigInteger time) {
			return time.divide(BigInteger.valueOf(60));
		}

		@Override
		public Duration intoDuration(BigInteger time) {
			return Duration.ofMinutes(time.longValue());
		}
	},
	HOURS {
		@Override
		public BigInteger transform(BigInteger time) {
			return time.divide(BigInteger.valueOf(60));
		}

		@Override
		public Duration intoDuration(BigInteger time) {
			return Duration.ofHours(time.longValue());
		}
	},
	DAYS {
		@Override
		public BigInteger transform(BigInteger time) {
			return time.divide(BigInteger.valueOf(24));
		}

		@Override
		public Duration intoDuration(BigInteger time) {
			return Duration.ofDays(time.longValue());
		}
	};

	public BigInteger transform(BigInteger time) {
		throw new UnsupportedOperationException();
	}

	public Duration intoDuration(BigInteger time) {
		throw new UnsupportedOperationException();
	}
}
