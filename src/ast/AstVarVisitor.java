package ast;

import java.util.TreeMap;

public class AstVarVisitor implements Visitor {

    private String varName;
    private String newName;
    private int varLine;

    private static int numLineVarDeclInClass = 0;
    private static int numLineVarInMethodArgs = 0;
    private static int numLineVarDeclInMethod = 0;
    private TreeMap<Integer, Integer> lineNumbers;
    // we will check what was the closes one to the specific variable, and set the data-set accordingly

    public AstVarVisitor(String varName, String newName, int varLine){
        this.lineNumbers = new TreeMap<>();
        this.varName = varName;
        this.newName = newName;
        this.varLine = varLine;
    }

    private void visitBinaryExpr(BinaryExpr e) {
        e.e1().accept(this);
        e.e2().accept(this);
    }


    @Override
    public void visit(Program program) {
        program.mainClass().accept(this);
        for (ClassDecl classdecl : program.classDecls()) {
            numLineVarDeclInMethod = 0;
            numLineVarInMethodArgs = 0;
            numLineVarDeclInClass = 0;
            // initialize when going back to the next class
            classdecl.accept(this);
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        for (var fieldDecl : classDecl.fields()) {
            if (fieldDecl.name().equals(varName)) {
                lineNumbers.put(fieldDecl.lineNumber, fieldDecl.lineNumber); // this var was declared here - it's a decl.
                numLineVarDeclInClass = fieldDecl.lineNumber;
            }
            if (fieldDecl.lineNumber == varLine) {
                fieldDecl.setName(newName);
            }
            fieldDecl.accept(this);
        }

        for (var methodDecl : classDecl.methoddecls()) {
            numLineVarInMethodArgs = 0; // initialize before the next method - different methods args not connected
            numLineVarDeclInMethod = 0; // initialize before the next method
            methodDecl.accept(this);
        }
    }

    @Override
    public void visit(MainClass mainClass) {
        mainClass.mainStatement().accept(this);
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        methodDecl.returnType().accept(this);

        for (var formal : methodDecl.formals())  // check if var in method args
            formal.accept(this);

        for (var varDecl : methodDecl.vardecls()) // check if var in method var decl
            varDecl.accept(this);


        for (var stmt : methodDecl.body())
            stmt.accept(this);

        methodDecl.ret().accept(this);
    }

    @Override
    public void visit(FormalArg formalArg) {
        if (formalArg.name().equals(varName)) {
            lineNumbers.put(formalArg.lineNumber, formalArg.lineNumber);
            numLineVarInMethodArgs = formalArg.lineNumber;
        }
        if (formalArg.lineNumber == varLine)
            formalArg.setName(newName);
        formalArg.type().accept(this);
    }

    @Override
    public void visit(VarDecl varDecl) {
        if (varDecl.name().equals(varName)){
            lineNumbers.put(varDecl.lineNumber, varDecl.lineNumber);
            numLineVarDeclInMethod = varDecl.lineNumber;
        }
        if (varDecl.lineNumber == varLine)
            varDecl.setName(newName);
        varDecl.type().accept(this);
    }

    @Override
    public void visit(BlockStatement blockStatement) {
        for (var s : blockStatement.statements())
            s.accept(this);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.cond().accept(this);

        ifStatement.thencase().accept(this);

        ifStatement.elsecase().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.cond().accept(this);

        whileStatement.body().accept(this);
    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.arg().accept(this);
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        if (assignStatement.lv().equals(varName)) {
            if (numLineVarDeclInMethod != 0) { // lv relates to it
                if (lineNumbers.get(varLine) == numLineVarDeclInMethod) {
                    assignStatement.setLv(newName);
                }
            } else if (numLineVarInMethodArgs != 0) { // lv relates to it
                if (lineNumbers.get(varLine) == numLineVarInMethodArgs) {
                    assignStatement.setLv(newName);
                }
            } else { // lv relates to it
                if (lineNumbers.get(varLine) == numLineVarDeclInClass) {
                    assignStatement.setLv(newName);
                }
            }
        }
        assignStatement.rv().accept(this);
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {

        assignArrayStatement.index().accept(this);

        assignArrayStatement.rv().accept(this);
    }

    @Override
    public void visit(AndExpr e) {
        visitBinaryExpr(e);
    }

    @Override
    public void visit(LtExpr e) {
        visitBinaryExpr(e);;
    }

    @Override
    public void visit(AddExpr e) {
        visitBinaryExpr(e);;
    }

    @Override
    public void visit(SubtractExpr e) {
        visitBinaryExpr(e);
    }

    @Override
    public void visit(MultExpr e) {
        visitBinaryExpr(e);
    }

    @Override
    public void visit(ArrayAccessExpr e) {
        e.arrayExpr().accept(this);

        e.indexExpr().accept(this);
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.arrayExpr().accept(this);
    }

    @Override
    public void visit(MethodCallExpr e) {
        e.ownerExpr().accept(this);

        for (Expr arg : e.actuals())
            arg.accept(this);

    }

    @Override
    public void visit(IntegerLiteralExpr e) {
        // values should be left empty - can't change them
    }

    @Override
    public void visit(TrueExpr e) {
        // values should be left empty - can't change them
    }

    @Override
    public void visit(FalseExpr e) {
        // values should be left empty - can't change them
    }

    @Override
    public void visit(IdentifierExpr e) { // a parameter to method or var in expression, doesn't have a line number!!!
        if (e.id().equals(varName)){
            if (numLineVarDeclInMethod != 0) {
                if (lineNumbers.get(varLine) == numLineVarDeclInMethod) {
                    e.setId(newName);
                }
            } else if (numLineVarInMethodArgs != 0) {
                if (lineNumbers.get(varLine) == numLineVarInMethodArgs) {
                    e.setId(newName);
                }
            } else {
                if (lineNumbers.get(varLine) == numLineVarDeclInClass) {
                    e.setId(newName);
                }
            }
        }
    }

    public void visit(ThisExpr e) {
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        e.lengthExpr().accept(this);
    }

    @Override
    public void visit(NewObjectExpr e) {
    }

    @Override
    public void visit(NotExpr e) {
        e.e().accept(this);
    }

    @Override
    public void visit(IntAstType t) {
    }

    @Override
    public void visit(BoolAstType t) {
    }

    @Override
    public void visit(IntArrayAstType t) {
    }

    @Override
    public void visit(RefType t) {
        if (t.id().equals(varName)){
            if (numLineVarDeclInMethod != 0) {
                if (lineNumbers.get(varLine) == numLineVarDeclInMethod) {
                    t.setId(newName);
                }
            } else if (numLineVarInMethodArgs != 0) {
                if (lineNumbers.get(varLine) == numLineVarInMethodArgs) {
                    t.setId(newName);
                }
            } else {
                if (lineNumbers.get(varLine) == numLineVarDeclInClass) {
                    t.setId(newName);
                }
            }
        }
    }
}
