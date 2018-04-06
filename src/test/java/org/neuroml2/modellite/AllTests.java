package org.neuroml2.modellite;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.neuroml.model.NeuroMLDocument;
import org.neuroml.model.util.NeuroMLConverter;



public class AllTests {

	@Before
	public void setUp() throws Throwable {
        NeuroML2ModelReader nmlReader = new NeuroML2ModelReader();
        
	}

	@Test
	public void testChannels() throws Throwable {
		
        String[] nmlFiles = {"kdr.channel.nml", 
                             "SK_E2.channel.nml",
                             "SKv3_1.channel.nml",
                             "NML2_SingleCompHHCell.nml", 
                             "k2.channel.nml", 
                             "kx_rod.channel.nml"};
        
        for (String n: nmlFiles)
        {
            File f = new File("src/test/resources/"+n);
            System.out.println("===============================\nTesting: "+f.getCanonicalPath());
            System.out.println(NeuroML2ModelReader.extractExpressions(f));
            
            System.out.println("-----------------");
            NeuroMLConverter nmlc = new NeuroMLConverter();
            NeuroMLDocument nmlDoc = nmlc.loadNeuroML(f, true);
            System.out.println(NeuroML2ModelReader.extractExpressions(nmlDoc).toDetailString("  "));
            
        }
	}

	protected File getLocalFile(String fname) {
		return new File(getClass().getResource(fname).getFile());
	}

}
