package fr.tbourg.inferray_bench;

import fr.ujm.tse.lt2c.satin.inferray.configuration.ConfigurationBuilder;
import fr.ujm.tse.lt2c.satin.inferray.configuration.PropertyConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        File wikiDir = new File("./res/wiki");
        for (SupportedProfile profile : new SupportedProfile[]{SupportedProfile.RDFSPLUS, SupportedProfile.RDFS, SupportedProfile.RHODF}) {

            for (File file : wikiDir.listFiles()) {
                String myOntology = file.getCanonicalPath();
                System.err.println(myOntology);
                final ConfigurationBuilder builder = new ConfigurationBuilder();
                final PropertyConfiguration config = builder
                        .setDumpFileOnExit(false)
                        .setMultithread(true).setThreadpoolSize(4)
                        .setFastClosure(true)
                        .setRulesProfile(SupportedProfile.RDFSPLUS)
                        .build();
                final Inferray inferray = new Inferray(config);
                inferray.parse(myOntology);
                inferray.process();
            }
            System.out.printf("program,profile,load,process\n");
            for (int i = 0; i < 5; i++) {
                for (File file : wikiDir.listFiles()) {
                    String myOntology = file.getCanonicalPath();
                    System.err.println(myOntology);
                    final ConfigurationBuilder builder = new ConfigurationBuilder();
                    final PropertyConfiguration config = builder
                            .setDumpFileOnExit(false)
                            .setMultithread(true).setThreadpoolSize(4)
                            .setFastClosure(true)
                            .setRulesProfile(profile)
                            .build();
                    final Inferray inferray = new Inferray(config);
                    long t = new Date().getTime();
                    inferray.parse(myOntology);
                    long load = new Date().getTime() - t;
                    long size = size(inferray.getMainTripleStore().size());
                    t = new Date().getTime();
                    inferray.process();
                    long process = new Date().getTime() - t;
                    System.out.printf("java,%s,%d,%d,%d\n", profile.name(), size, load, process);
                }
            }
        }
    }

    static long size(long size) {
        return (size % 10 == 0 ? size : size - 59);
    }
}
