package src.main.java.br.com.dio.dto;

import src.main.java.br.com.dio.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id, String name, BoardColumnKindEnum kind, int cardsAmount) {
}
