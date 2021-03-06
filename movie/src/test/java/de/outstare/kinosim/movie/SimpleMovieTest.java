package de.outstare.kinosim.movie;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class SimpleMovieTest {

	@Test
	public void testGetTimeSinceRelease() {
		final LocalDate now = LocalDate.now();
		LocalDate release;
		release = now;
		assertEquals(0, new SimpleMovie(null, null, 0, null, null, null, release).getWeeksSinceRelease(now));

		release = LocalDate.now().minusDays(6);
		assertEquals(0, new SimpleMovie(null, null, 0, null, null, null, release).getWeeksSinceRelease(now));

		release = LocalDate.now().minusDays(7);
		assertEquals(1, new SimpleMovie(null, null, 0, null, null, null, release).getWeeksSinceRelease(now));

		release = LocalDate.now().minusDays(8);
		assertEquals(1, new SimpleMovie(null, null, 0, null, null, null, release).getWeeksSinceRelease(now));

		release = LocalDate.now().minusDays(13);
		assertEquals(1, new SimpleMovie(null, null, 0, null, null, null, release).getWeeksSinceRelease(now));

		release = LocalDate.now().minusDays(14);
		assertEquals(2, new SimpleMovie(null, null, 0, null, null, null, release).getWeeksSinceRelease(now));

		// future
		release = LocalDate.now().plusDays(14);
		assertEquals(-2, new SimpleMovie(null, null, 0, null, null, null, release).getWeeksSinceRelease(now));
	}

}
