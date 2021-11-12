package it.jasonravagli.gym.appmysql;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.jasonravagli.gym.gui.SwingDialogManageCourse;
import it.jasonravagli.gym.gui.SwingDialogManageMember;
import it.jasonravagli.gym.gui.SwingDialogManageSubs;
import it.jasonravagli.gym.gui.SwingGymView;
import it.jasonravagli.gym.logic.GymController;
import it.jasonravagli.gym.mysql.MySqlCourseRepository;
import it.jasonravagli.gym.mysql.MySqlMemberRepository;
import it.jasonravagli.gym.mysql.MySqlRepositoryProvider;
import it.jasonravagli.gym.mysql.MySqlTransactionManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class MySqlGymApp implements Callable<Void> {

	private static final Logger LOGGER = LogManager.getLogger(MySqlGymApp.class);
	private static final String CONN_URL = "jdbc:mysql://localhost:3306/test";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "password";

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "gym";

	public static void main(String[] args) {
		new CommandLine(new MySqlGymApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				Connection connection = DriverManager.getConnection(CONN_URL, USERNAME, PASSWORD);

				MySqlMemberRepository memberRepository = new MySqlMemberRepository(connection);
				MySqlCourseRepository courseRepository = new MySqlCourseRepository(connection);

				MySqlRepositoryProvider repositoryProvider = new MySqlRepositoryProvider(memberRepository,
						courseRepository);
				MySqlTransactionManager transactionManager = new MySqlTransactionManager(connection,
						repositoryProvider);

				GymController controller = new GymController();

				SwingDialogManageMember dialogManageMember = new SwingDialogManageMember(controller);
				SwingDialogManageCourse dialogManageCourse = new SwingDialogManageCourse(controller);
				SwingDialogManageSubs dialogManageSubs = new SwingDialogManageSubs(controller);
				SwingGymView gymView = new SwingGymView(controller, dialogManageMember, dialogManageCourse,
						dialogManageSubs);

				controller.setTransactionManager(transactionManager);
				controller.setView(gymView);

				gymView.setVisible(true);
			} catch (Exception e) {
				LOGGER.error("Application terminated due to exception: {}", e.getMessage());
			}
		});
		return null;
	}

}
