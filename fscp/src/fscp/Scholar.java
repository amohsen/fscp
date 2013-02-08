package fscp;

import java.util.Collection;

import syntax.Formula;
import syntax.Quantified;

public interface Scholar {
	public enum Role {
		VERIFIER,
		FALSIFIER
	}

	String getName();
	Assignment propose(Formula l, Model m, Collection<Assignment> excluded);
	Role decide(Formula f, Model m, Assignment g);
	String choose(Quantified f, Model m, Assignment g);
}
