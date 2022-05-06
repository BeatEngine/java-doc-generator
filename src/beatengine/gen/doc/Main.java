package beatengine.gen.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static void printHelp()
    {
        System.out.println("Argumente: \"Datei-pfad/zur/irgendwas.java\"");
        System.out.println("Path to folder (/abc/cde) --> Start for all *.java files in each subdirectory");
    }



				/**
				* Description
				* 
				* @param dir
				* @return (List<File>) 
				*/
    private static List<File> findAllJavaFiles(final File dir)
    {
        final List<File> javaFiles = new ArrayList<File>();
        final File[] files = dir.listFiles();
        for(final File f : files)
        {
            if(f.isFile())
            {
                if(f.getName().endsWith(".java"))
                {
                    javaFiles.add(f);
                }
            }
            else if(f.isDirectory())
            {
                javaFiles.addAll(findAllJavaFiles(f));
            }
        }
        return javaFiles;
    }

    public static void main(final String[] args) {

        if(args.length != 1)
        {
            printHelp();
        }
        else
        {
            final File file = new File(args[0]);
            if(file.isDirectory())
            {
                final List<File> allJavaFilesOfSubdirectories = findAllJavaFiles(file);
                for(final File f : allJavaFilesOfSubdirectories)
                {
                    if(f.exists())
                    {
                        System.out.println(f.getPath()+" Java-DOc wird eingefuegt...");
                        final JDGenerator jdGenerator = new JDGenerator(f);
                        jdGenerator.generateJavaDoc();
                        System.out.println("fertig!");
                    }
                }
            }
            else if(file.exists())
            {
                System.out.println("Java-DOc wird eingefuegt...");
                final JDGenerator jdGenerator = new JDGenerator(file);
                jdGenerator.generateJavaDoc();
                System.out.println("fertig!");
            }
            else
            {
                System.out.println("Datei nicht gefunden!");
                printHelp();
            }
        }
    }
}
