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

        Expression path = getPathFrom(srcContext, tarContext);

        String varName = findVarNotInExp(body);

        changeNameOfVarInExp(body, "self", varName);

        VarDecl var = new VarDecl(varName, srcContext);  //此变量var和self同类型，替换

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

    /**
     * 将exp中原来名字为srcName的Variable节点的名字改为tarName
     * @param exp
     * @param srcName
     * @param tarName
     */
    public void changeNameOfVarInExp(Expression exp,String srcName, String tarName){
        ;
    }

    /**
     * 转换的第2步骤，不断检验fBody是否满足化简规则并化简
     * @param preInv
     * @return
     */
    public MClassInvariant transformStep2(MClassInvariant preInv) {
        Expression body = preInv.bodyExpression().copy();

        int n = -1;
        while ((n = checkRules(body)) != -1) {
            body = applyRules(body, n);
        }

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

    Boolean checkRule2(Expression exp_){
        Expression exp = exp_.copy();

        if(!(exp instanceof ExpForAll))
            return false;

        Expression path = ((ExpForAll) exp).getRangeExpression();
        while(path instanceof ExpNavigation){
            MAssociationEnd dst = (MAssociationEnd) ((ExpNavigation) path).getDestination();
            MMultiplicity multi = dst.multiplicity();
            List<MMultiplicity.Range> ranges = multi.getRanges();
            int upper = ranges.get(0).getUpper();
            if(upper > 1)
                return false;
            else
                path = ((ExpNavigation) path).getObjectExpression();
        }

        return true;
    }

    Boolean checkRule3(Expression exp){
        return false;
    }
    Boolean checkRule4(Expression exp){
        return false;
    }

    Expression applyRule2(Expression exp){

        return null;
    }
    Expression applyRule3(Expression exp){
        return null;
    }
    Expression applyRule4(Expression exp){
        return null;
    }
}
