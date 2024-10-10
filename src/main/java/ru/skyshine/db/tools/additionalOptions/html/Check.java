package ru.skyshine.db.tools.additionalOptions.html;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Check {
    private String id;
    private String forr;
    private String text;

    public Check(String allParam) {
        this(allParam, allParam, allParam);
    }
}