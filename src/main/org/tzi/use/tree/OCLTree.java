package org.tzi.use.tree;

import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.ocl.expr.Expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: zlyang
 * @date: 2022-05-01 15:15
 * @description:
 */
public class OCLTree {

    private TreeNode root;

    private Set<PSE> PSEList;

    private String context;

    private String name;

    public static OCLTree createTree(MClassInvariant invariant){
        return new OCLTree(invariant.bodyExpression(), invariant.name(), invariant.cls().name());
    }

    private OCLTree(Expression e, String name, String context){
        root = e.getTreeNode(null);
        root.setTag("und");
        this.PSEList = new HashSet<>();
        this.context = context;
        this.name = name;
        initPSE();
    }

    private void initPSE(){
        getNodePSE(root);
    }

    private void getNodePSE(TreeNode node){
        for (TreeNode child : node.getChildren()) {
            getNodePSE(child);
        }
//        if(node.getType() == TreeNodeType.VARIABLE && node.isSelf() && (node.getTag().contains("+") || node.getTag().contains("-"))){
//            PSEList.add(new PSE(OptType.CREATE_ENTITY, node.getTarget(), node.getTarget()));
//        } else
        if (node.getType() == TreeNodeType.NAVIGATION){
            String context = isInstance(node) ? node.getTarget() : this.context;
            if(node.getTag().contains("+")){
                PSEList.add(new PSE(OptType.INSERT_RELATION, node.getTarget(), context));
            }
            if(node.getTag().contains("-")){
                PSEList.add(new PSE(OptType.DELETE_RELATION, node.getTarget(), context));
            }
        } else if(node.getType() == TreeNodeType.ATTRIBUTE){
            String context = isInstance(node) ? node.getTarget().split("\\.")[0] : this.context;
            PSEList.add(new PSE(OptType.UPDATE_ATTRIBUTION, node.getTarget(), context));
        } else if(node.getType() == TreeNodeType.OPERATION && node.getExpType() == ExpType.ALLINSTANCES){
            if(node.getTag().contains("+")){
                PSEList.add(new PSE(OptType.CREATE_ENTITY, node.getTarget(), this.context));
            }
            if(node.getTag().contains("-")){
                PSEList.add(new PSE(OptType.DELETE_ENTITY, node.getTarget(), this.context));
            }
        }
    }

    private static boolean isInstance(TreeNode node){
        while(node != null){
            List<TreeNode> children = node.getChildren();
            if(children.size() == 0){
                if(node.getType() == TreeNodeType.VARIABLE){
                    if(node.isSelf()){
                        return true;
                    }
                    node = node.getRef();
                } else if(node.getType() == TreeNodeType.OPERATION && node.getExpType() == ExpType.ALLINSTANCES){
                    return false;
                } else {
                    throw new RuntimeException("Unknown type of node with no child");
                }
            } else {
                node = children.get(0);
            }
        }
        throw new RuntimeException("Invalid tree");
    }

    public TreeNode getRoot() {
        return root;
    }

    public Set<PSE> getPSEList() {
        return PSEList;
    }

    public String getContext() {
        return context;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "context " + context + " inv " + name + ":\n\t"
                + root.toString();
    }
}
