package org.tzi.use.tree;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.expr.Expression;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zlyang
 * @date: 2022-05-01 9:04
 * @description:
 */
public class TreeTest extends TestCase {

    MModel model;

    @Override
    @Before
    public void setUp() throws Exception {
        model = USECompiler.compileSpecification(new FileInputStream("./demo/Demo.use"),
                "./demo/Demo.use", new PrintWriter(System.err),
                new ModelFactory());
    }

    @Test
    public void testSplit(){
        String[] split = "abc".split(";");
        System.out.println(split.length);
        Arrays.stream(split).forEach(System.out::println);
    }

    @Test
    public void testGetClassName(){
        TreeNode treeNode = new TreeNode();
        System.out.println(treeNode.getClass().getSimpleName());
    }

    @Test
    public void testModelRead(){
        model.classInvariants().stream().map(MClassInvariant::bodyExpression).forEach(System.out::println);
    }

    @Test
    public void testTreeRead(){
        List<Expression> expressions = model.classInvariants().stream().map(MClassInvariant::bodyExpression).collect(Collectors.toList());
        List<TreeNode> trees = expressions.stream().map(expression -> expression.getTreeNode(null)).collect(Collectors.toList());
        System.out.println(0);
    }

    @Test
    public void testTagSign(){
        List<Expression> expressions = model.classInvariants().stream().map(MClassInvariant::bodyExpression).collect(Collectors.toList());
        List<TreeNode> trees = expressions.stream().map(expression -> expression.getTreeNode(null)).collect(Collectors.toList());
        trees.forEach(e -> e.setTag("und"));
        System.out.println(0);
    }

    @Test
    public void testPSE(){
        List<OCLTree> oclTrees = model.classInvariants().stream().map(OCLTree::createTree).collect(Collectors.toList());
        System.out.println(0);
    }
}
