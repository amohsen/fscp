package fscp;

import fscp.Assignment.IncompleteAssignmentException;
import syntax.Predicate;

public interface Model {
	@SuppressWarnings("serial")
	class PredicateNotFoundException extends RuntimeException{}
	boolean executePredicate(Assignment g, Predicate pred) 
			throws PredicateNotFoundException, IncompleteAssignmentException;
	boolean wellFormed(String value, String type);
	boolean wellFormedTypeName(String type); 
}
