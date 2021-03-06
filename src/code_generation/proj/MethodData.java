package code_generation.proj;

import java.util.ArrayList;
import java.util.Map;

public class MethodData  {
    public String name;
    public ClassData classData;
    public Map<String, String> localVars; // <name : type (String because of ****)>
    public Map<String, String> formalVars;
    public ArrayList<FormalVars> formalVarsList;
    public Map<String, VarData> fieldsVars; // fields that weren't overridden
    public String returnType;
    public int offset;

    public MethodData(String name, ArrayList<FormalVars> formalVarsList, ClassData classData,Map<String, String> localVars,Map<String, String> formalVars,Map<String, VarData>  fieldsVars,int offset,String returnType){
        this.name = name;
        this.classData = classData;
        this.localVars = localVars;
        this.fieldsVars = fieldsVars;
        this.formalVars = formalVars;
        this.returnType = returnType;
        this.offset = offset;
        this.formalVarsList = formalVarsList;
    }

    public String getMethodName(){
        return name;
    }
    String getVarType(String varName){
        if (localVars.containsKey(varName)){
            return localVars.get(varName);
        }
        if (formalVars.containsKey(varName)){
            return formalVars.get(varName);
        }
        if (fieldsVars.containsKey(varName)){
            return fieldsVars.get(varName).getType();
        }
        return "?";
    }

    boolean isField(String varName){
        return (fieldsVars.containsKey(varName));
    }


    public String getReturnType() {
        return returnType;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
