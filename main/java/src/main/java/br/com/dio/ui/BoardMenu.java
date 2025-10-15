package src.main.java.br.com.dio.ui;

import lombok.AllArgsConstructor;
import src.main.java.br.com.dio.dto.BoardColumnInfoDTO;
import src.main.java.br.com.dio.persistence.entity.BoardColumnEntity;
import src.main.java.br.com.dio.persistence.entity.BoardEntity;
import src.main.java.br.com.dio.persistence.entity.CardEntity;
import src.main.java.br.com.dio.service.BoardColumnQueryService;
import src.main.java.br.com.dio.service.BoardQueryService;
import src.main.java.br.com.dio.service.CardQueryService;
import src.main.java.br.com.dio.service.CardService;

import java.sql.SQLException;
import java.util.Scanner;

import static src.main.java.br.com.dio.persistence.config.ConnectionConfig.getConnection;
import static src.main.java.br.com.dio.persistence.entity.BoardColumnKindEnum.INITIAL;

@AllArgsConstructor

public class BoardMenu {
    private final BoardEntity entity;
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public void execute() {
        try {
            System.out.printf("Bem vindo ao board %s, selecione a opção desejada\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Visualizar board");
                System.out.println("7 - Visualizar colunas com cards");
                System.out.println("8 - Visualizar card");
                System.out.println("9 - Voltar para o menu anterior");
                System.out.println("10 - Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColunm();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando par ao menu anterior");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida, informe uma opção do menu");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }

    }

    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visializar");
        var selectedCardId = scanner.nextLong();
        try (var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.printf("Status: %s\n", c.blocked() ?
                                        "Bloqueado" : "Ativo");
                                if (c.blocked()) {
                                    System.out.printf("Motivo do bloqueio: %s\n", c.blockReason());
                                    System.out.printf("Data do bloqueio: %s\n", c.blockedAt());
                                    System.out.printf("Quantidade de bloqueios: %s\n", c.blocksAmount());
                                }
                                System.out.printf("Coluna atual: %s\n", c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));
        }


    }

    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Informe o título do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()) {
            new CardService(connection).insert(card);
        }
    }

    private void moveCardToNextColunm() throws SQLException{
        System.out.println("Informe o id do card que deseja mover para a proxima coluna");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        }
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDatails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount());
                });
            });
        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumns = -1L;
        while (!columnsIds.contains(selectedColumns)) {
            System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumns = scanner.nextLong();

        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumns);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\n Descrição: %s", ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
        ;
    }
}
