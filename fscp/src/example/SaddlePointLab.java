package example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import syntax.Formula;
import syntax.ParseException;
import syntax.Predicate;
import edu.neu.ccs.demeterf.lib.CommaList;
import edu.neu.ccs.demeterf.lib.SCons;
import edu.neu.ccs.demeterf.lib.SEmpty;
import edu.neu.ccs.demeterf.lib.SList;
import edu.neu.ccs.demeterf.lib.SeList;
import edu.neu.ccs.demeterf.lib.SeListE;
import edu.neu.ccs.demeterf.lib.SeListNE;
import edu.neu.ccs.demeterf.lib.ident;
import fscp.Assignment;
import fscp.ClaimFamily;
import fscp.ClaimFamily.Claim;
import fscp.Model;
import fscp.Scholar;

public class SaddlePointLab{
	public final ClaimFamily saddlePointClaims;
	public final Collection<Scholar> scholars = new ArrayList<Scholar>();
	//public final List<SGHistory> history = new ArrayList<SGHistory>();

	public SaddlePointLab(){
		try {
			Formula f = Formula.parse("(free q in z1) (forall x in z1) (exists y in z1) p(x,y,q)");
			Model m = new SaddlePointModel();
			saddlePointClaims = new ClaimFamily(f, m);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		SaddlePointLab lab = new SaddlePointLab();
		Claim c0 = lab.saddlePointClaims.new Claim(new Assignment().add("q", "z1", "0.0"));
		Claim c1 = lab.saddlePointClaims.new Claim(new Assignment().add("q", "z1", "1.0"));
		
		//populate scholars
		//populate history
		//...
	}

	static class SaddlePointModel implements Model{

		@Override
		public boolean executePredicate(Assignment g, Predicate pred) {
			if(pred.getName().getName().equals("p")){
				List<ident> args = toList(pred.getArgs());
				if(args.size() != 3) throw new RuntimeException("Inappropriate number of arguments given to "+ pred.toString());
				String xVar = args.get(0).getName();
				String yVar = args.get(1).getName();
				String qVar = args.get(2).getName();
				String xVal = g.getValue(xVar);
				String yVal = g.getValue(yVar);
				String qVal = g.getValue(qVar);
				
				if(!wellFormed(xVal, "z1")) throw new RuntimeException("ill formed value for var " + xVar);
				if(!wellFormed(yVal, "z1")) throw new RuntimeException("ill formed value for var " + yVar);
				if(!wellFormed(qVal, "z1")) throw new RuntimeException("ill formed value for var " + qVar);
				
				float x = Float.parseFloat(xVal);
				float y = Float.parseFloat(yVal);
				float q = Float.parseFloat(qVal);
				
				return (x*y + (1-x)*(1-y*y)) >= q; 
				
			}else{
				throw new RuntimeException("Unknown predicate "+ pred.toString());
			}
		}

		<X> List<X> toList(CommaList<X> lst){
			return toList(lst.getLst());
		}
		<X> List<X> toList(SList<?, X> lst){
			if(lst instanceof SEmpty<?, ?>){
				return new ArrayList<X>();
			}else{
				SCons<?, X> cns = (SCons<?, X>) lst;
				List<X> all = toList(cns.getRest());
				all.add(cns.getFirst());
				return all;
			}
		}
		<X> List<X> toList(SeList<?, X> lst){
			if(lst instanceof SeListE<?, ?>){
				return new ArrayList<X>();
			}else{
				SeListNE<?, X> nel = (SeListNE<?, X>) lst;
				List<X> all = toList(nel.getRest());
				all.add(nel.getFirst());
				return all;
			}
		} 

		@Override
		public boolean wellFormedTypeName(String type) {
			return type.equals("z1");
		}

		@Override
		public boolean wellFormed(String value, String type) {
			try{
				float v = Float.parseFloat(value);
				return v>=0 && v<=1;
			}catch(Exception e){
				return false;
			}
		}

	}

}
