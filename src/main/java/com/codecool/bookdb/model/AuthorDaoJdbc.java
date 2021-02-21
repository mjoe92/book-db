package com.codecool.bookdb.model;

import com.codecool.bookdb.manager.BookDbManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDaoJdbc implements AuthorDao {

    private final DataSource source;

    public AuthorDaoJdbc(DataSource source) {
        this.source = source;
    }

    @Override
    public void add(Author author) {
        try(Connection conn = source.getConnection()) {
            String sql = String.format("INSERT INTO author (first_name, last_name, birth_date) VALUES (%s, %s, %s)",
                    author.getFirstName(), author.getLastName(), author.getBirthDate());
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //st.setString(1, author.getFirstName());
            //st.setString(2, author.getLastName());
            //st.setDate(3, author.getBirthDate());
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            rs.next(); // Read next returned value - in this case the first one. See ResultSet docs for more explaination
            author.setId(rs.getInt(1));

        } catch (SQLException ex) {
            throw new RuntimeException("Error while adding new Author.", ex);
        }
    }

    @Override
    public void update(Author author) {
        try (Connection conn = source.getConnection()) {
            String sql = String.format(
                    "UPDATE author SET first_name = %s, last_name = %s, birth_date = %s WHERE id = %d",
                    author.getFirstName(), author.getLastName(), author.getBirthDate(), author.getId());
            PreparedStatement st = conn.prepareStatement(sql);
            //st.setString(1, author.getFirstName());
            //st.setString(2, author.getLastName());
            //st.setDate(3, author.getBirthDate());
            //st.setInt(4, author.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Author get(int id) {
        try (Connection conn = source.getConnection()) {
            String sql = String.format("SELECT first_name, last_name, birth_date FROM author WHERE id = %d", id);
            PreparedStatement st = conn.prepareStatement(sql);
            //st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) return null;
            String firstName = rs.getString(1);
            String lastName = rs.getString(2);
            Date date = rs.getDate(3);
            Author author = new Author(firstName, lastName, date);
            author.setId(id);
            return author;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Author> getAll() {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM author ORDER BY id";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            List<Author> list = new ArrayList<>();
            while (rs.next()) {
                String firstName = rs.getString(2);
                String lastName = rs.getString(3);
                Date date = rs.getDate(4);
                Author author = new Author(firstName, lastName, date);
                author.setId(rs.getInt(1));
                list.add(author);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
