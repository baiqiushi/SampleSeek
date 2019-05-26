package edu.uci.ics.cloudberry.sampleseek;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import edu.uci.ics.cloudberry.sampleseek.util.Config;

import java.io.File;
import java.io.IOException;

public class SampleSeekMain {

    public static Config config = null;

    public static Config loadConfig(String configFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Config config = mapper.readValue(new File(configFilePath), Config.class);
        return config;
    }

    public static void main(String[] args) {
        boolean success = false;


        // parse arguments
        String configFilePath = null;
        for (int i = 0; i < args.length; i ++) {
            switch (args[i].toLowerCase()) {
                case "--config" :
                case "-c" :
                    try {
                        configFilePath = args[i + 1];
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Config file path should follow -c [--config].");
                        return;
                    }
            }
        }

        if (configFilePath == null) {
            System.err.println("Please indicate config file path.\nUsage: --config [file] or -c [file].\n");
            //return;
            System.err.println("Use default config file path: ./src/sampleseek.yaml\n");
            configFilePath = "/Users/white/IdeaProjects/sampleseek/src/sampleseek.yaml";
        }

        // load config file
        try {
            config = loadConfig(configFilePath);
        } catch (IOException e) {
            System.err.println("Config file: [" + configFilePath + "] does not exist.");
            e.printStackTrace();
            return;
        }

        SampleManager sampleManager = new SampleManager();

        System.out.println("=== Sample exist? ===");
        success = sampleManager.isSampeExist();
        System.out.println(success ? "YES!" : "NO!");

        if (!success) {
            System.out.println("=== Generating sample into database ===");
            long start = System.currentTimeMillis();
            success = sampleManager.generateSample();
            long end = System.currentTimeMillis();
            System.out.println("successful? : " + success);
            if (!success) {
                System.err.println("exit ...");
                return;
            }
            System.out.println("time: " + String.format("%.2f", (end - start)/1000.0) + " seconds");
        }

        System.out.println("=== Loading sample into memory ===");
        long start = System.currentTimeMillis();
        success = sampleManager.loadSample();
        long end = System.currentTimeMillis();
        System.out.println("successful? : " + success);
        if (!success) {
            System.err.println("exit ...");
        }
        System.out.println("time: " + String.format("%.2f", (end - start)/1000.0) + " seconds");

        System.out.println("=== Sample loaded ===");
        sampleManager.printSample();
    }
}
