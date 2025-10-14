package src.main.java.br.com.dio;

import src.main.java.br.com.dio.persistence.migration.MigrationStrategy;
import src.main.java.br.com.dio.ui.MainMenu;

import java.sql.SQLException;

import static src.main.java.br.com.dio.persistence.config.ConnectionConfig.getConnection;


public class Main {
    public static void main(String[] args) throws SQLException {
        try(var connection = getConnection()){
            new MigrationStrategy(connection).executeMigration();
        }
        new MainMenu().execute();
    }
}

