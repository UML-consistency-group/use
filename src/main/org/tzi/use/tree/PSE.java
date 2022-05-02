package org.tzi.use.tree;

import java.util.Objects;

/**
 * @author: zlyang
 * @date: 2022-05-01 15:29
 * @description:
 */
public class PSE {

    private OptType optType;

    private String target;

    private String context;

    public void setOptType(OptType optType) {
        this.optType = optType;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public OptType getOptType() {
        return optType;
    }

    public String getTarget() {
        return target;
    }

    public String getContext() {
        return context;
    }

    public PSE(OptType optType, String target, String context) {
        this.optType = optType;
        this.target = target;
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PSE pse = (PSE) o;
        return optType == pse.optType && Objects.equals(target, pse.target) && Objects.equals(context, pse.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optType, target, context);
    }
}
