package unipassau.thesis.vehicledatadissemination.benchmark.pre.bgv;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import unipassau.thesis.vehicledatadissemination.model.ProxyReEncryptionModel;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.util.concurrent.TimeUnit;

public class BgvKeyGenBenchmark {

    public static String dataDumpFolder = System.getProperty("user.dir") + "/data_dump/BGV/";
    public static String cryptoContextFileName = "benchmark-crypto-context";
    public static String cryptoContextpath = dataDumpFolder + cryptoContextFileName;
    public static String keyPath = dataDumpFolder + "benchmark";

    static final int iter = 5;
    static final int fork = 1;
    static final int warmIter = 3;
    static final int iter_time = 5;


    @State(Scope.Thread)
    public static class BGVBenchmarkStateKeyGen {
        ProxyReEncryptionModel pre =new ProxyReEncryptionModel("BGV", 256, 16384, ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);

        @Setup(Level.Invocation)
        public void setup() {
            OpenPRE.INSTANCE.cryptoContextGen(pre.getSchemeName(),
                    dataDumpFolder,
                    cryptoContextFileName,
                    256,
                    pre.getRingSize(),
                    pre.getSecurityLevel().toString());

        }
    }



    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void keyGen_CC_BGV_128_16384(BGVBenchmarkStateKeyGen mystate) {
        mystate.pre.setRingSize(16384);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        OpenPRE.INSTANCE.keysGen(cryptoContextpath , keyPath);
    }

    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void keyGen_CC_BGV_128_32768(BGVBenchmarkStateKeyGen mystate) {
        mystate.pre.setRingSize(32768);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_128);
        OpenPRE.INSTANCE.keysGen(cryptoContextpath , keyPath);    }


    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void keyGen_CC_BGV_192_16384(BGVBenchmarkStateKeyGen mystate) {
        mystate.pre.setRingSize(16384);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_192);
        OpenPRE.INSTANCE.keysGen(cryptoContextpath , keyPath);    }

    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void keyGen_CC_BGV_192_32768(BGVBenchmarkStateKeyGen mystate) {
        mystate.pre.setRingSize(32768);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_192);
        OpenPRE.INSTANCE.keysGen(cryptoContextpath , keyPath);    }


    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void keyGen_CC_BGV_256_16384(BGVBenchmarkStateKeyGen mystate) {
        mystate.pre.setRingSize(16384);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_256);
        OpenPRE.INSTANCE.keysGen(cryptoContextpath , keyPath);    }

    @Benchmark
    @Warmup(iterations = warmIter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = iter, time = iter_time, timeUnit = TimeUnit.SECONDS)
    @Fork(value = fork)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void keyGen_CC_BGV_256_32768(BGVBenchmarkStateKeyGen mystate) {
        mystate.pre.setRingSize(32768);
        mystate.pre.setSecurityLevel(ProxyReEncryptionModel.SecurityLevel.SECURITY_LEVEL_256);
        OpenPRE.INSTANCE.keysGen(cryptoContextpath , keyPath);    }




    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(BgvKeyGenBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
