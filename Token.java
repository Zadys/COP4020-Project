package edu.ufl.cise.plc;

import edu.ufl.cise.plc.IToken.Kind;
import edu.ufl.cise.plc.IToken.SourceLocation;

public class Token implements IToken {

    final Kind kind;
    final int pos;
    final int length;
    final String value;

    public Token(Kind kind, int pos, int length, String value) {

        this.kind = kind;
        this.pos = pos;
        this.length = length;
        this.value = value;
    }

    @Override public Kind getKind() {
        return this.kind;
    }

    @Override public int getIntValue() {
        if(this.kind == Kind.INT_LIT)
            return Integer.parseInt(this.value);
        else {
            System.out.print("Not Kind.INT_LIT");
            return -1; //fix this return
        }
    }
    
    @Override public float getFloatValue() {
        if(this.kind == Kind.FLOAT_LIT)
            return Float.parseFloat(this.value);
        else {
            System.out.print("Not Kind.Float_Lit");
            return -1.f; //fix this return
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
        SourceLocation sl = new SourceLocation(pos, length);
        
        return sl;
    }

    @Override public String getText() {
        if(this.kind == Kind.STRING_LIT) {
            String raw = this.value;
            
            return (this.value);
        }
        else {
            System.out.print("Not Kind.String_Lit");
            return "Error"; 
        }
    }
    
    @Override public String getStringValue() {
        if(this.kind == Kind.STRING_LIT) {
            String val = "";
            for(int i = 0; i < this.value.length(); i++) {
                if(this.value.charAt(i) != '\\') {
                    val += this.value.charAt(i);
                }
                else if(i+1 < this.value.length()) {
                    switch(this.value.charAt(i+1)) {
                    case 't' -> val+= '\t';
                    case 'r' -> val+= '\r';
                    case 'n' -> val+= '\r';
                    }
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