/* Generated By:JavaCC: Do not edit this line. GoParserVisitor.java Version 5.0 */
package wci.frontend;

public interface GoParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTstart node, Object data);
  public Object visit(ASTstatement node, Object data);
  public Object visit(ASTstatementList node, Object data);
  public Object visit(ASTblock node, Object data);
  public Object visit(ASTswitchStatement node, Object data);
  public Object visit(ASTswitchBlock node, Object data);
  public Object visit(ASTcaseGroup node, Object data);
  public Object visit(ASTassignmentStatement node, Object data);
  public Object visit(ASTincOrDec node, Object data);
  public Object visit(ASTforStatement node, Object data);
  public Object visit(ASTforClause node, Object data);
  public Object visit(ASTrangeClause node, Object data);
  public Object visit(ASToperand node, Object data);
  public Object visit(ASTexpression node, Object data);
  public Object visit(ASTexpressionList node, Object data);
}
/* JavaCC - OriginalChecksum=97555443bde7eef8b71047ad3b04046c (do not edit this line) */
