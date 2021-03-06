package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.pascal.PascalParserTD;
import wci.frontend.pascal.PascalTokenType;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.ICodeNodeType;
import wci.intermediate.SymTabEntry;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.frontend.pascal.PascalErrorCode.INVALID_OPERATOR;
import static wci.frontend.pascal.PascalTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.VALUE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

/**
 * <h1>ExpressionParser</h1>
 *
 * <p>Parse a Pascal expression.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only. No warranties.</p>
 */
public class ExpressionParser extends StatementParser {

    /**
     * Constructor.
     *
     * @param parent the parent parser.
     */
    public ExpressionParser(PascalParserTD parent) {
        super(parent);
    }
    // Synchronization set for starting an expression.
    static final EnumSet<PascalTokenType> EXPR_START_SET =
            EnumSet.of(PLUS, MINUS, IDENTIFIER, INTEGER, REAL, STRING,
                    PascalTokenType.NOT, LEFT_PAREN, LEFT_BRACKET);

    /**
     * Parse an expression.
     *
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
            throws Exception {
        return parseExpression(token);
    }
    // Set of relational operators.
    private static final EnumSet<PascalTokenType> REL_OPS =
            EnumSet.of(EQUALS, NOT_EQUALS, LESS_THAN, LESS_EQUALS,
                    GREATER_THAN, GREATER_EQUALS, IN);
    // Map relational operator tokens to node types.
    private static final HashMap<PascalTokenType, ICodeNodeType> REL_OPS_MAP = new HashMap<PascalTokenType, ICodeNodeType>();

    static {
        REL_OPS_MAP.put(EQUALS, EQ);
        REL_OPS_MAP.put(NOT_EQUALS, NE);
        REL_OPS_MAP.put(LESS_THAN, LT);
        REL_OPS_MAP.put(LESS_EQUALS, LE);
        REL_OPS_MAP.put(GREATER_THAN, GT);
        REL_OPS_MAP.put(GREATER_EQUALS, GE);
        REL_OPS_MAP.put(IN, IN_SET);
    }

    /**
     * Parse an expression.
     *
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseExpression(Token token)
            throws Exception {
        // Parse a simple expression and make the root of its tree
        // the root node.
        ICodeNode rootNode = parseSimpleExpression(token);

        token = currentToken();
        TokenType tokenType = token.getType();

        // Look for a relational operator.
        if (REL_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = REL_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            switch ((ICodeNodeTypeImpl) opNode.getType()) {
                case LT:
                case GT:
                case IN_SET:
                    if (rootNode.getType() == ICodeNodeTypeImpl.SET) {
                        errorHandler.flag(token, INVALID_OPERATOR, this);
                    }
            }

            Token lastToken = token;
            token = nextToken();  // consume the operator

            // Parse the second simple expression.  The operator node adopts
            // the simple expression's tree as its second child.
            ICodeNode rhs = parseSimpleExpression(token);
            opNode.addChild(rhs);

            if (opNode.getType() == IN_SET && rhs.getType() == INTEGER_CONSTANT) {
                errorHandler.flag(lastToken, INVALID_OPERATOR, this);
            }

            // The operator node becomes the new root node.
            rootNode = opNode;
        }

        return rootNode;
    }
    // Set of additive operators.
    private static final EnumSet<PascalTokenType> ADD_OPS =
            EnumSet.of(PLUS, MINUS, PascalTokenType.OR);
    // Map additive operator tokens to node types.
    private static final HashMap<PascalTokenType, ICodeNodeTypeImpl> ADD_OPS_OPS_MAP = new HashMap<PascalTokenType, ICodeNodeTypeImpl>();

    static {
        ADD_OPS_OPS_MAP.put(PLUS, ADD);
        ADD_OPS_OPS_MAP.put(MINUS, SUBTRACT);
        ADD_OPS_OPS_MAP.put(PascalTokenType.OR, ICodeNodeTypeImpl.OR);
    }

    /**
     * Parse a simple expression.
     *
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseSimpleExpression(Token token)
            throws Exception {
        TokenType signType = null;  // type of leading sign (if any)

        // Look for a leading + or - sign.
        TokenType tokenType = token.getType();
        if ((tokenType == PLUS) || (tokenType == MINUS)) {
            signType = tokenType;
            token = nextToken();  // consume the + or -
        }

        // Parse a term and make the root of its tree the root node.
        ICodeNode rootNode = parseTerm(token);

        // Was there a leading - sign?
        if (signType == MINUS) {

            // Create a NEGATE node and adopt the current tree
            // as its child. The NEGATE node becomes the new root node.
            ICodeNode negateNode = ICodeFactory.createICodeNode(NEGATE);
            negateNode.addChild(rootNode);
            rootNode = negateNode;
        }

        token = currentToken();
        tokenType = token.getType();

        // Loop over additive operators.
        while (ADD_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = ADD_OPS_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse another term.  The operator node adopts
            // the term's tree as its second child.
            opNode.addChild(parseTerm(token));

            // The operator node becomes the new root node.
            rootNode = opNode;

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }
    // Set of multiplicative operators.
    private static final EnumSet<PascalTokenType> MULT_OPS =
            EnumSet.of(STAR, SLASH, DIV, PascalTokenType.MOD, PascalTokenType.AND);
    // Map multiplicative operator tokens to node types.
    private static final HashMap<PascalTokenType, ICodeNodeType> MULT_OPS_OPS_MAP = new HashMap<PascalTokenType, ICodeNodeType>();

    static {
        MULT_OPS_OPS_MAP.put(STAR, MULTIPLY);
        MULT_OPS_OPS_MAP.put(SLASH, FLOAT_DIVIDE);
        MULT_OPS_OPS_MAP.put(DIV, INTEGER_DIVIDE);
        MULT_OPS_OPS_MAP.put(PascalTokenType.MOD, ICodeNodeTypeImpl.MOD);
        MULT_OPS_OPS_MAP.put(PascalTokenType.AND, ICodeNodeTypeImpl.AND);
    }

    /**
     * Parse a term.
     *
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseTerm(Token token)
            throws Exception {
        // Parse a factor and make its node the root node.
        ICodeNode rootNode = parseFactor(token);

        token = currentToken();
        TokenType tokenType = token.getType();

        if (rootNode.getType() == ICodeNodeTypeImpl.SET && (tokenType == PascalTokenType.OR || tokenType == PascalTokenType.AND)) {
            errorHandler.flag(token, INVALID_OPERATOR, this);
        }

        // Loop over multiplicative operators.
        while (MULT_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = MULT_OPS_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            switch ((ICodeNodeTypeImpl) opNode.getType()) {
                case INTEGER_DIVIDE:
                case FLOAT_DIVIDE:
                    if (rootNode.getType() == ICodeNodeTypeImpl.SET) {
                        errorHandler.flag(token, INVALID_OPERATOR, this);
                    }
            }

            token = nextToken();  // consume the operator

            // Parse another factor.  The operator node adopts
            // the term's tree as its second child.
            ICodeNode rhs = parseFactor(token);
            opNode.addChild(rhs);

            // The operator node becomes the new root node.
            rootNode = opNode;

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }

    /**
     * Parse a factor.
     *
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseFactor(Token token)
            throws Exception {
        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        switch ((PascalTokenType) tokenType) {

            case IDENTIFIER: {
                // Look up the identifier in the symbol table stack.
                // Flag the identifier as undefined if it's not found.
                String name = token.getText().toLowerCase();
                SymTabEntry id = symTabStack.lookup(name);
                if (id == null) {
                    errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
                    id = symTabStack.enterLocal(name);
                }

                rootNode = ICodeFactory.createICodeNode(VARIABLE);
                rootNode.setAttribute(ID, id);
                id.appendLineNumber(token.getLineNumber());

                token = nextToken();  // consume the identifier
                break;
            }

            case INTEGER: {
                // Create an INTEGER_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();  // consume the number
                break;
            }

            case REAL: {
                // Create an REAL_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(REAL_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();  // consume the number
                break;
            }

            case STRING: {
                String value = (String) token.getValue();

                // Create a STRING_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
                rootNode.setAttribute(VALUE, value);

                token = nextToken();  // consume the string
                break;
            }

            case NOT: {
                token = nextToken();  // consume the NOT

                // Create a NOT node as the root node.
                rootNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

                // Parse the factor.  The NOT node adopts the
                // factor node as its child.
                rootNode.addChild(parseFactor(token));

                break;
            }

            case LEFT_PAREN: {
                token = nextToken();      // consume the (

                // Parse an expression and make its node the root node.
                rootNode = parseExpression(token);

                // Look for the matching ) token.
                token = currentToken();
                if (token.getType() == RIGHT_PAREN) {
                    token = nextToken();  // consume the )
                } else {
                    errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
                }

                break;
            }

            // Case statement for set expressions
            case LEFT_BRACKET: {
                token = nextToken();      // consume the [

                // Parse the set and make it the root node
                rootNode = parseSet(token);

                // Look for the matching ] token.
                token = currentToken();
                if (token.getType() == RIGHT_BRACKET) {
                    token = nextToken();  // consume the ]
                } else {
                    errorHandler.flag(token, MISSING_RIGHT_BRACKET, this);
                }
                break;
            }

            default: {
                errorHandler.flag(token, UNEXPECTED_TOKEN, this);
                break;
            }
        }

        return rootNode;
    }
    private static final HashMap<PascalTokenType, ICodeNodeTypeImpl> SET_OPS_MAP = new HashMap<PascalTokenType, ICodeNodeTypeImpl>();

    static {
        // some stuff in here mapping EnumSet SET_OPS to ICodeNodeTypes
        ADD_OPS_OPS_MAP.put(PascalTokenType.OR, ICodeNodeTypeImpl.OR);
    }

    private ICodeNode parseSet(Token token)
            throws Exception {
        ICodeNode rootNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.SET);
        HashSet<Integer> values = new HashSet<Integer>(); // This is used for parse time duplicate error checking only
        rootNode.setAttribute(VALUE, new HashSet<Integer>());
        boolean setParsingFinished = false;

        while (token.getType() != RIGHT_BRACKET && token.getType() != ERROR && !setParsingFinished) {
            ICodeNode leftNumberNode = parseSimpleExpression(token);

            if (leftNumberNode.getType() == INTEGER_CONSTANT
                    && !values.add((Integer) leftNumberNode.getAttribute(VALUE)))
            {
                errorHandler.flag(token, NON_UNIQUE_MEMBERS, this);
            }

            token = currentToken();

            switch ((PascalTokenType) token.getType()) {
                case RIGHT_BRACKET:
                    rootNode.addChild(leftNumberNode);
                    break;
                case COMMA:
                    rootNode.addChild(leftNumberNode);
                    token = nextToken(); // Consume the ,
                    if (token.getType() == COMMA) {
                        errorHandler.flag(token, EXTRA_COMMAS, this);
                        token = nextToken(); // Consume the extra ,
                    }
                    break;
                case DOT_DOT:
                    token = nextToken(); // Consume the ..
                    if (token.getType() == COMMA) {
                        errorHandler.flag(token, INVALID_SUBRANGE, this);
                        token = nextToken(); // Consume the , that causes a syntax error
                        rootNode.addChild(leftNumberNode); // Add the left subrange as a single value anyway
                    }
                    else {
                        ICodeNode rightNumberNode = parseSimpleExpression(token); // Parse the right subrange
                        ICodeNode subrangeNode = ICodeFactory.createICodeNode(SUBRANGE);
                        subrangeNode.addChild(leftNumberNode);
                        subrangeNode.addChild(rightNumberNode);
                        rootNode.addChild(subrangeNode);

                        if (leftNumberNode.getType() == INTEGER_CONSTANT && rightNumberNode.getType() == INTEGER_CONSTANT) {
                            boolean duplicateFound = false;
                            Integer leftRange = (Integer) leftNumberNode.getAttribute(VALUE) + 1; // Already handled first number
                            Integer rightRange = (Integer) rightNumberNode.getAttribute(VALUE);

                            while (leftRange <= rightRange) {
                                if (!values.add(leftRange++) && !duplicateFound) {
                                    errorHandler.flag(token, NON_UNIQUE_MEMBERS, this);
                                    duplicateFound = true;
                                }
                            }
                        }

                        token = currentToken();
                        if (token.getType() == COMMA) {
                            token = nextToken(); // Consume the ,
                        }
                        else if (token.getType() != RIGHT_BRACKET) {
                            errorHandler.flag(token, MISSING_COMMA, this);
                        }
                    }
                    break;
                case INTEGER:
                    errorHandler.flag(token, MISSING_COMMA, this);
                    break;
                case SEMICOLON:
                    setParsingFinished = true;
                    break;
                default:
                    errorHandler.flag(token, UNEXPECTED_TOKEN, this);
                    break;
            }
        }

        return rootNode;
    }
}
