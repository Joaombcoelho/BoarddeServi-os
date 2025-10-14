package src.main.java.br.com.dio.service;

import lombok.AllArgsConstructor;
import src.main.java.br.com.dio.persistence.dao.CardDAO;
import src.main.java.br.com.dio.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class CardService {
    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}

