/**
 * Title: JDGenerator.
 *
 * @author beatengine
 */
package beatengine.gen.doc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class JDGenerator
{
	private final File classFile;

	private String file;

	private boolean loaded = false;

	private int position = 0;
	/**
	 * Konstruktor der Klasse JDGenerator.
	 */
	public JDGenerator(final File javaFile)
	{
		classFile = javaFile;
	}

	private void loadFile()
	{
		try
		{
			final FileInputStream fileInputStream = new FileInputStream(classFile);
			byte[] buff = new byte[1024];
			int av = fileInputStream.available();
			file = "";
			while (av > 0)
			{
				int r = 1024;
				if(av < r)
				{
					r = av;
				}
				r = fileInputStream.read(buff,0, r);
				file += new String(buff, 0, r);
				av = fileInputStream.available();
			}
			fileInputStream.close();
			loaded = true;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private String javadoc(final String function)
	{
		String fn = function;
		String tabs = "";
		while (fn.startsWith("\t") || fn.startsWith(" "))
		{
			tabs += "\t";
			fn = fn.substring(1);
		}

		String scope = "";
		String behaviour = "";
		String returnType = "";
		String name = "";

		if(fn.startsWith("public"))
		{
			scope = "public";
			fn = fn.substring(scope.length()+1);
		}
		else if(fn.startsWith("private"))
		{
			scope = "private";
			fn = fn.substring(scope.length()+1);
		}
		else if(fn.startsWith("protected"))
		{
			scope = "protected";
			fn = fn.substring(scope.length()+1);
		}

		if(fn.startsWith("static"))
		{
			behaviour = "static";
			fn = fn.substring(behaviour.length()+1);
		}

		for(int r = 0; r < fn.length(); r++)
		{
			if(fn.charAt(r) == ' ')
			{
				if(r + 2 < fn.length() && fn.charAt(r+1) != '(' && fn.charAt(r+2) != '(' && fn.charAt(r+1) != '<' && fn.charAt(r+2) != '<')
				{
					returnType = fn.substring(0, r).trim();
					fn = fn.substring(r);
					break;
				}
			}
		}
		String paramBody = "";
		final int idxPbegin = fn.indexOf("(");
		if(idxPbegin >= 0)
		{
			paramBody = fn.substring(idxPbegin+1, fn.length()-1);
			name = fn.substring(0, idxPbegin).trim();
			fn = fn.substring(idxPbegin+1);
		}
		else
		{
			return "";
		}
		final List<String> params = new ArrayList<String>();

		if(!paramBody.isBlank())
		{
			int a = 0;
			int k = 0;
			for(int i = 0; i < paramBody.length(); i++)
			{
				if(paramBody.charAt(i) == '(')
				{
					k++;
				}
				else if(paramBody.charAt(i) == ')')
				{
					k--;
				}
				else if(paramBody.charAt(i) == ',' && k <= 0 || i== paramBody.length()-1)
				{
					String p = paramBody.substring(a, i+1);
					p = p.replace(",", "");

					int psidx = 0;
					int space = 0;
					while (psidx < p.length() && p.charAt(psidx) != '(' && p.charAt(psidx) != '=' && p.charAt(psidx) != '{')
					{
						if(p.charAt(psidx) == ' ')
						{
							space = psidx;
						}
						psidx++;
					}

					if(space > 0)
					{
						p = p.substring(space);
					}
					params.add(p.trim());
					a = i+1;
				}
			}
		}

		String description = "Description";
		boolean isGetter = false;
		boolean isSetter = false;
		boolean isQuery = false;
		String subName = "";
		if(name.startsWith("get"))
		{
			isGetter = true;
			subName = name.substring(3);
			description = "Rückgabe von " + subName;
		}
		else if(name.startsWith("set"))
		{
			isSetter = true;
			subName = name.substring(3);
			description = "Setzen von " + subName;
		}
		else if(name.startsWith("is"))
		{
			isQuery = true;
			subName = name.substring(2);
			description = "Abfragen von " + subName;
		}
		else if("compare".equals(name))
		{
			description = "Vergleichsoperator";
		}

		String doc = tabs + "/**\n";
		doc += tabs + "* " + description + "\n";

		doc += tabs + "* \n";
		for(final String param : params)
		{
			doc += tabs + "* @param " + param;
			if(isSetter || isGetter || isQuery)
			{
				doc += " " + subName;
			}
			doc += "\n";
		}
		if(isGetter)
		{
			doc += tabs + "* @return (" + returnType + ") " + subName + "\n";
		}
		doc += tabs + "*/";
		return doc;
	}

	private void insertJavaDoc()
	{
		while (true)
		{
			int fend = file.indexOf(")\n", position);
			if(fend == -1)
			{
				break;
			}
			position = fend + 1;
			int paramBegin = fend - 1;
			int s          = 0;
			while (paramBegin >= 0 && (file.charAt(paramBegin) != '(' || s > 0))
			{
				if (file.charAt(paramBegin) == ')')
				{
					s++;
				}
				else if (file.charAt(paramBegin) == '(')
				{
					s--;
				}
				paramBegin--;
			}
			int lineBegin = paramBegin - 1;
			while (lineBegin >= 0 && file.charAt(lineBegin) != '\n')
			{
				lineBegin--;
			}
			int lineBefore = lineBegin - 1;
			while (lineBefore >= 0 && file.charAt(lineBefore) != '}')
			{
				lineBefore--;
			}
			if (lineBefore <= 0 || lineBegin <= 0 || paramBegin <= 0 || fend <= lineBegin)
			{
				continue;
			}
			if (!file.substring(lineBefore, lineBegin).contains("*/") && !file.substring(lineBefore, lineBegin).contains("/*") && !file.substring(lineBefore, paramBegin).contains("="))
			{
				//Kein JavaDoc vorhanden!
				String functionLine  = file.substring(lineBegin, fend+1);
				final int    idxAnnotation = functionLine.indexOf("@");
				if (idxAnnotation >= 0 && idxAnnotation < 5)
				{
					//Ist eine Annotation keine Funktion
					continue;
				}
				if(functionLine.contains("if") || functionLine.contains("else") || functionLine.contains("switch(") || functionLine.contains("while(") || functionLine.contains("for(")  || functionLine.contains("switch (") || functionLine.contains("while (") || functionLine.contains("for ("))
				{
					//Ist if, else oder eine schleife
					continue;
				}
				while (functionLine.startsWith("\n"))
				{
					functionLine = functionLine.substring(1);
					position--;
				}
				final String javadoc = javadoc(functionLine);
				file = file.substring(0, lineBegin) + "\n" + javadoc + "\n" + functionLine + file.substring(fend+1);
				position += javadoc.length()+2;
			}
		}
	}

	/**
	 *
	 */
	private void saveFile()
	{
		try
		{
			final FileOutputStream     fileOutputStream     = new FileOutputStream(classFile);
			final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file.getBytes());
			final byte[]               parts                = new byte[1024];
			int r;
			while (byteArrayInputStream.available() > 0)
			{
				r = byteArrayInputStream.read(parts);
				fileOutputStream.write(parts, 0, r);
			}
			fileOutputStream.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void generateJavaDoc()
	{
		loadFile();
		if(loaded)
		{
				insertJavaDoc();
				saveFile();
		}
	}

}
