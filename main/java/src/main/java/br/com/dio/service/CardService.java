package src.main.java.br.com.dio.service;

import lombok.AllArgsConstructor;
import src.main.java.br.com.dio.dto.BoardColumnInfoDTO;
import src.main.java.br.com.dio.exception.CardBlockedException;
import src.main.java.br.com.dio.exception.CardFinishedxception;
import src.main.java.br.com.dio.exception.EntityNotFoundException;
import src.main.java.br.com.dio.persistence.dao.CardDAO;
import src.main.java.br.com.dio.persistence.entity.BoardColumnKindEnum;
import src.main.java.br.com.dio.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static src.main.java.br.com.dio.persistence.entity.BoardColumnKindEnum.FINAL;

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

    public void moveToNextColumn(final long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                var message = "O card de id %s está bloqueado e não pode ser movido".formatted(cardId);
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream().filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst().orElseThrow(() -> new IllegalStateException("O card de id %s não pertence a nenhuma das colunas do board"));
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedxception("O card de id %s já está na coluna final e não pode ser movido".formatted(cardId));
            }
            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow();
            dao.moveToColumn(nextColumn.id(), cardId);
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }

    }
}


