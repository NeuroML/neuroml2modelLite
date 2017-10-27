package org.neuroml2.modellite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class NeuroML2ModelReader
{

    public static String extractExpressions(File nml2File) throws IOException, InterruptedException
    {
        String nml2jar = "src/main/resources/org.neuroml.neuroml2-model-0.0.6-jar-with-dependencies.jar";
        System.out.println("Extracting expressions from: " + nml2File.getCanonicalPath());

        Process proc = Runtime.getRuntime().exec("java -jar " + nml2jar + " " + nml2File.getCanonicalPath());

        proc.waitFor();

        // Then retreive the process output
        InputStream in = proc.getInputStream();
        InputStream err = proc.getErrorStream();

        byte b[] = new byte[in.available()];
        in.read(b, 0, b.length);
        String strin = new String(b);

        byte c[] = new byte[err.available()];
        err.read(c, 0, c.length);
        //System.out.println(new String(c));

        return strin;
    }

    public static void main(String[] args) throws IOException, InterruptedException
    {
        String ionChannelFile = "src/test/resources/Ca_LVAst.channel.nml";
        //ionChannelFile = "../../geppetto/org.geppetto.model.neuroml/src/test/resources/traub/k2.channel.nml";
        //ionChannelFile = "../../geppetto/org.geppetto.model.neuroml/src/test/resources/hhcell/NML2_SingleCompHHCell.nml";
        if (args.length == 1)
        {
            ionChannelFile = args[0];
        }
        File nmlFile = new File(ionChannelFile);

        NeuroML2ModelReader nmlr;

        System.out.println("Opening file: " + nmlFile.getCanonicalPath());
        
        System.out.println(NeuroML2ModelReader.extractExpressions(nmlFile));

    }

}
