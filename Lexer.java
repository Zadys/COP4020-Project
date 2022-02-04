package edu.ufl.cise.plc;

import java.util.LinkedList;
import java.util.Queue;

public class Lexer implements ILexer {
	
	//ArrayList<Token> tokens;
	Queue<Token> tokens = new LinkedList<>();
	
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
	}
	
	public Lexer(String input) {

		input += '\0';

        int pos = 0;
        int startPos = 0;
        char c = input.charAt(0);
        
        State state = State.START;
        
		while(c != '\0') {
			
	        c = input.charAt(pos);
	        boolean isZero = false;
	        int length = pos - startPos;
	        String value = input.substring(startPos, pos);
	        
	        switch(state) {
	
	            case START -> {
	
	                startPos = pos;

	                switch(c) {
	                	case ' ', '\t', '\n', '\r' -> {pos++;} //skip whitespace
	                    case '&' -> tokens.add(new Token(Token.Kind.AND, startPos, 1, "&"));
	                    case '=' -> state = State.HAS_EQUALS;
	                    case '!' -> state = State.HAS_EXC;
	                    case ',' -> tokens.add(new Token(Token.Kind.COMMA, startPos, 1, ","));
	                    case '/' -> tokens.add(new Token(Token.Kind.DIV, startPos, 1, "/"));
	                    case '>' -> state = State.GREATER_THAN;
	                    case '<' -> state = State.LESS_THAN;
	                    case '(' -> tokens.add(new Token(Token.Kind.LPAREN, startPos, 1, "("));
	                    case ')' -> tokens.add(new Token(Token.Kind.RPAREN, startPos, 1, ")"));
	                    case '[' -> tokens.add(new Token(Token.Kind.LSQUARE, startPos, 1, "["));
	                    case ']' -> tokens.add(new Token(Token.Kind.RSQUARE, startPos, 1, "]"));
	                    case '-' -> state = State.HAS_MINUS;
	                    case '%' -> tokens.add(new Token(Token.Kind.MOD, startPos, 1, "%"));
	                    case '|' -> tokens.add(new Token(Token.Kind.OR, startPos, 1, "|"));
	                    case '+' -> tokens.add(new Token(Token.Kind.PLUS, startPos, 1, "+"));
	                    case '^' -> tokens.add(new Token(Token.Kind.RETURN, startPos, 1, "^"));
	                    case ';' -> tokens.add(new Token(Token.Kind.SEMI, startPos, 1, ";"));
	                    case '*' -> tokens.add(new Token(Token.Kind.TIMES, startPos, 1, "*"));
	                    
	                }
	
	                if(Character.isLetter(c) || c == '_' || c == '$') { 
	                    state = State.IN_IDENT;
                    }
	                else if(Character.isDigit(c)) {
	                	if(c == '0') isZero = true;
	                	state = State.IN_NUM;
	                }
	                else if (c == '"')
	                	state = State.IN_STR;
	                
	                
	                pos++;
	
	            }
	            case IN_IDENT -> {
	            	//System.out.print("\nEntered IN_IDENT\n");
	
	                if( Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == '$' )
	                    pos++;
	                else {
	                	tokens.add(new Token(Token.Kind.IDENT, startPos, length, value));
	                    state = State.START;
	                }
	
	            }
	            case IN_NUM -> {
	            	//System.out.print("\nEntered IN_NUM\n");
	            	
	            	if(Character.isDigit(c) && !isZero)
	            	    pos++;
	            	else if (c == '.') {
	            	    state = State.HAS_DOT;
	            	    pos++;
	            	}
	            	else {
	            	    tokens.add(new Token(Token.Kind.INT_LIT, startPos, length, value));
	            		state = State.START;
	            	}
	
	            }
	            case HAS_DOT -> {
	            	if(Character.isDigit(c)) {
	            		state = State.IN_FLOAT;
	            		pos++;
	            	}
	            	else {
	            		//throw new LexicalException("Error");
	            		System.out.print("\nInvalid float\n");
	            	}
	            		
	            }
	            case IN_FLOAT -> {
	            	if(Character.isDigit(c))
	            		pos++;
	            	else {
	            		tokens.add(new Token(Token.Kind.FLOAT_LIT, startPos, length, value));
	            		state = State.START;
	            	}
	            	
	            }
	            case HAS_EQUALS -> {
	            	if(c == '=') {
	            		tokens.add(new Token(Token.Kind.EQUALS, startPos, 2, "=="));
	            		pos++;
	            	}
	            	else 
	            		tokens.add(new Token(Token.Kind.ASSIGN, startPos, 1, "="));

	            	state = State.START;
	            }
	            case HAS_EXC -> {
	            	if(c == '=') {
	            		tokens.add(new Token(Token.Kind.NOT_EQUALS, startPos, 2, "!="));
	            		pos++;
	            	}
	            	else 
	            		tokens.add(new Token(Token.Kind.BANG, startPos, 1, "!"));
 	
	            	state = State.START;
	            }
	            case GREATER_THAN -> {
	            	switch(c) {
		            	case '=' -> {
		            		tokens.add(new Token(Token.Kind.GE, startPos, 2, ">="));
		            		pos++;
		            	}
		            	case '>' -> {
		            		tokens.add(new Token(Token.Kind.RANGLE, startPos, 2, ">>"));
		            		pos++;
		            	}
		            	default -> tokens.add(new Token(Token.Kind.GT, startPos, 1, ">"));
	            	}
	            	
	            	state = State.START;
	            }
	            case LESS_THAN -> {
	            	switch(c) {
		            	case '=' -> {
		            		tokens.add(new Token(Token.Kind.LE, startPos, 2, "<="));
		            		pos++;
		            	}
		            	case '<' -> {
		            		tokens.add(new Token(Token.Kind.LANGLE, startPos, 2, "<<"));
		            		pos++;
		            	}
		            	case '-' -> {
		            		tokens.add(new Token(Token.Kind.LARROW, startPos, 2, "<-"));
		            		pos++;
		            	}
		            	default -> tokens.add(new Token(Token.Kind.LT, startPos, 1, "<"));
	            	}
	            	
	            	state = State.START;
	            }
	            case HAS_MINUS -> {
	            	if(c == '>') {
	            		tokens.add(new Token(Token.Kind.RARROW, startPos, 2, "->"));
	            		pos++;
	            	}
	            	else
	            		tokens.add(new Token(Token.Kind.MINUS, startPos, 1, "-"));
	            	
	            	state = State.START;
	            }
	            case IN_STR -> {
	            	switch(c) {
		            	case '"' -> {
		            		tokens.add(new Token(Token.Kind.STRING_LIT, startPos, length, value));
		            		state = state.START;
		            		pos++;
		            	}
		            	default -> pos++;
	            	}
	            }
	           //default -> throw new LexicalException("Error");
	
	        }
		}
	}
	
	
	
	@Override public IToken next() {
		
		return tokens.poll();
		
	}
	@Override public IToken peek() {
		
		return tokens.peek();
	}

}
