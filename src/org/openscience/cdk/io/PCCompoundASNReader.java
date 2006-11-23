/* $Revision: 7001 $ $Author: kaihartmann $ $Date: 2006-09-20 21:12:37 +0200 (Wed, 20 Sep 2006) $
 *
 * Copyright (C) 2006  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PubChemASNFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Reads an object from ASN formated input for PubChem Compound entries. The following
 * bits are supported: atoms.aid, atoms.element, bonds.aid1, bonds.aid2.
 *
 * @cdk.module io
 *
 * @cdk.keyword file format, PubChem Compound ASN
 */
public class PCCompoundASNReader extends DefaultChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;
    
    IMolecule molecule = null;
    Map atomIDs = null;

    /**
     * Construct a new reader from a Reader type object.
     *
     * @param input reader from which input is read
     */
    public PCCompoundASNReader(Reader input) {
        this.input = new BufferedReader(input);
        logger = new LoggingTool(this);
    }

    public PCCompoundASNReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public PCCompoundASNReader() {
        this(new StringReader(""));
    }
    
    public IResourceFormat getFormat() {
        return PubChemASNFormat.getInstance();
    }
    
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IChemFile) {
        	try {
        		return (IChemObject)readChemFile((IChemFile)object);
        	} catch (IOException e) {
        		throw new CDKException("An IO Exception occured while reading the file.", e);
        	} catch (CDKException e) {
        		throw e;
        	} catch (Exception e) {
        		throw new CDKException("An error occured.", e);
        	}
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    public void close() throws IOException {
        input.close();
    }

    // private procedures

    private IChemFile readChemFile(IChemFile file) throws Exception {
        IChemSequence chemSequence = file.getBuilder().newChemSequence();
        IChemModel chemModel = file.getBuilder().newChemModel();
        IMoleculeSet moleculeSet = file.getBuilder().newMoleculeSet();
        molecule = file.getBuilder().newMolecule();
        atomIDs = new HashMap();
        
        String line = input.readLine();
        while (input.ready() && line != null) {
        	if (line.indexOf("{") != -1) {
        		processBlock(line);
        	} else {
        		System.out.println("Skipping non-block: " + line); 
        	}
        	line = input.readLine();
        }
        moleculeSet.addAtomContainer(molecule);
        chemModel.setMoleculeSet(moleculeSet);
        chemSequence.addChemModel(chemModel);
        file.addChemSequence(chemSequence);
        return file;
    }

    
	private void processBlock(String line) throws Exception {
    	String command = getCommand(line);
    	if (command.equals("atoms")) {
            // parse frame by frame
    		System.out.println("ASN atoms found");
    		processAtomBlock();
    	} else if (command.equals("bonds")) {
    		// ok, that fine
    		System.out.println("ASN bonds found");
    		processBondBlock();
    	} else if (command.equals("PC-Compound ::=")) {
    		// ok, that fine
    		System.out.println("ASN PC-Compound found");
        } else {
        	System.out.println("Skipping block: " + command);
        	skipBlock();
        }
	}

	private void processAtomBlock() throws Exception {
		String line = input.readLine();
        while (input.ready() && line != null) {
        	if (line.indexOf("{") != -1) {
        		processAtomBlockBlock(line);
        	} else if (line.indexOf("}")!= -1) {
    			return;
    		} else {
        		System.out.println("Skipping non-block: " + line); 
        	}
        	line = input.readLine();
        }
	}

	private void processBondBlock() throws Exception {
		String line = input.readLine();
        while (input.ready() && line != null) {
        	if (line.indexOf("{") != -1) {
        		processBondBlockBlock(line);
        	} else if (line.indexOf("}")!= -1) {
    			return;
    		} else {
        		System.out.println("Skipping non-block: " + line); 
        	}
        	line = input.readLine();
        }
	}

	private IAtom getAtom(int i) {
		if (molecule.getAtomCount() <= i) {
			molecule.addAtom(molecule.getBuilder().newAtom());
		}
		return molecule.getAtom(i);
	}
	
	private IBond getBond(int i) {
		if (molecule.getBondCount() <= i) {
			molecule.addBond(molecule.getBuilder().newBond());
		}
		return molecule.getBond(i);
	}
	
	private void processAtomBlockBlock(String line) throws Exception {
		String command = getCommand(line);
		if (command.equals("aid")) {
			// assume this is the first block in the atom block
			System.out.println("ASN atoms aid found");
			processAtomAIDs();
		} else if (command.equals("element")) {
			// assume this is the first block in the atom block
			System.out.println("ASN atoms element found");
			processAtomElements();
		} else {
			System.out.println("Skipping atom block block: " + command);
			skipBlock();
		}
	}
	
	private void processBondBlockBlock(String line) throws Exception {
		String command = getCommand(line);
		if (command.equals("aid1")) {
			// assume this is the first block in the atom block
			System.out.println("ASN bonds aid1 found");
			processBondAtomIDs(0);
		} else if (command.equals("aid2")) {
			// assume this is the first block in the atom block
			System.out.println("ASN bonds aid2 found");
			processBondAtomIDs(1);
		} else {
			System.out.println("Skipping atom block block: " + command);
			skipBlock();
		}
	}
	
	private void processAtomAIDs() throws Exception {
		String line = input.readLine();
		int atomIndex = 0;
        while (input.ready() && line != null) {
        	if (line.indexOf("}") != -1) {
        		// done
        		return;
        	} else {
//        		System.out.println("Found an atom ID: " + line);
//        		System.out.println("  index: " + atomIndex);
        		IAtom atom = getAtom(atomIndex);
        		String id = getValue(line);
        		atom.setID(id);
        		atomIDs.put(id, atom);
        		atomIndex++;
        	}
        	line = input.readLine();
        }
	}

	private void processBondAtomIDs(int pos) throws Exception {
		String line = input.readLine();
		int bondIndex = 0;
        while (input.ready() && line != null) {
        	if (line.indexOf("}") != -1) {
        		// done
        		return;
        	} else {
//        		System.out.println("Found an atom ID: " + line);
//        		System.out.println("  index: " + atomIndex);
        		IBond bond = getBond(bondIndex);
        		String id = getValue(line);
        		IAtom atom = (IAtom)atomIDs.get(id);
        		if (atom == null) {
        			throw new CDKException("File is corrupt: atom ID does not exist " + id);
        		}
        		bond.setAtom(atom, pos);
        		bondIndex++;
        	}
        	line = input.readLine();
        }
	}

	private void processAtomElements() throws Exception {
		String line = input.readLine();
		int atomIndex = 0;
        while (input.ready() && line != null) {
        	if (line.indexOf("}") != -1) {
        		// done
        		return;
        	} else {
//        		System.out.println("Found symbol: " + toSymbol(getValue(line)));
//        		System.out.println("  index: " + atomIndex);
        		IAtom atom = getAtom(atomIndex);
        		atom.setSymbol(toSymbol(getValue(line)));
        		atomIndex++;
        	}
        	line = input.readLine();
        }
	}

	private String toSymbol(String value) {
		if (value.length() == 1) return value.toUpperCase();
		return value.substring(0,1).toUpperCase() + value.substring(1);
	}

	private void skipBlock() throws IOException {
		String line = input.readLine();
		int openBrackets = 0;
        while (line != null) {
//    		System.out.println("SkipBlock: line=" + line);
    		if (line.indexOf('{') != -1) {
        		openBrackets++;
        	}
//    		System.out.println(" #open brackets: " + openBrackets);
        	if (line.indexOf('}') != -1) {
        		if (openBrackets == 0) return;
        		openBrackets--;
        	}
        	line = input.readLine();
        }
	}

    private String getCommand(String line) {
    	StringBuffer buffer = new StringBuffer();
    	int i = 0;
    	boolean foundBracket = false;
    	while (i<line.length() && !foundBracket) {
    		char currentChar = line.charAt(i);
    		if (currentChar == '{') {
    			foundBracket = true;
    		} else {
    			buffer.append(currentChar);
    		}
    		i++;
    	}
    	return foundBracket ? buffer.toString().trim() : null;
    }
    
    private String getValue(String line) {
    	StringBuffer buffer = new StringBuffer();
    	int i = 0;
    	boolean foundComma = false;
    	boolean preWS = true;
    	while (i<line.length() && !foundComma) {
    		char currentChar = line.charAt(i);
    		if (Character.isWhitespace(currentChar)) {
    			if (!preWS) buffer.append(currentChar);
    		} else if (currentChar == ',') {
    			foundComma = true;
    		} else {
    			buffer.append(currentChar);
    			preWS = false;
    		}
    		i++;
    	}
    	return buffer.toString();
    }
}
