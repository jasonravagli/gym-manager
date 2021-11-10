package it.jasonravagli.gym.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.jasonravagli.gym.model.Member;

public class MySqlMemberRepositoryIT {

	private static final String CONN_URL = "jdbc:mysql://localhost:3306/test";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "password";
	private static final String TABLE_MEMBERS = "members";
	private static final String INSERT_QUERY = "INSERT INTO " + TABLE_MEMBERS
			+ "(id, name, surname, date_of_birth) VALUES(UUID_TO_BIN(?),?,?,?)";
	private static final String RETRIEVE_QUERY = "SELECT BIN_TO_UUID(id) as uuid, name, surname, date_of_birth FROM "
			+ TABLE_MEMBERS;

	private Connection connection;

	private MySqlMemberRepository repository;

	@Before
	public void setUp() throws Exception {
		connection = DriverManager.getConnection(CONN_URL, USERNAME, PASSWORD);
		// Clean the table
		connection.prepareStatement("DELETE FROM " + TABLE_MEMBERS).executeUpdate();

		repository = new MySqlMemberRepository(connection);
	}

	@After
	public void tearDown() throws Exception {
		if (connection != null && !connection.isClosed())
			connection.close();
	}

	@Test
	public void testFindAllWhenDatabaseIsEmpty() throws SQLException {
		assertThat(repository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() throws SQLException {
		Member member1 = createTestMember("test-name-1", "test-surname-1", LocalDate.of(1996, 4, 30));
		Member member2 = createTestMember("test-name-2", "test-surname-2", LocalDate.of(1996, 10, 31));
		insertMemberIntoDb(member1);
		insertMemberIntoDb(member2);

		assertThat(repository.findAll()).containsExactly(member1, member2);
	}

	@Test
	public void testFindByIdWhenMemberDoesNotExist() throws SQLException {
		assertThat(repository.findById(UUID.randomUUID())).isNull();
	}

	@Test
	public void testFindByIdWhenMemberExists() throws SQLException {
		Member member = createTestMember("test-name-1", "test-surname-1", LocalDate.of(1996, 4, 30));
		insertMemberIntoDb(member);

		assertThat(repository.findById(member.getId())).isEqualTo(member);
	}

	@Test
	public void testSave() throws SQLException {
		Member member = createTestMember("test-name-1", "test-surname-1", LocalDate.of(1996, 4, 30));
		repository.save(member);

		assertThat(readAllMembersFromDatabase()).containsExactly(member);
	}

	@Test
	public void testUpdate() throws SQLException {
		Member existingMember = createTestMember("test-name-1", "test-surname-1", LocalDate.of(1996, 4, 30));
		insertMemberIntoDb(existingMember);
		Member updatedMember = createTestMember("updated-name", "updated-surname", LocalDate.of(1996, 10, 31));
		updatedMember.setId(existingMember.getId());

		repository.update(updatedMember);

		assertThat(readAllMembersFromDatabase()).containsExactly(updatedMember);
	}

	@Test
	public void testDeleteById() throws SQLException {
		Member existingMember = createTestMember("test-name-1", "test-surname-1", LocalDate.of(1996, 4, 30));
		insertMemberIntoDb(existingMember);

		repository.deleteById(existingMember.getId());

		assertThat(readAllMembersFromDatabase()).isEmpty();
	}

	// ----- Utility methods -----

	private Member createTestMember(String name, String surname, LocalDate dateOfBirth) {
		Member member = new Member();
		member.setId(UUID.randomUUID());
		member.setName(name);
		member.setSurname(surname);
		member.setDateOfBirth(dateOfBirth);

		return member;
	}

	private void insertMemberIntoDb(Member member) throws SQLException {
		PreparedStatement stat = connection.prepareStatement(INSERT_QUERY);
		stat.setString(1, member.getId().toString());
		stat.setString(2, member.getName());
		stat.setString(3, member.getSurname());
		stat.setObject(4, member.getDateOfBirth());
		stat.executeUpdate();
	}

	private List<Member> readAllMembersFromDatabase() throws SQLException {
		PreparedStatement stat = connection.prepareStatement(RETRIEVE_QUERY);
		ResultSet rs = stat.executeQuery();

		List<Member> members = new ArrayList<>();
		while (rs.next()) {
			members.add(createMemberFromResultSet(rs));
		}

		return members;
	}

	private Member createMemberFromResultSet(ResultSet rs) throws SQLException {
		UUID uuid = UUID.fromString(rs.getString("uuid"));
		String name = rs.getString("name");
		String surname = rs.getString("surname");
		LocalDate dateOfBirth = rs.getObject("date_of_birth", LocalDate.class);
		Member member = new Member();
		member.setId(uuid);
		member.setName(name);
		member.setSurname(surname);
		member.setDateOfBirth(dateOfBirth);
		return member;
	}

}
