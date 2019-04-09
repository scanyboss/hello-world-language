import com.google.common.base.Charsets;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.eclipse.xtext.util.JavaVersion;
import org.eclipse.xtext.util.XtextVersion;
import org.eclipse.xtext.xtext.wizard.*;
import org.eclipse.xtext.xtext.wizard.cli.CliProjectsCreator;

import java.io.*;
import java.util.Collections;
import java.util.jar.Manifest;

public class Main {

    public static void main(String args[]) {
        WizardConfiguration wizardConfiguration = new WizardConfiguration();
        wizardConfiguration.setBaseName("org.hello");
        wizardConfiguration.getLanguage().setName("org.hello.World");
        wizardConfiguration.getLanguage()
                .setFileExtensions(LanguageDescriptor.FileExtensions.fromString("hw"));
        wizardConfiguration.getIdeProject().setEnabled(true);
        wizardConfiguration.getUiProject().setEnabled(true);
        wizardConfiguration.setRootLocation("target");
        wizardConfiguration.setXtextVersion(new XtextVersion("2.17.0"));
        wizardConfiguration.setEncoding(Charsets.UTF_8);
        wizardConfiguration.setPreferredBuildSystem(BuildSystem.MAVEN);
        wizardConfiguration.setSourceLayout(SourceLayout.PLAIN);
        wizardConfiguration.setProjectLayout(ProjectLayout.HIERARCHICAL);
        wizardConfiguration.setJavaVersion(JavaVersion.JAVA8);
        File targetLocation = new File("target");
        targetLocation.mkdirs();
        wizardConfiguration.setRootLocation(targetLocation.getPath());

        CliProjectsCreator creator = new CliProjectsCreator();
        creator.setLineDelimiter("\n");
        creator.createProjects(wizardConfiguration);


        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("target/org.hello.parent/pom.xml"));
        request.setGoals(Collections.singletonList("compile"));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File("/usr/share/maven"));
        try {
            invoker.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        InputStream stream = null;
        FileOutputStream stream1 = null;
        try {
            String path = "target/org.hello.parent/org.hello.ui/META-INF/MANIFEST.MF";
            stream = new FileInputStream(path);

            Manifest manifest = new Manifest(stream);
            String current = manifest.getMainAttributes().getValue("Require-Bundle");
            manifest.getMainAttributes().putValue("Require-Bundle", current + ",org.eclipse.ui.console;bundle-version=\"3.8.100\"");
            stream1 = new FileOutputStream(path);
            manifest.write(stream1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null)
                    stream.close();
                if (stream1 != null) {
                    stream1.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
