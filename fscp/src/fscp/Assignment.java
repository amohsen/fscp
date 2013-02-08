package fscp;

import java.util.HashMap;
import java.util.Map;
import edu.neu.ccs.demeterf.lib.*;


/** Representation of Assignment */
public class Assignment{
	@SuppressWarnings("serial")
	public class IncompleteAssignmentException extends RuntimeException{};
	private Map<String, String> var2type = new HashMap<String, String>();
	private Map<String, String> var2val = new HashMap<String, String>();
	protected final List<VarAssignment> varAssignments;

	public Assignment(){
		this.varAssignments = List.create();
	}

	/** Construct a(n) Assignment Instance */
	public Assignment(List<VarAssignment> varAssignments){
		this.varAssignments = varAssignments;
		for(VarAssignment va : varAssignments){
			String var = va.getVar();
			String type = va.getType();
			String val = va.getValue();
			var2type.put(var, type);
			var2val.put(var, val);
		}
	}

	public String getType(String var){
		String type = var2type.get(var);
		if(type == null) throw new IncompleteAssignmentException();
		return type;
	}

	public String getValue(String var){
		String val = var2val.get(var);
		if(val == null) throw new IncompleteAssignmentException();
		return val;
	}

	public Assignment add(String var, String type, String value){
		VarAssignment nva = new VarAssignment(var, type, value);
		Assignment g = new Assignment(varAssignments.push(nva));
		g.var2type.putAll(var2type);
		g.var2type.put(var, type);
		g.var2val.putAll(var2val);
		g.var2val.put(var, value);
		return g;
	}


	/** Is the given object Equal to this Assignment? */
	public boolean equals(Object o){
		if(!(o instanceof Assignment))return false;
		if(o == this)return true;
		Assignment oo = (Assignment)o;
		return (((Object)varAssignments).equals(oo.varAssignments));
	}
	/** Parse an instance of Assignment from the given String */
	public static Assignment parse(String inpt) throws fscp.ParseException{
		return new fscp.TheParser(new java.io.StringReader(inpt)).parse_Assignment();
	}
	/** Parse an instance of Assignment from the given Stream */
	public static Assignment parse(java.io.InputStream inpt) throws fscp.ParseException{
		return new fscp.TheParser(inpt).parse_Assignment();
	}
	/** Parse an instance of Assignment from the given Reader */
	public static Assignment parse(java.io.Reader inpt) throws fscp.ParseException{
		return new fscp.TheParser(inpt).parse_Assignment();
	}

	/** Field Class for Assignment.varAssignments */
	public static class varAssignments extends edu.neu.ccs.demeterf.Fields.any{}

	/** DGP method from Class Display */
	public String display(){ return fscp.Display.DisplayM(this); }
	/** DGP method from Class Print */
	public String print(){ return fscp.Print.PrintM(this); }
	/** DGP method from Class ToStr */
	public String toStr(){ return fscp.ToStr.ToStrM(this); }
	/** DGP method from Class PrintToString */
	public String toString(){ return fscp.PrintToString.PrintToStringM(this); }
	/** DGP method from Class HashCode */
	public int hashCode(){ return fscp.HashCode.HashCodeM(this); }
	/** Getter for field Assignment.varAssignments */
	public List<VarAssignment> getVarAssignments(){ return varAssignments; }
}
