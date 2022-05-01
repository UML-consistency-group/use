package org.tzi.use.tree;

import java.util.ArrayList;
import java.util.List;

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
}
