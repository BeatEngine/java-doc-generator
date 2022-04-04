package beatengine.gen.doc;

import java.io.File;

public class Main {

    public static void main(final String[] args) {

        if(args.length != 1)
        {
            System.out.println("Argumente: \"Datei-pfad/zur/irgendwas.java\"");
        }
        else
        {
            final File file = new File(args[0]);
            if(file.exists())
            {
                System.out.println("Java-DOc wird eingefuegt...");
                final JDGenerator jdGenerator = new JDGenerator(file);
                jdGenerator.generateJavaDoc();
                System.out.println("fertig!");
            }
            else
            {
                System.out.println("Datei nicht gefunden!");
            }
        }
    }
}
