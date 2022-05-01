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
     * TreeNodeType为Operation和ITERATOR时的操作类型
     */
    private OptType optType;

    /**
     * und, +, -
     */
    private String tag;

    private List<TreeNode> children;

    public TreeNode() {
        this.tag = "und";
        this.children = new ArrayList<>();
    }



    public TreeNode(String expName, TreeNodeType type,  OptType optType, String target) {
        this();
        this.expName = expName;
        this.type = type;
        this.target = target;
        this.optType = optType;
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

    public OptType getOptType() {
        return optType;
    }

    public String getTag() {
        return tag;
    }

    public List<TreeNode> getChildren() {
        return children;
    }
}
