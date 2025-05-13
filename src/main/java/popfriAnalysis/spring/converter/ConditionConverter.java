package popfriAnalysis.spring.converter;

import popfriAnalysis.spring.web.dto.ConditionRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ConditionConverter {
    public static String conditionDtoToSQL(ConditionRequest.ConditionDto dto){
        StringBuilder builder = new StringBuilder();
        builder.append(dto.getValue());

        if (!dto.getCondition().isEmpty()) {
            String orGroup = dto.getCondition().stream()
                    .map(ConditionConverter::conditionDtoToSQL)
                    .filter(clause -> !clause.isBlank())
                    .collect(Collectors.joining(" OR ", " AND (", ")"));
            builder.append(orGroup);
        }

        return builder.toString();
    }
    public static List<String> conditionDtoToConditionValue(List<ConditionRequest.ConditionDto> dtoList){
        return dtoList.stream()
                .map(ConditionConverter::conditionDtoToSQL)
                .filter(clause -> !clause.isBlank())
                .toList();
    }
}
