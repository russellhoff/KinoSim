package de.outstare.kinosim.movie;

import java.time.Duration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A SimpleMovie implements just {@link Movie}.
 */
public class SimpleMovie implements Movie {

	private final String		title;
	private final Duration		duration;
	private final int			ageRating;
	private final String		distributor;
	private final Set<Genre>	genres;
	private final Rating		rating;

	public SimpleMovie(final String title, final Duration duration, final int ageRating, final String distributor, final Set<Genre> genres,
			final Rating rating) {
		this.title = title;
		this.duration = duration;
		this.ageRating = ageRating;
		this.distributor = distributor;
		this.genres = genres;
		this.rating = rating;

	}

	/**
	 * @see de.outstare.kinosim.movie.Movie#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * @see de.outstare.kinosim.movie.Movie#getDuration()
	 */
	@Override
	public Duration getDuration() {
		return duration;
	}

	/**
	 * @see de.outstare.kinosim.movie.Movie#getAgeRating()
	 */
	@Override
	public int getAgeRating() {
		return ageRating;
	}

	/**
	 * @see de.outstare.kinosim.movie.Movie#getDistributor()
	 */
	@Override
	public String getDistributor() {
		return distributor;
	}

	/**
	 * @see de.outstare.kinosim.movie.Movie#getGenres()
	 */
	@Override
	public Set<Genre> getGenres() {
		return genres;
	}

	/**
	 * @see de.outstare.kinosim.movie.Movie#getRating()
	 */
	@Override
	public Rating getRating() {
		return rating;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Movie o) {
		return getTitle().compareTo(o.getTitle());
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final SimpleMovie other = (SimpleMovie) obj;
		return new EqualsBuilder()
				.append(title, other.title)
				.append(duration, other.duration)
				.append(ageRating, other.ageRating)
				.append(distributor, other.distributor)
				.append(genres, other.genres)
				.append(rating, other.rating)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(title)
				.append(duration)
				.append(ageRating)
				.append(distributor)
				.append(genres)
				.append(rating)
				.toHashCode();
	}

	public static Movie createRandom() {
		final Random r = new Random();
		final String aTitle = randomWord(r) + " " + randomWord(r);
		final Duration aDuration = Duration.ofMinutes(60 + r.nextInt(60));
		final int age = 6 + 4 * r.nextInt(4);
		final String aDistributor = randomWord(r);
		final Set<Genre> aGenres = new HashSet<>();
		final int genreCount = 1 + r.nextInt(3);
		for (int i = 0; i < genreCount; i++) {
			Genre randomGenre = Genre.values()[r.nextInt(Genre.values().length)];
			aGenres.add(randomGenre);
		}
		final Rating aRating = Rating.createRandom();
		return new SimpleMovie(aTitle, aDuration, age, aDistributor, aGenres, aRating);
	}

	private static String randomWord(final Random r) {
		return RandomStringUtils.randomAlphabetic(4 + r.nextInt(8));
	}
}
