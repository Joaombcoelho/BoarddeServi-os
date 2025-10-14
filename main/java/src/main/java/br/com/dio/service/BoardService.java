package src.main.java.br.com.dio.service;

import lombok.AllArgsConstructor;
import src.main.java.br.com.dio.persistence.dao.BoardColumnDAO;
import src.main.java.br.com.dio.persistence.dao.BoardDAO;
import src.main.java.br.com.dio.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {

    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        try{
            dao.insert(entity);
            for (var column : entity.getBoardColumns()){
                column.setBoard(entity);
                int nextOrder = boardColumnDAO.getNextOrderForBoard(entity.getId());
                column.setOrder(nextOrder);
                boardColumnDAO.insert(column);
            }
            connection.commit();
        }catch(SQLException e){
            connection.rollback();
            throw e;}
        return entity;
    }

    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        try{
            if (!dao.exists(id)) {
                return false;
            }
                dao.delete(id);
                connection.commit();
                return true;
        } catch(SQLException e){
            connection.rollback();
            throw e;
        }
    }
}
