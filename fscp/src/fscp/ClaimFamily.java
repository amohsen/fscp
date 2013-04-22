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
		public Claim(Assignment g){
			this.g = g;
		}
		
		public RGHistory RG(Scholar verifier, Scholar falsifier){
			return RG(f, m, g, verifier, falsifier);
		}

		/**
		 * Returns the winning scholar
		 * */
		public RGHistory RG(Formula f, Model m, Assignment g, Scholar verifier, Scholar falsifier ){
			if(f instanceof Predicate){
				Predicate pred = (Predicate) f;
				Scholar winner = m.executePredicate(g, pred)? verifier:falsifier;
				return new RGHistory(verifier.getName(), falsifier.getName(),
						winner.getName(), g, new Date().toString());
			}else if(f instanceof Negated){
				Negated negated = (Negated) f;
				return RG(negated.getFormula(), m, g, falsifier, verifier);
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
				if(quantifier instanceof Free){
					return RG(formula, m, g, verifier, falsifier);
				}else{
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
								Scholar winner = other;
								return new RGHistory(verifier.getName(), falsifier.getName(),
										winner.getName(), g, new Date().toString());
							}
						}
					}else{
						Scholar winner = other;
						return new RGHistory(verifier.getName(), falsifier.getName(),
								winner.getName(), g, new Date().toString());
					}
				}
			}
		}

		public final List<SGHistory> substantiationGame(Scholar s1, 
				Scholar s2){
			Scholar.Role s1r = s1.decide(f, m, g);
			Scholar.Role s2r = s2.decide(f, m, g);
			return substantiationGame(s1, s1r, s2, s2r);
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
				history.add(RG(verifier, falsifier));
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
