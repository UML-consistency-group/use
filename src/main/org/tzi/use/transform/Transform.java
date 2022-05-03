package org.tzi.use.transform;

import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.expr.*;
import org.tzi.use.uml.ocl.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: jhchen
 * @date: 2022-05-02 20:49
 * @description:
 */
public class Transform {

    /**
     * 第一步，在外部添加forAll表达式，找到path，代替self
     * @param preInv
     * @param tarContext
     * @return
     */
    public MClassInvariant transformStep1(MClassInvariant preInv, MClassImpl tarContext) {
        Expression body = preInv.bodyExpression().copy();
        MClass srcContext = preInv.cls();

        Expression path = getPathFrom(srcContext, tarContext);//构造一个从srcContext->tarContext的ExpNavigation表达式，如self.rental

        String varName = findVarNotInExp(body);

        changeNameOfVarInExp(body, "self", varName);    //把body里的self名字改为varName

        VarDecl var = new VarDecl(varName, srcContext);  //此变量var和self同类型

        //构造一个forAll表达式，把原来的body包起来
        ExpForAll root = null;
        try {
            root = new ExpForAll(var, path, body);
        } catch (ExpInvalidException e) {
            e.printStackTrace();
        }

        String tarInvName = preInv.name()+tarContext.name();
        List<String> varList = new ArrayList<>();
        varList.add("self");

        //构造新的Inv
        MClassInvariant tarInv = null;
        try {
            tarInv = new MClassInvariant(tarInvName, varList, tarContext, root, false);
        } catch (ExpInvalidException e) {
            e.printStackTrace();
        }

        return tarInv;
    }

    /**
     * 返回从源类到目的类的一个Navigation 表达式路径
     * @param srcContext
     * @param tarContext
     * @return
     */
    public Expression getPathFrom(MClass srcContext, MClass tarContext){
        return null;
    }

    //找到没有在exp中用过的变量名
    public String findVarNotInExp(Expression exp){
        return null;
    }

    //将exp中原来名字为srcName的Variable节点的名字改为tarName
    public void changeNameOfVarInExp(Expression exp,String srcName, String tarName){
        ;
    }

    /**
     * 转换的第2步骤，不断检验fBody是否满足化简规则并化简
     * @param preInv
     * @return
     */
    public MClassInvariant transformStep2(MClassInvariant preInv) {
        Expression body = preInv.bodyExpression().copy();   //获取原约束的body

        int n = -1;
        while ((n = checkRules(body)) != -1) {  //不断检查是否满足某条化简rule
            body = applyRules(body, n);         //执行对应rule的过程
        }
        //若不能再应用任何rule，退出

        //用新的body构造新的约束inv
        MClassInvariant tarInv = null;
        try {
            List<String> varList = new ArrayList<>();
            varList.add("self");
            tarInv = new MClassInvariant(preInv.name(), varList, preInv.cls(), body, preInv.isExistential());
        } catch (ExpInvalidException e) {
            e.printStackTrace();
        }
        return tarInv;
    }

    int checkRules(Expression exp){
        if(checkRule2(exp))
            return 2;
        if(checkRule3(exp))
            return 3;
        if (checkRule4(exp))
            return 4;
        return -1;
    }

    Expression applyRules(Expression exp,int ruleNum){
        if(ruleNum == 2)
            return applyRule2(exp);
        if(ruleNum == 3)
            return applyRule3(exp);
        if (ruleNum == 4)
            return applyRule4(exp);
        return exp;
    }

    /**
     * 第2条规则，消除外包forAll
     * @param exp_
     * @return
     */
    Boolean checkRule2(Expression exp_){
        Expression exp = exp_.copy();

        if(!(exp instanceof ExpForAll))
            return false;

        //获取左子树，path，例如self.rental
        Expression path = ((ExpForAll) exp).getRangeExpression();
        //path如果是ExpNavigation，就要判断目的端dst的多重度是否大于1
        //如果是ExpVariable，说明走到了path的末端，跳出循环
        while(path instanceof ExpNavigation){
            MAssociationEnd dst = (MAssociationEnd) ((ExpNavigation) path).getDestination();
            MMultiplicity multi = dst.multiplicity();
            List<MMultiplicity.Range> ranges = multi.getRanges();
            int upper = ranges.get(0).getUpper();   //获取到多重度
            if(upper > 1)
                return false;
            else
                path = ((ExpNavigation) path).getObjectExpression();    //继续判断当前节点的子节点
        }

        return true;
    }

    Boolean checkRule3(Expression exp){
        return false;
    }
    Boolean checkRule4(Expression exp){
        return false;
    }

    /**
     * rule 2 的具体过程，把forAll左子树导入内部表达式的self节点
     * @param exp_
     * @return
     */
    Expression applyRule2(Expression exp_){
        Expression exp = exp_.copy();
        Expression path = ((ExpForAll)exp).getRangeExpression();
        Expression body = ((ExpForAll)exp).getQueryExpression();

        VarDecl var = ((ExpForAll) exp).getVariableDeclarations().varDecl(0);
        String varName = var.name();

        replaceVarWithExp(body,varName,path);//把body中"self"节点替换为path

        return body;
    }

    /**
     * 将body表达式中所有名字为varName的ExpVariable节点替换为tarExp
     * @param body
     * @param varName
     * @param tarExp
     */
    void replaceVarWithExp(Expression body,String varName,Expression tarExp){
        if (body instanceof ExpVariable){   //判断当前节点的类型和名字
            if(body.name().equals(varName)){
                body = tarExp.copy();   //由于body是引用类型，直接为其new新节点，则原来的body也会改变
                return;
            }
        }
        //若当前节点不是self，则要递归地处理其每个子节点
        Expression child = null;
        while ((child = getNext(body))!=null){
            replaceVarWithExp(child,varName,tarExp);
        }


    }

    Expression getNext(Expression exp){
        return null;
    }


    Expression applyRule3(Expression exp){
        return null;
    }
    Expression applyRule4(Expression exp){
        return null;
    }
}
