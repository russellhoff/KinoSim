package de.outstare.kinosim.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.outstare.kinosim.ShowSimulator;
import de.outstare.kinosim.cinema.CinemaHall;
import de.outstare.kinosim.cinema.MovieTheater;
import de.outstare.kinosim.commodities.gui.InventoryBar;
import de.outstare.kinosim.commodities.gui.PurchasingGui;
import de.outstare.kinosim.guests.GuestCalculator;
import de.outstare.kinosim.guests.GuestsDayReport;
import de.outstare.kinosim.guests.gui.GuestsDayReportGui;
import de.outstare.kinosim.guituil.WindowUtil;
import de.outstare.kinosim.housegenerator.AreaMovieTheaterCreator;
import de.outstare.kinosim.movie.Movie;
import de.outstare.kinosim.movie.generator.MovieGenerator;
import de.outstare.kinosim.movie.generator.RandomMovieGenerator;
import de.outstare.kinosim.schedule.AdBlock;
import de.outstare.kinosim.schedule.Schedule;
import de.outstare.kinosim.schedule.ScheduleImpl;
import de.outstare.kinosim.schedule.Show;
import de.outstare.kinosim.schedule.editor.ScheduleEditor;
import de.outstare.kinosim.schedule.editor.gui.SchedulerGui;
import de.outstare.kinosim.util.Randomness;
import de.outstare.kinosim.util.TimeRange;

/**
 * A ShowSimulatorGui shows a {@link ScheduleEditor}, a button for simulating and a {@link GuestsDayReport} for the simulation result.
 */
public class ShowSimulatorGui {
	private final ScheduleEditor editor;
	private final ShowSimulator simulator;
	private final TimeRange editableTime;

	/**
	 * @param editor
	 *            with a schedule
	 * @param simulator
	 *            that shares the same schedule as the editor!
	 */
	public ShowSimulatorGui(final ScheduleEditor editor, final ShowSimulator simulator, final TimeRange editableTime) {
		this.editor = editor;
		this.simulator = simulator;
		this.editableTime = editableTime;
	}

	public JComponent createUi() {
		final JPanel panel = new JPanel();
		final SchedulerGui editorUi = new SchedulerGui(editor, editableTime);
		final JButton simulate = new JButton("simulate");
		simulate.setAction(new AbstractAction("simulate") {
			private static final long serialVersionUID = -219059695783807057L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				simulator.nextDay();

				panel.remove(panel.getComponents().length - 1);
				panel.remove(panel.getComponents().length - 1);
				panel.add(createReportUi());
				panel.add(createBalanceUi());
				panel.validate();
			}
		});
		final JPanel storage = createInventoryUi();

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(editorUi.createUi());
		panel.add(simulate);
		panel.add(storage);
		panel.add(createReportUi());
		panel.add(createBalanceUi());
		return panel;
	}

	private JPanel createInventoryUi() {
		final InventoryBar storageBar = new InventoryBar(simulator.getInventory());
		final JButton purchaseButton = new JButton();
		purchaseButton.setAction(new AbstractAction("Shop") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JDialog dialog = new JDialog();
				dialog.setTitle("Buy goods");
				dialog.setModal(false);
				final JComponent purchaseGui = new PurchasingGui(simulator.getPurchasing()).createUi();
				dialog.setContentPane(purchaseGui);
				dialog.pack();
				dialog.setLocationRelativeTo(null); // center on screen
				dialog.setVisible(true);
			}
		});
		final JPanel panel = new JPanel();
		panel.add(new JLabel("Inventory"));
		panel.add(storageBar.createUi());
		panel.add(purchaseButton);
		return panel;
	}

	private JComponent createBalanceUi() {
		final JTextArea balanceUi = new JTextArea(simulator.getBalance().prettyPrint(), 10, 120);
		balanceUi.setFont(Font.decode(Font.MONOSPACED));
		final JLabel bankAccountLabel = new JLabel(simulator.getBankAccount().prettyPrint());
		if (simulator.getBankAccount().getBalance().getValue() >= 0) {
			bankAccountLabel.setForeground(new Color(0, 175, 0));
		} else {
			bankAccountLabel.setForeground(Color.RED);
		}
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(bankAccountLabel, BorderLayout.NORTH);
		panel.add(new JScrollPane(balanceUi), BorderLayout.CENTER);
		return panel;
	}

	private JComponent createReportUi() {
		return new GuestsDayReportGui(simulator.getReport()).createUi();
	}

	/** Test **/
	public static void main(final String[] args) {
		final Schedule schedule = new ScheduleImpl();
		final MovieTheater theater = new AreaMovieTheaterCreator(Randomness.getGaussianAround(1000)).createTheater();
		final List<CinemaHall> halls = theater.getHalls();
		final List<Movie> movies = generateMovieList();
		final Random r = Randomness.getRandom();
		final int minStartHour = 13;
		final int maxStartHour = 24;
		for (int i = 0; i < 4; i++) {
			final Movie movie = movies.get(r.nextInt(movies.size()));
			final CinemaHall hall = halls.get(r.nextInt(halls.size()));
			LocalTime showStart;
			int loops = 10;
			do {
				showStart = LocalTime.of(minStartHour + r.nextInt(maxStartHour - minStartHour), 0);
				loops--;
			} while (!schedule.isFree(hall, new TimeRange(showStart, movie.getDuration())) && loops > 0);
			schedule.add(new Show(showStart, movie, hall, AdBlock.NONE, 0));
		}
		final ScheduleEditor testEditor = new ScheduleEditor(schedule, halls, movies);
		final ShowSimulator testSimulator = new ShowSimulator(schedule, GuestCalculator.createRandom(), LocalDate.now(), theater);
		final ShowSimulatorGui editorGui = new ShowSimulatorGui(testEditor, testSimulator, TimeRange.of(minStartHour, maxStartHour + 2));
		// creating and showing this application's GUI.
		WindowUtil.showAndClose(editorGui.createUi(), "ScheduleEditorGuiDemo", new Dimension(1000, 700));
	}

	static List<Movie> generateMovieList() {
		final MovieGenerator movieGenerator = new RandomMovieGenerator();
		final List<Movie> movies = new ArrayList<>();
		for (int i = 0; i < 13; i++) {
			movies.add(movieGenerator.generate());
		}
		return movies;
	}
}
