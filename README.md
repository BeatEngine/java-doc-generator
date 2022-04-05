# java-doc-generator
Generates Java-Doc to an specific file (doesn't change existing)

Example using find:

find ~/Projects/ProjectDemo/src/main/java/com/abc -name *.java -exec bash -c "java -jar JavaDocGenerator.jar \"{}\"" \;
