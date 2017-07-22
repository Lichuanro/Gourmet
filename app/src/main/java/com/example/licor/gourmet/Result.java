package com.example.licor.gourmet;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by Licor on 2017/5/30.
 */

public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private float value;
    public Result(String name, float value) {
        this.name = name;
        this.value = value;
    }

    public Result() {}

    public String getValue() {
        DecimalFormat decimalFormat=new DecimalFormat("##0.00");
        return decimalFormat.format(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
