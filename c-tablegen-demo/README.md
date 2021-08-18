# Use maven plugin to generate ORM code

* Copy tablegen.properties file to your project root dir.
* Modify your pom.xml, add plugin configuration as 

```xml
<build>
        <plugins>
            <plugin>
                <groupId>com.github.yujiaao</groupId>
                <artifactId>tablegen-maven-plugin</artifactId>
                <version>1.6.4</version>

                <executions>
                    <execution>
                        <id>first-execution</id>
                        <goals>
                            <goal>gen</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <propPath>${project.basedir}/tablegen.properties</propPath>
                </configuration>
            </plugin>
          <!-- ... other plugins -->
        </plugins>
    </build>          
          
```

* Open `tablegen.properties` in your favorite editor, change the database connection string according your db configuration
* Run `mvn tablegen:gen` to generate code.
* and Rock On!
