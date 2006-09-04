package org.openscience.cdk.reaction.type;


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>HyperconjugationReaction is the stabilising interaction that results 
 * from the interaction of the electrons in a s-bond (for our case only C-H)
 * with an adjacent empty (or partially filled) p-orbital.</p>
 * <p>Based on the valence bond model of bonding, hyperconjugation can be described as 
 * "double bond - no bond resonance"</p>
 * <p>This reaction could be represented like</p>
 * <pre>[C+]-C => C=C + [H+] </pre>
 * 
 * <pre>
 *  ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new HyperconjugationReaction();
 *  Object[] params = {Boolean.FALSE};
    type.setParameters(params);
 *  ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
 *  </pre>
 * 
 * <p>We have the possibility to localize the reactive center. Good method if you
 * want to localize the reaction in a fixed point</p>
 * <pre>atoms[0].setFlag(CDKConstants.REACTIVE_CENTER,true);</pre>
 * <p>Moreover you must put the parameter Boolean.TRUE</p>
 * <p>If the reactive center is not localized then the reaction process will
 * try to find automatically the posible reactive center.</p>
 * 
 * 
 * @author         Miguel Rojas
 * 
 * @cdk.created    2006-07-04
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 **/
public class HyperconjugationReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

	/**
	 * Constructor of the HyperconjugationReaction object
	 *
	 */
	public HyperconjugationReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the HyperconjugationReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#HyperconjugationReaction",
				this.getClass().getName(),
				"$Id: HyperconjugationReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the HyperconjugationReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("HyperconjugationReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the HyperconjugationReaction object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		Object[] params = new Object[1];
		params[0] = new Boolean (hasActiveCenter);
		return params;
	}
	
	/**
	 *  Initiate process.
	 *  It is needed to call the addExplicitHydrogensToSatisfyValency
	 *  from the class tools.HydrogenAdder.
	 *
	 *@param  reactants         reactants of the reaction.
	 *@param  agents            agents of the reaction (Must be in this case null).
	 *
	 *@exception  CDKException  Description of the Exception
	 */
	public IReactionSet initiate(IMoleculeSet reactants, IMoleculeSet agents) throws CDKException{

		logger.debug("initiate reaction: HyperconjugationReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("HyperconjugationReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("HyperconjugationReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = reactants.getBuilder().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		IAtomContainerSet acSet = reactant.getBuilder().newAtomContainerSet();
		
		IAtom[] atoms = reactant.getAtoms();
		for(int i = 0 ; i < atoms.length ; i++)
		if(atoms[i].getFlag(CDKConstants.REACTIVE_CENTER)&& atoms[i].getFormalCharge() == 1&& !(atoms[i].getSymbol().equals("H"))){
			IAtom[] atoms1 = reactant.getConnectedAtoms(atoms[i]);
			for(int j = 0; j < atoms1.length; j++)
			if(atoms1[j].getFlag(CDKConstants.REACTIVE_CENTER)&& !(atoms1[j].getSymbol().equals("H"))){
				IBond bond = reactant.getBond(atoms[i], atoms1[j]);
				if(bond.getOrder() == 1)
				if(bond.getFlag(CDKConstants.REACTIVE_CENTER)){
					IAtom[] atoms2 = reactant.getConnectedAtoms(atoms1[j]);
					for(int k = 0; k < atoms2.length ; k++){
						if(atoms2[k].getSymbol().equals("H")){
				
							int atom1 = reactants.getMolecule(0).getAtomNumber(atoms[i]);
							int atom2 = reactants.getMolecule(0).getAtomNumber(atoms1[j]);
							int atomH = reactants.getMolecule(0).getAtomNumber(atoms2[k]);
							int bond1 =  reactants.getMolecule(0).getBondNumber(bond);

							IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
							reaction.addReactant(reactants.getMolecule(0));
								
							IMolecule reactantCloned;
							try {
								reactantCloned = (IMolecule) reactant.clone();
							} catch (CloneNotSupportedException e) {
								throw new CDKException("Could not clone IMolecule!", e);
							}
								
							double order = reactantCloned.getBond(bond1).getOrder();
							reactantCloned.getBond(bond1).setOrder(order + 1);
							
							int charge = reactantCloned.getAtom(atom1).getFormalCharge();
							reactantCloned.getAtom(atom1).setFormalCharge(charge-1);
							
							reactantCloned.removeAtomAndConnectedElectronContainers(reactantCloned.getAtom(atomH));
							
							
							/* mapping */
							IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond, reactantCloned.getBond(bond1));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atoms[i], reactantCloned.getAtom(atom1));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atoms2[j], reactantCloned.getAtom(atom2));
					        reaction.addMapping(mapping);
								
					        if(existAC(acSet,reactantCloned))
								continue;
					        acSet.addAtomContainer(reactantCloned);
					        
							reaction.addProduct(reactantCloned);
							
							IAtom hydrogen = reactants.getBuilder().newAtom("H");
							hydrogen.setFormalCharge(1);
							IMolecule proton = reactants.getBuilder().newMolecule();
							proton.addAtom(hydrogen);
							reaction.addProduct(proton);
			
							setOfReactions.addReaction(reaction);
						}
					}
				}
			}
				
		}
		
		return setOfReactions;	
		
		
	}
	/**
	 * set the active center for this molecule. 
	 * The active center will be those which correspond with [A+]-B([H]). 
	 * <pre>
	 * A: Atom with charge
	 * -: Singlebond
	 * B: Atom
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		IAtom[] atoms = reactant.getAtoms();
		for(int i = 0 ; i < atoms.length ; i++)
			if(!atoms[i].getSymbol().equals("H")&& atoms[i].getFormalCharge() == 1){
			IAtom[] atoms1 = reactant.getConnectedAtoms(atoms[i]);
			for(int j = 0; j < atoms1.length; j++)
				if(!atoms1[j].getSymbol().equals("H") && atoms1[j].getFormalCharge() == 0){
				IBond bond = reactant.getBond(atoms[i], atoms1[j]);
				if(bond.getOrder() == 1){
					IAtom[] atoms2 = reactant.getConnectedAtoms(atoms1[j]);
					for(int k = 0; k < atoms2.length ; k++){
						if(atoms2[k].getSymbol().equals("H")){
							atoms[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
							atoms1[j].setFlag(CDKConstants.REACTIVE_CENTER,true);
							bond.setFlag(CDKConstants.REACTIVE_CENTER,true);
						}
					}
				}
				
			}
		}
	}
	/**
	 * controll if the new product was already found before
	 * @param acSet 
	 * @param fragment
	 * @return True, if it contains
	 */
	private boolean existAC(IAtomContainerSet acSet, IMolecule fragment) {
		QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(fragment);
		for(int i = 0; i < acSet.getAtomContainerCount(); i++){
			IAtomContainer ac = acSet.getAtomContainer(i);
			try {
				if(UniversalIsomorphismTester.isIsomorph(ac, qAC))
					return true;
			} catch (CDKException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 *  Gets the parameterNames attribute of the HyperconjugationReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the HyperconjugationReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
