package src.main.java.br.com.dio.service;

import lombok.AllArgsConstructor;
import src.main.java.br.com.dio.dto.CardDetailsDTO;
import src.main.java.br.com.dio.persistence.dao.CardDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {
    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var dao = new CardDAO(connection);

        return dao.findById(id);
    }
}
