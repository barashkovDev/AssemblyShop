package ru.skyshine.db.tools.storageClasses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ParamMap {
    private List<String> paramName;
    private List<String> paramRef;

    public ParamMap(ParamMap other) {
        this.paramName = new ArrayList<>(other.paramName);
        this.paramRef = new ArrayList<>(other.paramRef);
    }

    public void add(String paramName, String paramRef) {
        this.paramName.add(paramName);
        this.paramRef.add(paramRef);
    }

    public void add(List<String> paramName, List<String> paramRef) {
        this.paramName.addAll(paramName);
        this.paramRef.addAll(paramRef);
    }

    public String getByParamName(String paramRef) {
        return this.paramName.get(this.paramRef.indexOf(paramRef));
    }

    public String getByParamRef(String paramName) {
        return this.paramRef.get(this.paramName.indexOf(paramName));
    }
}

