package edu.ufl.cise.plc;

import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

public class Lexer implements ILexer {
	
	Queue<Token> tokens = new LinkedList<>();
	
	public static HashMap<String, IToken.Kind> reserveWords = new HashMap<String, IToken.Kind>();
	
	public static enum State {
		START,
		IN_IDENT,
		IN_NUM,
		HAS_DOT,
		IN_FLOAT,
		HAS_EQUALS,
		HAS_EXC,
		GREATER_THAN,
		LESS_THAN,
		HAS_MINUS,
		IN_STR,
		ESC_SEQ,
	}
	
	
	
	public Lexer(String input) {
		
		fillReserveMap();
		input += '\0';
		
        int pos = 0;
        int startPos = 0;
        
        int line = 0;
        int column = 0;
        
        IToken.SourceLocation location = new IToken.SourceLocation(0,0);
        
        char c = input.charAt(0);
        
        State state = State.START;
        
		while(pos < input.length()) {
			
	        c = input.charAt(pos);
	        
	        boolean isZero = false;
	        int length = pos - startPos;
	        String value = input.substring(startPos, pos);
	        
	        
	        switch(state) {
	            case START -> {
	            	
	                startPos = pos;
	                //System.out.printf("\nDetected new token at %d\n", startPos);
	                location = new IToken.SourceLocation(line, column);
	                System.out.printf("\nLooking at %d\n", startPos);
	                switch(c) {
	                	case ' ', '\t' -> {break;} //skip whitespace
	                    case '&' -> tokens.add(new Token(Token.Kind.AND, startPos, 1, "&", location));
	                    case '=' -> state = State.HAS_EQUALS;
	                    case '!' -> state = State.HAS_EXC;
	                    case ',' -> tokens.add(new Token(Token.Kind.COMMA, startPos, 1, ",", location));
	                    case '/' -> tokens.add(new Token(Token.Kind.DIV, startPos, 1, "/", location));
	                    case '>' -> state = State.GREATER_THAN;
	                    case '<' -> state = State.LESS_THAN;
	                    case '(' -> tokens.add(new Token(Token.Kind.LPAREN, startPos, 1, "(", location));
	                    case ')' -> tokens.add(new Token(Token.Kind.RPAREN, startPos, 1, ")", location));
	                    case '[' -> tokens.add(new Token(Token.Kind.LSQUARE, startPos, 1, "[", location));
	                    case ']' -> tokens.add(new Token(Token.Kind.RSQUARE, startPos, 1, "]", location));
	                    case '-' -> state = State.HAS_MINUS;
	                    case '%' -> tokens.add(new Token(Token.Kind.MOD, startPos, 1, "%", location));
	                    case '|' -> tokens.add(new Token(Token.Kind.OR, startPos, 1, "|", location));
	                    case '+' -> tokens.add(new Token(Token.Kind.PLUS, startPos, 1, "+", location));
	                    case '^' -> tokens.add(new Token(Token.Kind.RETURN, startPos, 1, "^", location));
	                    case ';' -> tokens.add(new Token(Token.Kind.SEMI, startPos, 1, ";", location));
	                    case '*' -> tokens.add(new Token(Token.Kind.TIMES, startPos, 1, "*", location));          
	                    case '\n', '\r'-> {
	                    	line++;
	                    	column = -1;
	                    }
	                }
	                
	                if(Character.isJavaIdentifierStart(c)) { 
	                    state = State.IN_IDENT;
                    }
	                else if(Character.isDigit(c)) {
	                	if(c == '0') isZero = true;
	                	state = State.IN_NUM;
	                }
	                else if (c == '\"')
	                	state = State.IN_STR;
	                
	                pos++;
	                column++;
	            }
	            case IN_IDENT -> {
	            	
	            	if(reserveWords.get(value) != null) {
	            		tokens.add(new Token(reserveWords.get(value), startPos, length, value, location));
	            		state = State.START;
	            	}
	            	else if(Character.isJavaIdentifierPart(c) && c != '\0') {
	                    pos++;
	                    column++;
	            	}
	                else {
	                	if(reserveWords.get(value) != null)
	                		tokens.add(new Token(reserveWords.get(value), startPos, length, value, location));
	                	else
	                		tokens.add(new Token(Token.Kind.IDENT, startPos, length, value, location));
	                   
	                	state = State.START;
	                }
	            }
	            case IN_NUM -> {
	            	
	            	//System.out.print("\nEntered IN_NUM\n");
	            	
	            	if(Character.isDigit(c) && !isZero){
	            		column++;
	            	    pos++;
	            	}
	            	else if (c == '.') {
	            	    state = State.HAS_DOT;
	            	    pos++;
	            	    column++;
	            	}
	            	else {
	            	    tokens.add(new Token(Token.Kind.INT_LIT, startPos, length, value, location));
	            		state = State.START;
	            	}
	
	            }
	            case HAS_DOT -> {
	            	if(Character.isDigit(c)) {
	            		state = State.IN_FLOAT;
	            		pos++;
	            		column++;
	            	}
	            	else {
	            		//throw new LexicalException("Error");
	            	}
	            		
	            }
	            case IN_FLOAT -> {

	            	
	            	if(Character.isDigit(c))
	            		pos++;
	            	else {
	            		tokens.add(new Token(Token.Kind.FLOAT_LIT, startPos, length, value, location));
	            	
	            	state = State.START;
	            	}
	            	
	            }
	            case HAS_EQUALS -> {
	            	if(c == '=') {
	            		tokens.add(new Token(Token.Kind.EQUALS, startPos, 2, "==", location));
	            		pos++;
	            	}
	            	else 
	            		tokens.add(new Token(Token.Kind.ASSIGN, startPos, 1, "=", location));

	            	state = State.START;
	            }
	            case HAS_EXC -> {
	            	if(c == '=') {
	            		tokens.add(new Token(Token.Kind.NOT_EQUALS, startPos, 2, "!=", location));
	            		pos++;
	            	}
	            	else 
	            		tokens.add(new Token(Token.Kind.BANG, startPos, 1, "!", location));
 	
	            	state = State.START;
	            }
	            case GREATER_THAN -> {
	            	switch(c) {
		            	case '=' -> {
		            		tokens.add(new Token(Token.Kind.GE, startPos, 2, ">=", location));
		            		pos++;
		            	}
		            	case '>' -> {
		            		tokens.add(new Token(Token.Kind.RANGLE, startPos, 2, ">>", location));
		            		pos++;
		            	}
		            	default -> tokens.add(new Token(Token.Kind.GT, startPos, 1, ">", location));
	            	}
	            	
	            	state = State.START;
	            }
	            case LESS_THAN -> {
	            	switch(c) {
		            	case '=' -> {
		            		tokens.add(new Token(Token.Kind.LE, startPos, 2, "<=", location));
		            		pos++;
		            	}
		            	case '<' -> {
		            		tokens.add(new Token(Token.Kind.LANGLE, startPos, 2, "<<", location));
		            		pos++;
		            	}
		            	case '-' -> {
		            		tokens.add(new Token(Token.Kind.LARROW, startPos, 2, "<-", location));
		            		pos++;
		            	}
		            	default -> tokens.add(new Token(Token.Kind.LT, startPos, 1, "<", location));
	            	}
	            	
	            	state = State.START;
	            }
	            case HAS_MINUS -> {
	            	if(c == '>') {
	            		tokens.add(new Token(Token.Kind.RARROW, startPos, 2, "->", location));
	            		pos++;
	            	}
	            	else
	            		tokens.add(new Token(Token.Kind.MINUS, startPos, 1, "-", location));
	            	
	            	state = State.START;
	            }
	            case IN_STR -> {
	            	switch(c) {
		            	case '\"' -> {
		            		tokens.add(new Token(Token.Kind.STRING_LIT, startPos, length-1, input.substring(startPos+1, pos), location));
		            		state = State.START;
		            		pos++;
		            	}
		            	case '\\' -> {
		            		state = State.ESC_SEQ;
		            		pos++;
		            	}
		            	default -> pos++;
	            	}
	            }
	            case ESC_SEQ -> {
	            	
	            	pos++;
	            	
	            	state = State.IN_STR;
	            }
	           default -> System.out.print("\nError\n");
	
	        }
		}
	}
	
	public void fillReserveMap() {
		//<type>
		reserveWords.put("string", IToken.Kind.TYPE);
		reserveWords.put("int", IToken.Kind.TYPE);
		reserveWords.put("float", IToken.Kind.TYPE);
		reserveWords.put("boolean", IToken.Kind.TYPE);
		reserveWords.put("color", IToken.Kind.TYPE);
		reserveWords.put("image", IToken.Kind.TYPE);
		reserveWords.put("void", IToken.Kind.TYPE);
		
		//<image_op>
		reserveWords.put("getWidth", IToken.Kind.IMAGE_OP);
		reserveWords.put("getHeight", IToken.Kind.IMAGE_OP);
		
		//<color_op>
		reserveWords.put("getRed", IToken.Kind.COLOR_OP);
		reserveWords.put("getGreen", IToken.Kind.COLOR_OP);
		reserveWords.put("getBlue", IToken.Kind.COLOR_OP);
		
		//<color_const>
		reserveWords.put("BLACK", IToken.Kind.COLOR_CONST);
		reserveWords.put("BLUE", IToken.Kind.COLOR_CONST);
		reserveWords.put("CYAN", IToken.Kind.COLOR_CONST);
		reserveWords.put("DARK_GRAY", IToken.Kind.COLOR_CONST);
		reserveWords.put("GRAY", IToken.Kind.COLOR_CONST);
		reserveWords.put("GREEN", IToken.Kind.COLOR_CONST);
		reserveWords.put("LIGHT_GRAY", IToken.Kind.COLOR_CONST);
		reserveWords.put("MAGENTA", IToken.Kind.COLOR_CONST);
		reserveWords.put("ORANGE", IToken.Kind.COLOR_CONST);
		reserveWords.put("PINK", IToken.Kind.COLOR_CONST);
		reserveWords.put("RED", IToken.Kind.COLOR_CONST);
		reserveWords.put("WHITE", IToken.Kind.COLOR_CONST);
		reserveWords.put("YELLOW", IToken.Kind.COLOR_CONST);
		
		//<boolean_lit>
		reserveWords.put("true", IToken.Kind.BOOLEAN_LIT);
		reserveWords.put("false", IToken.Kind.BOOLEAN_LIT);
		
		//<other_keywords>
		reserveWords.put("if", IToken.Kind.KW_IF);
		reserveWords.put("else", IToken.Kind.KW_ELSE);
		reserveWords.put("fi", IToken.Kind.KW_FI);
		reserveWords.put("write", IToken.Kind.KW_WRITE);
		reserveWords.put("console", IToken.Kind.KW_CONSOLE);
	}
	
	@Override public IToken next() throws LexicalException {
			return tokens.poll();
		
	}
	@Override public IToken peek() throws LexicalException {
		
		return tokens.peek();
	}

}
