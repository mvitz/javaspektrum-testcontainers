package de.mvitz.js.tc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class AuthorRepository {

    private static final String INSERT_STATEMENT =
            "INSERT INTO authors (name) VALUES (?)";
    private static final String READ_ALL_STATEMENT =
            "SELECT id, name FROM authors";

    private final Connection connection;

    public AuthorRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Author author) throws SQLException {
        try (PreparedStatement pstmt = connection
                .prepareStatement(INSERT_STATEMENT, RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, author.getName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Could not save author");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    author.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Could not save author");
                }
            }
        }
    }

    public List<Author> findAll() throws SQLException {
        final List<Author> authors = new ArrayList<>();
        try (PreparedStatement pstmt = connection
                .prepareStatement(READ_ALL_STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Author author = new Author(rs.getString("name"));
                author.setId(rs.getLong("id"));
                authors.add(author);
            }
        }
        return authors;
    }
}
