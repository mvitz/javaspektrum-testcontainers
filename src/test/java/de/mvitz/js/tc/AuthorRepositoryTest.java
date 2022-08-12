package de.mvitz.js.tc;

import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AuthorRepositoryTest {

    @ClassRule
    public static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:9.6.12")
                    .withPassword("mysecretpassword");

    @Test
    public void save_should_set_id() throws Exception {
        withConnection(connection -> {
            AuthorRepository sut = new AuthorRepository(connection);
            Author author = new Author("Kevin");

            sut.save(author);

            assertEquals(1, author.getId());
        });
    }

    @Test
    public void findAll_should_contain_saved_author() throws Exception {
        withConnection(connection -> {
            AuthorRepository sut = new AuthorRepository(connection);

            Author author = new Author("Michael");
            sut.save(author);

            List<Author> authors = sut.findAll();
            assertEquals(1, authors.size());
            assertEquals("Michael", authors.get(0).getName());
        });
    }

    private void withConnection(ConnectionConsumer c) throws Exception {
        Connection connection = create();
        setup(connection);
        c.executeWith(connection);
    }

    private Connection create() throws Exception {
        String url = postgres.getJdbcUrl();
        String user = postgres.getUsername();
        String password = postgres.getPassword();
        return DriverManager.getConnection(url, user, password);
    }

    private void setup(Connection c) throws SQLException {
        String dropStmt = "DROP TABLE IF EXISTS authors";
        String createStmt = "CREATE TABLE authors (id SERIAL, name varchar(255))";
        try (PreparedStatement drop = c.prepareStatement(dropStmt);
             PreparedStatement create = c.prepareStatement(createStmt)) {
            drop.execute();
            create.execute();
        }
    }

    private interface ConnectionConsumer {
        void executeWith(Connection connection) throws Exception;
    }
}
