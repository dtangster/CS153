package wci.intermediate;

import wci.frontend.GoParserVisitor;
import wci.frontend.SimpleNode;
import wci.frontend.*;


public class GoParserVisitorAdapter implements GoParserVisitor {
    public Object visit(SimpleNode node, Object data) {
        return node.childrenAccept(this, data);
    }

    // TODO: As we change productions from #void to something else in Go.jjt, we will have to implement
    // TODO: each interface methods from the GoParserVisitor that is generated by JJTree.}

    public Object visit(ASTstatementList node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTstatement node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTassignmentStatement node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTidentifier node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTintegerConstant node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTrealConstant node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTfunctionDeclaration node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTparameter node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTparameterList node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTblock node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTifStatement node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTswitchStatement node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTswitchBlock node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTcaseGroup node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTforClause node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTexpression node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTexpressionList node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTforStatement node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASToperand node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTincOrDec node, Object data) {
        return node.childrenAccept(this, data);
    }
}
