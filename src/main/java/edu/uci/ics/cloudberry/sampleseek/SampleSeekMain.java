package edu.uci.ics.cloudberry.sampleseek;

public class SampleSeekMain {

    public final static String tableName = "tweets";
    public final static String[] dimensions = {"create_at"};
    public final static String sampleName = "s_tweets";
    public final static double epsilon = 0.05;
    public final static int cardinality = 10256295;


    public static void main(String[] args) {

        boolean success = false;

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
