package unipassau.thesis.vehicledatadissemination.benchmark.pre.bgv;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import unipassau.thesis.vehicledatadissemination.benchmark.pre.bfv.BfvEncryptionBenchmark;
import unipassau.thesis.vehicledatadissemination.model.ProxyReEncryptionModel;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.util.concurrent.TimeUnit;

public class BgvEncryptionBenchmark {

    public static String dataDumpFolder = System.getProperty("user.dir") + "/data_dump/BGV/";
    public static String cryptoContextFileName = "benchmark-crypto-context";
    public static String cryptoContextpath = dataDumpFolder + cryptoContextFileName;
    public static String keyPath = dataDumpFolder + "benchmark";
    public static String publicKey = dataDumpFolder + "benchmark-public-key";
    public static String ciphertextPath = dataDumpFolder + "ciphertext";


    static final int iter = 5;
    static final int fork = 1;
    static final int warmIter = 3;
    static final int iter_time = 8;


    @State(Scope.Thread)
    public static class BGVBenchmarkStateEncryption {
        ProxyReEncryptionModel pre =new ProxyReEncryptionModel("BGV", 256, 16384, ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
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
    public void encrypt_CC_BGV_128_16384(BGVBenchmarkStateEncryption mystate) {
        mystate.pre.setRingSize(16384);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        OpenPRE.INSTANCE.encrypt(publicKey, mystate.randomPlaintext, ciphertextPath);
    }

    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void encrypt_CC_BGV_128_32768(BGVBenchmarkStateEncryption mystate) {
        mystate.pre.setRingSize(32768);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        OpenPRE.INSTANCE.encrypt(publicKey, mystate.randomPlaintext, ciphertextPath);    }

    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void encrypt_CC_BGV_128_8192(BGVBenchmarkStateEncryption mystate) {
        mystate.pre.setRingSize(8192);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        OpenPRE.INSTANCE.encrypt(publicKey, mystate.randomPlaintext, ciphertextPath); }





    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(BgvEncryptionBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
