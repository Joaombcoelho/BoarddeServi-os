package src.main.java.br.com.dio.dto;

import src.main.java.br.com.dio.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind){
}
