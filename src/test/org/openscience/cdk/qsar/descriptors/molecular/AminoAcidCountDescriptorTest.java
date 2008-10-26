/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.qsar.descriptors.molecular;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.tools.ProteinBuilderTool;

/**
 * TestSuite that runs a test for the AtomCountDescriptor.
 *
 * @cdk.module test-qsarprotein
 */
public class AminoAcidCountDescriptorTest extends NewCDKTestCase {
	
	private IMolecularDescriptor descriptor;
    
	@Before
    protected void setUp() throws CDKException {
        descriptor = new AminoAcidCountDescriptor();
    }

	@Test public void testAACount() throws CDKException {
        BioPolymer protein = ProteinBuilderTool.createProtein("ARNDCFQEGHIPLKMSTYVW");
        IDescriptorResult result = descriptor.calculate(protein).getValue();
        Assert.assertTrue(result instanceof IntegerArrayResult);
        IntegerArrayResult iaResult = (IntegerArrayResult)result;
        for (int i=0; i<iaResult.length(); i++) {
        	Assert.assertTrue(iaResult.get(i) >= 1); // all AAs are found at least once
        }
        Assert.assertEquals(20, iaResult.get(8)); // glycine is in all of them, so 20 times
	}

	@Test
    public void testFCount() throws CDKException {
        BioPolymer protein = ProteinBuilderTool.createProtein("FF");
        IDescriptorResult result = descriptor.calculate(protein).getValue();
        Assert.assertTrue(result instanceof IntegerArrayResult);
        IntegerArrayResult iaResult = (IntegerArrayResult)result;
        Assert.assertEquals(2, iaResult.get(8));
        Assert.assertEquals(4, iaResult.get(5)); // thingy is symmetrical, so two mappings at each AA position possible
	}

	@Test public void testTCount() throws CDKException {
        BioPolymer protein = ProteinBuilderTool.createProtein("TT");
        IDescriptorResult result = descriptor.calculate(protein).getValue();
        Assert.assertTrue(result instanceof IntegerArrayResult);
        IntegerArrayResult iaResult = (IntegerArrayResult)result;
        Assert.assertEquals(2, iaResult.get(8));
        Assert.assertEquals(2, iaResult.get(16));
	}
}

