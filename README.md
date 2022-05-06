# java-doc-generator
Generates Java-Doc to an specific file (doesn't change existing)

New Version >= 3.0 with support directory-path:

java -jar JavaDocGenerator.jar ~/Projects/ProjectDemo/src/main/java/com/abc

Old Example using find:

find ~/Projects/ProjectDemo/src/main/java/com/abc -name *.java -exec bash -c "java -jar JavaDocGenerator.jar \"{}\"" \;
