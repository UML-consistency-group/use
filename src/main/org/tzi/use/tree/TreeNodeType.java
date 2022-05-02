package org.tzi.use.tree;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author: zlyang
 * @date: 2022-05-01 7:55
 * @description:
 */
public enum TreeNodeType {

    /**
     *
     */
    ITERATOR(1, "IteratorExp", (type, current) -> {
        return type.getProrogation().apply(current);
    }, s -> {
        return s.contains("Select") || s.contains("ForAll");
    }),
    /**
     *
     */
    OPERATION(2, "OperationCallExp", (type, current) -> {
        return type.getProrogation().apply(current);
    }, s -> {
        return s.contains("ExpStdOp");
    }),
    /**
     *
     */
    ATTRIBUTE(3, "AttributeCallExp", (type, current) -> {
        return "+";
    }, s -> {
        return s.contains("Attr");
    }),
    /**
     *
     */
    VARIABLE(4, "VariableExp", (type, current) -> {
        return "+";
    }, s -> {
        return s.contains("Variable");
    }),
    /**
     *
     */
    NAVIGATION(5, "NavigationCallExp", (type, current) -> {
        return current;
    }, s -> {
        return s.contains("Navigation");
    }),
    CONSTANT(6, "ConstantExp", (type, current) -> {
        return "und";
    }, s -> {
        return s.contains("Const");
    })
    ;

    public static TreeNodeType getTreeNodeType(String s){
        for (TreeNodeType value : TreeNodeType.values()) {
            if(value.isSelf.apply(s)){
                return value;
            }
        }
        return null;
    }

    TreeNodeType(int id, String type, BiFunction<ExpType, String, String> function, Function<String, Boolean> isSelf) {
        this.type = type;
        this.id = id;
        this.prorogation = function;
        this.isSelf = isSelf;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public BiFunction<ExpType, String, String> getProrogation() {
        return prorogation;
    }

    private final String type;

    private final int id;

    private final BiFunction<ExpType, String, String> prorogation;

    private final Function<String, Boolean> isSelf;
}
