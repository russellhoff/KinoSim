package de.outstare.kinosim.schedule.editor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.outstare.kinosim.cinema.CinemaHall;
import de.outstare.kinosim.movie.Movie;
import de.outstare.kinosim.schedule.AdBlock;
import de.outstare.kinosim.schedule.Schedule;
import de.outstare.kinosim.schedule.Show;
import de.outstare.kinosim.util.TimeRange;

/**
 * A ScheduleEditor modifies {@link Show}s of a {@link Schedule}. It can manipulate a schedule by moving {@link Movie}s between {@link CinemaHall}s on
 * a timeline.
 */
public class ScheduleEditor {
	private static final Logger LOG = LoggerFactory.getLogger(ScheduleEditor.class);

	private final Schedule schedule;
	private final Collection<? extends CinemaHall> availableHalls;
	private final Collection<? extends Movie> availableMovies;
	private final Set<ScheduleChangeListener> listeners = new HashSet<>();

	public ScheduleEditor(final Schedule schedule, final Collection<? extends CinemaHall> availableHalls,
			final Collection<? extends Movie> availableMovies) {
		this.schedule = schedule;
		this.availableHalls = availableHalls;
		this.availableMovies = availableMovies;
	}

	public void add(final CinemaHall hall, final Movie film, final LocalTime start) {
		add(new Show(start, film, hall, AdBlock.NONE, 0));
	}

	public void add(final Show show) {
		LOG.info("adding show to schedule: {}", show);
		schedule.add(show);
		changed();
	}

	public void remove(final Show show) {
		LOG.info("removing show from schedule: {}", show);
		schedule.remove(show);
		changed();
	}

	private void replace(final Show oldShow, final Show newShow) {
		remove(oldShow);
		add(newShow);
	}

	public void moveToTimeInHall(final Show show, final LocalTime start, final CinemaHall hall) {
		final Show newShow = show.copyWithOtherHall(hall);
		newShow.setStart(start);
		replace(show, newShow);
		changed();
	}

	public Show setAds(final Show show, final AdBlock ads) {
		final Show newShow = show.copyWithOtherAds(ads);
		replace(show, newShow);
		return newShow;
	}

	public Show setBreak(final Show show, final int breakDurationInMinutes) {
		final Show newShow = show.copyWithOtherBreak(breakDurationInMinutes);
		replace(show, newShow);
		return newShow;
	}

	public SortedSet<CinemaHall> getAvailableHalls() {
		return new TreeSet<>(availableHalls);
	}

	public SortedSet<Movie> getAvailableMovies() {
		return new TreeSet<>(availableMovies);
	}

	public Schedule getHallSchedule(final CinemaHall hall) {
		return schedule.filterForHall(hall);
	}

	public void addChangeListener(final ScheduleChangeListener listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(final ScheduleChangeListener listener) {
		listeners.remove(listener);
	}

	private void changed() {
		for (final ScheduleChangeListener listener : listeners) {
			listener.scheduleChanged();
		}
	}

	public boolean isFree(final CinemaHall hallForNewMovies, final LocalTime startTime, final Duration duration) {
		final TimeRange newRange = new TimeRange(startTime, duration);
		return schedule.isFree(hallForNewMovies, newRange);
	}

	public boolean isFreeFor(final CinemaHall hallForNewMovies, final LocalTime startTime, final Show show) {
		return schedule.isFreeFor(hallForNewMovies, startTime, show);
	}
}
