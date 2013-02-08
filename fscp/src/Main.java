import syntax.Formula;


public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Formula f = Formula.parse("(free q in float where bz1(q)) (forall x in float where bz1(x))  (exists y in float where bz1(y)) p(x,y,q)");
		System.out.println(f.toString());
	}

}
