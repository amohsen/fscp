package fscp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.neu.ccs.demeterf.lib.Option;
import edu.neu.ccs.demeterf.lib.Some;

import syntax.And;
import syntax.Compound;
import syntax.Connective;
import syntax.ForAll;
import syntax.Formula;
import syntax.Free;
import syntax.Negated;
import syntax.Predicate;
import syntax.Quantification;
import syntax.QuantificationPredicate;
import syntax.Quantified;
import syntax.Quantifier;

public final class ClaimFamily{
	private final Formula f;
	private final Model m;

	public ClaimFamily(Formula f, Model m) {
		this.f = f;
		this.m = m;
	}

	public final class Claim{
		private final Assignment g;
		private final Formula f; // same as the family's formula but with all free quantifiers dropped
		public Claim(Assignment g){
			this.g = g;
			this.f = check(ClaimFamily.this.f);
		}
		//make sure a assigns all free variables in the lab's formula		
		private Formula check(Formula f){
			if(f instanceof Predicate){
				return f;
			}else if(f instanceof Negated){
				Negated negated = (Negated) f;
				return new Negated(check(negated.getFormula()));
			}else if(f instanceof Compound){
				Compound compound = (Compound) f;
				Formula left = compound.getLeft();
				Formula right = compound.getRight();
				Connective conn = compound.getConnective();
				return new Compound(check(left), conn, check(right));
			}else{ //(f instanceof Quantified)
				Quantified quantified = (Quantified) f;
				Formula formula = quantified.getFormula();
				Quantification quantification = ((Quantified) f).getQuantification();
				Quantifier quantifier = quantification.getQuantifier();
				if(quantifier instanceof Free){
					String var = quantification.getVar().getName();
					String type = quantification.getType().getName();
					if(!g.getType(var).equals(type)){ 
						throw new RuntimeException("inappropriate type for variable " + var);
					}
					if(ClaimFamily.this.m.wellFormed(g.getValue(var), type)){ 
						throw new RuntimeException("illformed value for variable " + var);
					}
					Option<QuantificationPredicate> optQPred= quantification.getOptionalQuantificationPredicate();
					if(optQPred.isSome()){
						Predicate pred = ((Some<QuantificationPredicate>) optQPred).getJust().getPred();
						if(!m.executePredicate(g, pred)){
							throw new RuntimeException("provided value for variable " + var + " does not satisfy quantification predicate");
						}
					}
					return check(formula);
				}else{
					return new Quantified(quantification, check(formula));
				}
			}
		}
		
		public RGHistory RG(Scholar verifier, Scholar falsifier){
			return RG(f, m, g, verifier, falsifier);
		}

		public RGHistory RG(Formula f, Model m, Assignment g, Scholar verifier, Scholar falsifier ){
			if(f instanceof Predicate){
				Predicate pred = (Predicate) f;
				return new RGHistory(verifier.getName(), falsifier.getName(), 
						m.executePredicate(g, pred)? verifier.getName():falsifier.getName(),
								g, new Date().toString());
			}else if(f instanceof Negated){
				Negated negated = (Negated) f;
				return RG( negated.getFormula(), m, g, falsifier, verifier);
			}else if(f instanceof Compound){
				Compound compound = (Compound) f;
				Formula left = compound.getLeft();
				Formula right = compound.getRight();
				Connective conn = compound.getConnective();
				if(conn instanceof And){
					if(falsifier.decide(left, m, g).equals(Scholar.Role.FALSIFIER)){
						return RG(left, m, g, verifier, falsifier);
					}else{
						return RG(right, m, g, verifier, falsifier);
					}
				}else{
					if(verifier.decide(left, m, g).equals(Scholar.Role.VERIFIER)){
						return RG(left, m, g, verifier, falsifier);
					}else{
						return RG(right, m, g, verifier, falsifier);
					}
				}
			}else{ //(f instanceof Quantified)
				Quantified quantified = (Quantified) f;
				Formula formula = quantified.getFormula();
				Quantification quantification = ((Quantified) f).getQuantification();
				Quantifier quantifier = quantification.getQuantifier();
				String type = quantification.getType().getName();
				String var = quantification.getVar().getName();
				Option<QuantificationPredicate> optQPred= quantification.getOptionalQuantificationPredicate();
				Scholar actor = (quantifier instanceof ForAll)? falsifier : verifier;
				Scholar other = (quantifier instanceof ForAll)? verifier : falsifier;
				String value = actor.choose(quantified, m, g);
				if(m.wellFormed(value, type)){
					g = g.add(var, type, value);
					if(!optQPred.isSome()){
						return RG(formula, m, g, verifier, falsifier);
					}else{
						Predicate pred = ((Some<QuantificationPredicate>) optQPred).getJust().getPred();
						if(m.executePredicate(g, pred)){
							return RG(formula, m, g, verifier, falsifier);
						}else{
							return new RGHistory(verifier.getName(), falsifier.getName(), other.getName(), g, new Date().toString());
						}
					}
				}else{
					return new RGHistory(verifier.getName(), falsifier.getName(), other.getName(), g, new Date().toString());
				}
			}
		}

		/** Interactions */
		public final List<SGHistory> substantiationGame(Scholar s1, Scholar.Role s1r, 
				Scholar s2, Scholar.Role s2r){
			List<SGHistory> history = new ArrayList<SGHistory>();
			if(!s1r.equals(s2r)){ // conflict resolution
				Scholar verifier = null, falsifier = null;
				if(s1r.equals(Scholar.Role.VERIFIER)){
					verifier = s1;
					falsifier = s2;
				}else{
					verifier = s2;
					falsifier = s1;
				}
				history.add(new CRHistory(RG(verifier, falsifier)));
			}else{ //test
				if(s1r.equals(Scholar.Role.VERIFIER)){
					//both verifiers
					history.add(new TestHistory(s1.getName(), RG(s1, s2)));
					history.add(new TestHistory(s2.getName(), RG(s2, s1)));
				}else{
					//both falsifiers
					history.add(new TestHistory(s1.getName(), RG(s2, s1)));
					history.add(new TestHistory(s2.getName(), RG(s1, s2)));
				}
			}
			return history;
		}
	}
}
