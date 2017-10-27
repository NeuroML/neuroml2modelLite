package org.neuroml2.modellite;

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
		
        String[] nmlFiles = {"Ca_LVAst.channel.nml","Ih.channel.nml"};
        for (String n: nmlFiles)
        {
            File f = new File("src/test/resources/"+n);
            System.out.println("===============================\nTesting: "+f.getCanonicalPath());
            System.out.println(NeuroML2ModelReader.extractExpressions(f));
            
        }
	}

	protected File getLocalFile(String fname) {
		return new File(getClass().getResource(fname).getFile());
	}

}
