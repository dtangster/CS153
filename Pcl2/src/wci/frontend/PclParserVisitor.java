/* Generated By:JavaCC: Do not edit this line. PclParserVisitor.java Version 5.0 */
package wci.frontend;

public interface PclParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTcompoundStatement node, Object data);
  public Object visit(ASTassignmentStatement node, Object data);
  public Object visit(ASTadd node, Object data);
  public Object visit(ASTsubtract node, Object data);
  public Object visit(ASTmultiply node, Object data);
  public Object visit(ASTdivide node, Object data);
  public Object visit(ASTvariable node, Object data);
  public Object visit(ASTintegerConstant node, Object data);
  public Object visit(ASTrealConstant node, Object data);
}
/* JavaCC - OriginalChecksum=9c338aa49de814aa427e07ca9648c7b3 (do not edit this line) */