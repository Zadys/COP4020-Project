package edu.ufl.cise.plc;

import edu.ufl.cise.plc.IToken.Kind;
import edu.ufl.cise.plc.IToken.SourceLocation;

public class Token implements IToken {

    final Kind kind;
    final int pos;
    final int length;
    final String value;
    final SourceLocation sl;
    
    public Token(Kind kind, int pos, int length, String value, SourceLocation sl) {

        this.kind = kind;
        this.pos = pos;
        this.length = length;
        this.value = value;
        this.sl = sl;
    }

    @Override public Kind getKind() {
        return this.kind;
    }

    @Override public int getIntValue() {
        try {
        if(this.kind == Kind.INT_LIT)
            return Integer.parseInt(this.value);
        else {
            System.out.print("Not Kind.INT_LIT");
            return -1; //fix this return
        }
        }catch(Exception e) {
            System.out.print("Throw error int"); // needs change
            return -1;
        }
    }
    
    @Override public float getFloatValue() {
        try {
        if(this.kind == Kind.FLOAT_LIT)
            return Float.parseFloat(this.value);
        else {
            System.out.print("Not Kind.Float_Lit");
            return -1.f; //fix this return
        }
        }catch(Exception e) {
            System.out.print("throw error float"); // needs change
            return -1.f;
        }
    }
    
    @Override public boolean getBooleanValue() {
        if(this.kind == Kind.BOOLEAN_LIT)
            return Boolean.parseBoolean(this.value);
        else {
            System.out.print("Not Kind.Boolean_Lit");
            return false;
        }
    }
    
    //not done yet
    @Override public SourceLocation getSourceLocation() {
        return this.sl;
    }

    @Override public String getText() {
        if(this.kind == Kind.STRING_LIT) {
            return this.value;
        }
        else {
            System.out.print("Not Kind.String_Lit");
            return "Error"; 
        }
    }
    
    @Override public String getStringValue() {
        if(this.kind == Kind.STRING_LIT) {
            String val = "";
            for(int i = 1; i < this.value.length()-1; i++) {
                if(this.value.charAt(i) != '\\') {
                    val += this.value.charAt(i);
                }
                else if(i+1 < this.value.length()) {
                    switch(this.value.charAt(i+1)) {
                    case 't' -> {val+= '\t';}
                    case 'r' -> {val+= '\r';}
                    case 'n' -> {val+= '\n';}
                    case 'f' -> {val+= '\f';}
                    case 'b' -> {val+= '\b';}
                    case '\"'-> {val+= '\"';}
                    case '\''-> {val+= '\'';}
                    }
                    i++;
                }
                //else throw error
            }
            return val;
        }
        else {
            System.out.print("Not Kind.String_Lit");
            return "Error";
        }
    }
}