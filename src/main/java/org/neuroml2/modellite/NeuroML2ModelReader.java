package org.neuroml2.modellite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import org.neuroml.export.info.model.ExpressionNode;
import org.neuroml.export.info.model.InfoNode;
import org.neuroml.model.ComponentType;
import org.neuroml.model.NeuroMLDocument;
import org.neuroml.model.Standalone;
import org.neuroml.model.util.NeuroMLConverter;
import org.neuroml.model.util.NeuroMLException;

public class NeuroML2ModelReader
{

    private static String localCopyJar = null;

    public static InfoNode extractExpressions(File nml2File) throws IOException, InterruptedException, NeuroMLException
    {
        InfoNode info = new InfoNode();

        /*
        *   This is clearly not the best way to access this functionality...
        *   There is an incompatibility between some of the dependencies used in Geppetto and the new LEMS2/NeuroML2 model
        *   packages. When that is solved, this can be replaced with a direct call to extractInfo() in NeuroML2ModelReader
        *   in https://github.com/NeuroML/neuroml2model.
         */
        if (localCopyJar == null)
        {
            String nml2jar = "org.neuroml.neuroml2-model-0.0.8-jar-with-dependencies.jar";
            File f = getLocalFile(nml2jar);

            localCopyJar = f.getCanonicalPath();
        }

        System.out.println("Extracting expressions from: " + nml2File.getCanonicalPath());

        Process proc = Runtime.getRuntime().exec("java -jar " + localCopyJar + " " + nml2File.getCanonicalPath());

        proc.waitFor();

        // Then retreive the process output
        InputStream in = proc.getInputStream();
        InputStream err = proc.getErrorStream();

        byte b[] = new byte[in.available()];
        in.read(b, 0, b.length);
        String strin = new String(b);

        byte c[] = new byte[err.available()];
        err.read(c, 0, c.length);
        String strerr = new String(c);

        if (proc.exitValue() != 0)
        {
            throw new NeuroMLException("Problem loading: " + nml2File + ":\n" + strerr);
        }
        
        String[] lines = strin.split("\\n");
        for (String line : lines)
        {
            if (line.indexOf("=") > 0)
            {
                //System.out.println("Parsing: " + line);
                String tag = line.split(" = ")[0].trim();
                String val = line.split(" = ")[1].trim();
                String[] pre = tag.split(":");
                if (info.get(pre[0]) == null)
                {
                    info.put(pre[0], new InfoNode());
                }
                InfoNode l2 = (InfoNode) info.get(pre[0]);
                if (l2.get(pre[1]) == null)
                {
                    l2.put(pre[1], new InfoNode());
                }
                InfoNode l3 = (InfoNode) l2.get(pre[1]);

                if (pre[1].indexOf("gate") >= 0)
                {
                    if (pre[2].indexOf("instances") >= 0)
                    {
                        l3.put(pre[2], Integer.parseInt(val));
                    }
                    else
                    {
                        String expr = val.replaceAll(":", " : ").replaceAll("\\?", " ? ");
                        String ex = pre[2];

                        String title = pre[0] + " - " + pre[1] + " - " + pre[2];

                        double dv = 0.0025;
                        String yaxis = "rate (1/s)";
                        if (pre[2].contains("time"))
                            yaxis = "tau (s)";
                        if (pre[2].contains("steady"))
                            yaxis = "steady state";
                        ExpressionNode en = new ExpressionNode(expr, title, "Membrane potential (V)", yaxis, -0.1, 0.1 + dv, dv);
                        l3.put(ex, en);

                    }
                }
                else
                {
                    l3.put(pre[2], val);
                }

            }

        }

        return info;
    }

    public static InfoNode extractExpressions(NeuroMLDocument nmlDoc0) throws NeuroMLException
    {
        NeuroMLConverter nmlc = new NeuroMLConverter();
        NeuroMLDocument nmlDoc = new NeuroMLDocument();
        nmlDoc.setId(nmlDoc0.getId());

        LinkedHashMap<String, Standalone> saes = nmlc.getAllStandaloneElements(nmlDoc0);
        for (Standalone sae : saes.values())
        {
            if (!(sae.getClass().getName().equals("org.neuroml.model.Network")
                || sae.getClass().getName().equals("org.neuroml.model.Cell")
                || sae.getClass().getName().equals("org.neuroml.model.Cell2CaPools")
                || sae.getClass().getName().equals("org.neuroml.model.IonChannelKS")
                || sae.getClass().getName().equals("org.neuroml.model.PoissonFiringSynapse")
                || sae.getClass().getName().equals("org.neuroml.model.GapJunction")))
            {
                //System.out.println("-- Including " + sae.getClass().getName());
                NeuroMLConverter.addElementToDocument(nmlDoc, sae);
            }
        }

        for (ComponentType ct : nmlDoc0.getComponentType())
        {
            nmlDoc.getComponentType().add(ct);
        }

        try
        {
            File f = File.createTempFile("NeuroML_", ".tmp");
            System.out.println("Written temp file to: " + f.getCanonicalPath());
            nmlc.neuroml2ToXml(nmlDoc, f.getAbsolutePath());
            return extractExpressions(f);
        }
        catch (IOException i)
        {
            throw new NeuroMLException(i);
        }
        catch (InterruptedException i)
        {
            throw new NeuroMLException(i);
        }
    }

    private static File getLocalFile(String name) throws IOException
    {
        File f;

        String path = NeuroML2ModelReader.class.getClassLoader().getResource(name).toExternalForm();

        InputStream input = NeuroML2ModelReader.class.getClassLoader().getResourceAsStream(name);
        f = File.createTempFile(name.replaceAll("/", "_"), ".tmp");
        System.out.println("Creating " + f.getCanonicalPath() + " from " + path);
        OutputStream out = new FileOutputStream(f);
        int read;
        byte[] bytes = new byte[1024];
        while ((read = input.read(bytes)) != -1)
        {
            out.write(bytes, 0, read);
        }
        f.deleteOnExit();

        return f;
    }

    public static void main(String[] args) throws IOException, InterruptedException, NeuroMLException
    {
        String ionChannelFile = "src/test/resources/NML2_SingleCompHHCell.nml";
        //ionChannelFile = "src/test/resources/kdr.channel.nml";
        //ionChannelFile = "../../geppetto/org.geppetto.model.neuroml/src/test/resources/traub/k2.channel.nml";
        //ionChannelFile = "../../geppetto/org.geppetto.model.neuroml/src/test/resources/hhcell/NML2_SingleCompHHCell.nml";
        //ionChannelFile = "/tmp/NeuroML_3644588102506634724.tmp";
        //ionChannelFile = "../../neuroConstruct/osb/cerebral_cortex/networks/Thalamocortical/NeuroML2/pythonScripts/netbuild/TestL23.net.nml";
        ionChannelFile = "../../neuroConstruct/osb/cerebellum/networks/VervaekeEtAl-GolgiCellNetwork/NeuroML2/Golgi_040408_C1.net.nml";
        if (args.length == 1)
        {
            ionChannelFile = args[0];
        }
        File nmlFile = new File(ionChannelFile);

        NeuroML2ModelReader nmlr;

        System.out.println("Opening file: " + nmlFile.getCanonicalPath());

        System.out.println(NeuroML2ModelReader.extractExpressions(nmlFile));
        NeuroMLConverter nmlc = new NeuroMLConverter();
        NeuroMLDocument nmlDoc = nmlc.loadNeuroML(nmlFile, true);

        System.out.println(NeuroML2ModelReader.extractExpressions(nmlDoc).toDetailString(">  "));

    }

}
