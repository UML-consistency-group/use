package org.tzi.use.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author: zlyang
 * @date: 2022-05-01 7:50
 * @description:
 */
public class TreeNode {

    /**
     * 在use中expression类的原名字
     */
    private String expName;

    /**
     * 对应于论文中的类型
     */
    private TreeNodeType type;

    /**
     * 访问的uml对象名
     */
    private String target;

    /**
     * 当类型为VariableExp时，指示其是否是访问的self
     */
    private boolean isSelf;

    /**
     * TreeNodeType为Operation和ITERATOR时的操作类型
     */
    private ExpType expType;

    /**
     * und, +, -
     */
    private String tag;

    /**
     * 为target不是self时提供的指向select或forAll的引用
     */
    private TreeNode ref;

    private List<TreeNode> children;

    private static Map<TreeNodeType, Function<TreeNode, String>> toStringFun;


    static {
        toStringFun = new HashMap<>();
        toStringFun.put(TreeNodeType.ITERATOR, node -> {
            if(node.getExpType() == ExpType.FORALL){
                return node.getChildren().get(0) + "->forAll(" + node.getTarget() + " | " + node.getChildren().get(1) + ")";
            } else if(node.getExpType() == ExpType.SELECT){
                return node.getChildren().get(0) + "->select(" + node.getTarget() + " | " + node.getChildren().get(1) + ")";
            }
            throw new RuntimeException("Unknown ExpType for ITERATOR");
        });
        toStringFun.put(TreeNodeType.OPERATION, node -> {
            List<TreeNode> children = node.getChildren();
            if(children.size() == 1){
                if(node.getExpType() == ExpType.SIZE){
                    return children.get(0) + "->size()";
                }
                return node.getTarget() + children.get(0);
            } else if(children.size() == 2){
                return children.get(0) + node.target + children.get(1);
            } else if(children.isEmpty()){
                if(node.getExpType() == ExpType.ALLINSTANCES){
                    return node.getTarget() + ".allInstances()";
                }
            }
            throw new RuntimeException("Error Children num for OPERATION");
        });
        toStringFun.put(TreeNodeType.ATTRIBUTE, node -> {
            return node.getChildren().get(0) + "." + node.getTarget().split("\\.")[1];
        });
        toStringFun.put(TreeNodeType.VARIABLE, node -> {
            return node.isSelf() ? "self" :  node.getRef().getTarget();
        });
        toStringFun.put(TreeNodeType.NAVIGATION, node -> {
            return node.getChildren().get(0) + "." + node.getTarget().split("\\.")[1];
        });
        toStringFun.put(TreeNodeType.CONSTANT, TreeNode::getTarget);
    }

    public TreeNode() {
        this.tag = "und";
        this.children = new ArrayList<>();
    }


    public TreeNode(String expName, TreeNodeType type, ExpType expType) {
        this();
        this.expName = expName;
        this.type = type;
        this.expType = expType;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public void setRef(TreeNode ref) {
        this.ref = ref;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void addChile(TreeNode node){
        children.add(node);
    }

    public String getExpName() {
        return expName;
    }

    public TreeNodeType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public ExpType getExpType() {
        return expType;
    }

    public String getTag() {
        return tag;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setTag(String tag){
        this.tag = tag;
        String[] childrenTags = this.type.getProrogation().apply(expType, tag).split(";");
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setTag(childrenTags[i]);
        }
    }

    public TreeNode getRef() {
        return this.ref;
    }

    @Override
    public String toString() {
        return toStringFun.get(type).apply(this);
    }
}
