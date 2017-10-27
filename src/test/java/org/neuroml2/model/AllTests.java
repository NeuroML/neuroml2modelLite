package org.neuroml2.model;

import java.io.File;
import org.junit.Before;
import org.junit.Test;



public class AllTests {



	@Before
	public void setUp() throws Throwable {
        NeuroML2ModelReader nmlReader = new NeuroML2ModelReader();
        
	
	}

	@Test
	public void testChannels() throws Throwable {
		
            System.out.println("Testing...");
	}

	protected File getLocalFile(String fname) {
		return new File(getClass().getResource(fname).getFile());
	}

}
