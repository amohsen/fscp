package example;

import java.util.Collection;
import java.util.Random;

import syntax.Formula;
import syntax.Quantified;
import fscp.Assignment;
import fscp.Model;
import fscp.Scholar;

public class SaddlePointScholar implements Scholar{
	private final String name;
	Random r = new Random();

	public SaddlePointScholar(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Assignment propose(Formula l, Model m,
			Collection<Assignment> excluded) {
		return new Assignment().add("q", "z1", ""+r.nextFloat());
	}

	@Override
	public Role decide(Formula f, Model m, Assignment g) {
		return r.nextBoolean()?Role.VERIFIER:Role.FALSIFIER;
	}

	@Override
	public String choose(Quantified f, Model m, Assignment g) {
		return ""+r.nextFloat();
	}
	
}
