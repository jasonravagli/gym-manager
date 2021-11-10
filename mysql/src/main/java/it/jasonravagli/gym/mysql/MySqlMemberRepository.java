package it.jasonravagli.gym.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.jasonravagli.gym.logic.MemberRepository;
import it.jasonravagli.gym.model.Member;

public class MySqlMemberRepository implements MemberRepository {

	private static final String TABLE_NAME = "members";
	private static final String QUERY_FIND_ALL = "SELECT BIN_TO_UUID(id) as uuid, name, surname, date_of_birth FROM "
			+ TABLE_NAME + " ORDER BY surname";
	private static final String QUERY_FIND_BY_ID = "SELECT BIN_TO_UUID(id) as uuid, name, surname, date_of_birth FROM "
			+ TABLE_NAME + " WHERE BIN_TO_UUID(id) = ? ORDER BY surname";
	private static final String QUERY_INSERT = "INSERT INTO " + TABLE_NAME
			+ "(id, name, surname, date_of_birth) VALUES(UUID_TO_BIN(?),?,?,?)";
	private static final String QUERY_UPDATE = "UPDATE " + TABLE_NAME
			+ " SET name=?, surname=?, date_of_birth=? WHERE BIN_TO_UUID(id)=?";
	private static final String QUERY_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE BIN_TO_UUID(id)=?";

	private Connection connection;

	public MySqlMemberRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public List<Member> findAll() throws SQLException {
		PreparedStatement stat = connection.prepareStatement(QUERY_FIND_ALL);
		ResultSet rs = stat.executeQuery();

		List<Member> members = new ArrayList<>();
		while (rs.next()) {
			members.add(createMemberFromResultSet(rs));
		}

		return members;
	}

	@Override
	public void save(Member member) throws SQLException {
		PreparedStatement stat = connection.prepareStatement(QUERY_INSERT);
		stat.setString(1, member.getId().toString());
		stat.setString(2, member.getName());
		stat.setString(3, member.getSurname());
		stat.setObject(4, member.getDateOfBirth());
		stat.executeUpdate();
	}

	@Override
	public Member findById(UUID id) throws SQLException {
		PreparedStatement stat = connection.prepareStatement(QUERY_FIND_BY_ID);
		stat.setString(1, id.toString());
		ResultSet rs = stat.executeQuery();

		if (rs.next())
			return createMemberFromResultSet(rs);

		return null;
	}

	@Override
	public void deleteById(UUID id) throws SQLException {
		PreparedStatement stat = connection.prepareStatement(QUERY_DELETE);
		stat.setString(1, id.toString());
		stat.executeUpdate();
	}

	@Override
	public void update(Member member) throws SQLException {
		PreparedStatement stat = connection.prepareStatement(QUERY_UPDATE);
		stat.setString(1, member.getName());
		stat.setString(2, member.getSurname());
		stat.setObject(3, member.getDateOfBirth());
		stat.setString(4, member.getId().toString());
		stat.executeUpdate();
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
