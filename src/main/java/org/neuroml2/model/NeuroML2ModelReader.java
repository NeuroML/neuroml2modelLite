package org.neuroml2.model;

import java.io.File;
import java.io.IOException;


public class NeuroML2ModelReader {

	
    
    
    
    public static void main(String[] args) throws IOException
    {
        String ionChannelFile = "src/test/resources/Ca_LVAst.channel.nml";
        ionChannelFile = "../../geppetto/org.geppetto.model.neuroml/src/test/resources/traub/k2.channel.nml";
        ionChannelFile = "../../geppetto/org.geppetto.model.neuroml/src/test/resources/hhcell/NML2_SingleCompHHCell.nml";
        if (args.length==1)
            ionChannelFile = args[0];
        File nmlFile = new File(ionChannelFile);
        
        
        NeuroML2ModelReader nmlr;
     
            System.out.println("Opening file: "+nmlFile.getCanonicalPath());
           
    }

}
