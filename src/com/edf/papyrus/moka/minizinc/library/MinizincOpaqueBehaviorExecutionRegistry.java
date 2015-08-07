package com.edf.papyrus.moka.minizinc.library;

import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.Locus;
import org.eclipse.papyrus.moka.fuml.registry.AbstractOpaqueBehaviorExecutionRegistry;

public class MinizincOpaqueBehaviorExecutionRegistry extends AbstractOpaqueBehaviorExecutionRegistry {
	
	protected final static String MINIZINC_LIBRARY_NAME = "MinizincLibrary";
	
	/* (non-Javadoc)
	 * @see org.eclipse.papyrus.moka.fuml.registry.AbstractOpaqueBehaviorExecutionRegistry#registerOpaqueBehaviorExecutions(org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.Locus)
	 */
	@Override
	public void registerOpaqueBehaviorExecutions(Locus locus) {
		// TODO Auto-generated method stub
		super.registerOpaqueBehaviorExecutions(locus);
		this.buildOpaqueBehaviorsMap(MINIZINC_LIBRARY_NAME);
		try{
			this.registerOpaqueBehaviorExecution(new AffectationExecution(), "Minizinc Library::Affectation");
		}
		catch(Exception e){
			org.eclipse.papyrus.infra.core.Activator.log.error(e);
		}
	}

}
