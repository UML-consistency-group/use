package org.tzi.use.tree;

import java.util.function.Function;

/**
 * @author: zlyang
 * @date: 2022-05-01 8:20
 * @description:
 */
public enum ExpType {

    /**
     *
     */
    EQUAL(1, "=", s -> {
        if(s.equals("und")){
            return "+-;+-";
        }
        return "und;und";
    }, s -> s.contains("equal") && !s.contains("less") && !s.contains("greater")),
    ARITHMETIC(2, "+-*/", s -> {
        if(s.equals("und")){
            return "und;und";
        }
        return "+-;+-";
    }, s -> {
        return s.contains("add") || s.contains("sub") || s.contains("mult") || s.contains("div");
    }),
    LESS(3, "<", s -> {
        if(s.equals("und")){
            return "+;-";
        }
        return "und;und";
    }, s -> {
        return s.contains("less");
    }),
    GREATER(4, ">", s -> {
        if(s.equals("und")){
            return "-;+";
        }
        return "und;und";
    }, s -> s.contains("greater")),
    FORALL(5, "forAll", s -> {
        if (s.equals("und")){
            return "+;und";
        }
        return "und;und";
    }, s -> s.contains("ForAll")),
    SELECT(6, "select", s -> {
        if(s.equals("und")){
            return "und;und";
        }
        return s + ";und";
    }, s -> s.contains("Select")),
    SIZE(7, "size", s -> {
        if(s.equals("und")){
            return "und";
        }
        return s;
    }, s -> s.contains("size")),
    ALLINSTANCES(8, "allInstance", s -> {
        return "und";
    }, s -> s.contains("AllInstances"))
    ;

    public static ExpType getOptType(String s){
        for (ExpType value : ExpType.values()) {
            if(value.isSelf.apply(s)){
                return value;
            }
        }
        return null;
    }

    ExpType(int id, String type, Function<String, String> function, Function<String, Boolean> isSelf) {
        this.type = type;
        this.id = id;
        this.prorogation = function;
        this.isSelf = isSelf;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return type;
    }

    public Function<String, String> getProrogation() {
        return prorogation;
    }

    private final String type;

    private final int id;

    private final Function<String, String> prorogation;

    private final Function<String, Boolean> isSelf;
}
