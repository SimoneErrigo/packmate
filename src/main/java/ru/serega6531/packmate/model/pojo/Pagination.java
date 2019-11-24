package ru.serega6531.packmate.model.pojo;

import lombok.Data;
import org.springframework.data.domain.Sort;
import ru.serega6531.packmate.model.Pattern;

@Data
public class Pagination {

    private Sort.Direction direction;

    private long startingFrom;

    private int pageSize;

    private boolean favorites; // только для стримов, определяет, искать только избранные стримы или все

    private Pattern pattern; // только для стримов, если не null, ищем стримы с этим паттерном

}