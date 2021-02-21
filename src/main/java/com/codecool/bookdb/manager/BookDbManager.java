package com.codecool.bookdb.manager;

import com.codecool.bookdb.model.*;
import com.codecool.bookdb.view.UserInterface;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.sql.*;

import static com.codecool.bookdb.manager.BookDbManager.ServerInfo.PORT;

public class BookDbManager {

    UserInterface ui;
    AuthorDao authorDao;
    BookDao bookDao;

    public BookDbManager(UserInterface ui) {
        this.ui = ui;
    }

    public enum ServerInfo {
        DB("books"),
        HOST("localhost"),
        PORT("5432"),
        URL("jdbc:postgresql://"),
        USER("postgres"),
        PASSWORD("Monkey92");

        private final String info;

        ServerInfo(String info) {
            this.info = info;
        }
    }

    static final String URL = "jdbc:postgresql://localhost:5432/books";

    private DataSource connect() throws SQLException {

        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setServerName(ServerInfo.HOST.info);
        source.setDatabaseName(ServerInfo.DB.info);
        source.setUser(ServerInfo.USER.info);
        source.setPassword(ServerInfo.PASSWORD.info);
        source.setPortNumber(Integer.parseInt(PORT.info));
        source.setMaxConnections(2);

        System.out.println("Connecting...!");
        source.getConnection().close();
        System.out.println("Connecting is ready!");
        return source;
    }

    public void run() {
        try {
            setup();
        } catch (SQLException throwables) {
            System.err.println("Could not connect to the database.");
            return;
        }

        mainMenu();
    }

    private void setup() throws SQLException {
        DataSource dataSource = connect();
        authorDao = new AuthorDaoJdbc(dataSource);
        bookDao = new BookDaoJdbc(dataSource, authorDao);
    }

    private void mainMenu() {
        boolean running = true;

        while (running) {
            ui.printTitle("Main Menu");
            ui.printOption('a', "Authors");
            ui.printOption('b', "Books");
            ui.printOption('q', "Quit");
/*
            switch (ui.choice("abq")) {
                case 'a' -> new AuthorManager(ui, authorDao).run();
                case 'b' -> new BookManager(ui, bookDao, authorDao).run();
                case 'q' -> running = false;
            }
            */
            switch (ui.choice("abq")) {
                case 'a':
                    new AuthorManager(ui, authorDao).run();
                    break;
                case 'b':
                    new BookManager(ui, bookDao, authorDao).run();
                    break;
                case 'q':
                    running = false;
                    break;
            }

        }
    }

}
