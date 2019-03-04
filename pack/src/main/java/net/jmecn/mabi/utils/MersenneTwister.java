package net.jmecn.mabi.utils;

public class MersenneTwister {
	/**
	 * Period parameters
	 */
	private static final int N = 624;
	private static final int M = 397;
	/**
	 * constant vector a
	 */
	private static final long MATRIX_A = 0x9908b0dfL;
	/**
	 * most significant w-r bits
	 */
	private static final long UMASK = 0x80000000L;
	/**
	 * least significant r bits
	 */
	private static final long LMASK = 0x7fffffffL;

	/**
	 * the array for the state vector
	 */
	private long[] state;
	private int left = 1;
	private int next = 0;
	private boolean initialized = false;

	public MersenneTwister() {
		this(5489L);
	}

	public MersenneTwister(long seed) {
		state = new long[N];
		init(seed);
	}

	/**
	 * initializes state[N] with a seed
	 * 
	 * @param seed
	 */
	public void init(long seed) {
		state[0] = seed & 0xffffffffL;
		for (int j = 1; j < N; j++) {
			state[j] = (1812433253L * (state[j - 1] ^ (state[j - 1] >> 30)) + j);
			state[j] &= 0xffffffffL;// for >32 bit machines
		}
		left = 1;
		initialized = true;
	}

	/**
	 * generates a random number on [0,0xffffffff]-interval
	 * 
	 * @return
	 */
	public long genrandInt32() {
		long y;

		if (--left == 0)
			nextState();
		y = state[next++];

		/* Tempering */
		y ^= (y >> 11);
		y ^= (y << 7) & 0x9d2c5680L;
		y ^= (y << 15) & 0xefc60000L;
		y ^= (y >> 18);

		return y;
	}

	public void nextState() {
		int p = 0;
		int j;

		/* if init_genrand() has not been called, */
		/* a default initial seed is used */
		if (initialized == false)
			init(5489L);

		left = N;
		next = 0;

		for (j = N - M + 1; --j > 0; p++)
			state[p] = state[p + M] ^ twist(state[p], state[p + 1]);

		for (j = M; --j > 0; p++)
			state[p] = state[p + M - N] ^ twist(state[p], state[p + 1]);

		state[p] = state[p + M - N] ^ twist(state[p], state[0]);
	}

	private static final long mixBits(long u, long v) {
		return (u & UMASK) | (v & LMASK);
	}

	private static final long twist(long u, long v) {
		return (mixBits(u, v) >> 1) ^ ((v & 1) == 1 ? MATRIX_A : 0);
	}

}