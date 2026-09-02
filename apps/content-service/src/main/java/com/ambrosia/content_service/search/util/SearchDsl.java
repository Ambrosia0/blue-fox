package com.ambrosia.content_service.search.util;

import java.util.List;
import java.util.Set;

public class SearchDsl {
    String baseSql;
    List<Condition> conditions;
    String sort;

    
    class Condition {
        Set<BaseField> baseFields;
        String condition;
        class BaseField {

        }
    }

    class Sort {}
}
