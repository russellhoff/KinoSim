package de.outstare.kinosim.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimeRangeTest {

	@Test
	public void testNotOverlaps() {
		final TimeRange oneTwo = TimeRange.of(1, 2);
		final TimeRange twoFour = TimeRange.of(2, 4);
		final TimeRange threeFive = TimeRange.of(3, 5);
		final TimeRange oneSix = TimeRange.of(1, 6);
		final TimeRange wrapping1 = TimeRange.of(23, 2);
		final TimeRange wrapping2 = TimeRange.of(22, 1);

		assertNotOverlaps(oneTwo, threeFive);
		assertOverlaps(oneTwo, twoFour);
		assertOverlaps(twoFour, threeFive);
		assertOverlaps(oneSix, twoFour);
		assertOverlaps(oneTwo, wrapping1);
		assertOverlaps(wrapping1, wrapping2);
	}

	private void assertOverlaps(final TimeRange first, final TimeRange second) {
		// test commutativity
		assertTrue(first.overlaps(second));
		assertTrue(second.overlaps(first));
	}

	private void assertNotOverlaps(final TimeRange first, final TimeRange second) {
		// test commutativity
		assertFalse(first.overlaps(second));
		assertFalse(second.overlaps(first));
	}

	@Test
	public void testToHours() {
		final TimeRange oneSix = TimeRange.of(1, 6);
		final TimeRange wrapping = TimeRange.of(23, 2);

		assertEquals(5, oneSix.toHours());
		assertEquals(3, wrapping.toHours());
	}
	
	@Test
	public void testEndsNextDay(){
		LocalTime start1, end1, start2, end2;
		
		start1 = LocalTime.of(10, 17, 5);
		end1 = LocalTime.of(10, 17, 0);
		start2 = LocalTime.of(23, 59, 51);
		end2 = LocalTime.of(0, 2, 29);
		
		TimeRange rngNo = new TimeRange(start1, end1);
		assertFalse(rngNo.endsNextDay());
		TimeRange rngYes = new TimeRange(start2, end2);
		assertTrue(rngYes.endsNextDay());
		
		start1 = null;
		end1 = null;
		start2 = null;
		end2 = null;
		
	}
	
}
