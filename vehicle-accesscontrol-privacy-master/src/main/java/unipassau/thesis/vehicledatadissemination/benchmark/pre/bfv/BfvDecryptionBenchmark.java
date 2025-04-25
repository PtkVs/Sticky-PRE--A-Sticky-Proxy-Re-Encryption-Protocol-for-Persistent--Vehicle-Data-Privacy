package unipassau.thesis.vehicledatadissemination.benchmark.pre.bfv;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import unipassau.thesis.vehicledatadissemination.model.ProxyReEncryptionModel;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.util.concurrent.TimeUnit;

public class BfvDecryptionBenchmark {
    public static String dataDumpFolder = System.getProperty("user.dir") + "/data_dump/BFV/";
    public static String cryptoContextFileName = "benchmark-crypto-context";
    public static String cryptoContextpath = dataDumpFolder + cryptoContextFileName;
    public static String keyPath = dataDumpFolder + "benchmark";
    public static String publicKey = dataDumpFolder + "benchmark-public-key";
    public static String privateKey = dataDumpFolder + "benchmark-private-key";
    public static String ciphertextPath = dataDumpFolder + "ciphertext";


    static final int iter = 5;
    static final int fork = 1;
    static final int warmIter = 3;
    static final int iter_time = 8;


    @State(Scope.Thread)
    public static class BFVBenchmarkStateDecryption {
        ProxyReEncryptionModel pre =new ProxyReEncryptionModel("BFV", 256, 8192, ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        String randomPlaintext;


        @Setup(Level.Invocation)
        public void setup() {
            OpenPRE.INSTANCE.cryptoContextGen(pre.getSchemeName(),
                    dataDumpFolder,
                    cryptoContextFileName,
                    256,
                    pre.getRingSize(),
                    pre.getSecurityLevel().toString());
            OpenPRE.INSTANCE.keysGen(cryptoContextpath, keyPath);
            randomPlaintext= RandomStringUtils.randomAlphanumeric(pre.getRingSize());
            OpenPRE.INSTANCE.encrypt(publicKey, randomPlaintext, ciphertextPath);

        }
        @TearDown(Level.Invocation)
        public void teardown() {

        }
    }

    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void decrypt_CC_BFV_128_8192(BFVBenchmarkStateDecryption mystate) {
        mystate.pre.setRingSize(8192);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        String result = OpenPRE.INSTANCE.decrypt(privateKey, ciphertextPath);
    }

    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void decrypt_CC_BFV_128_16384(BFVBenchmarkStateDecryption mystate) {
        mystate.pre.setRingSize(16384);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        String result = OpenPRE.INSTANCE.decrypt(privateKey, ciphertextPath);    }

    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void decrypt_CC_BFV_128_32768(BFVBenchmarkStateDecryption mystate) {
        mystate.pre.setRingSize(32768);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        String result = OpenPRE.INSTANCE.decrypt(privateKey, ciphertextPath);    }

    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void decrypt_CC_BFV_128_4096(BFVBenchmarkStateDecryption mystate) {
        mystate.pre.setRingSize(4096);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        String result = OpenPRE.INSTANCE.decrypt(privateKey, ciphertextPath);    }





    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(BfvDecryptionBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
