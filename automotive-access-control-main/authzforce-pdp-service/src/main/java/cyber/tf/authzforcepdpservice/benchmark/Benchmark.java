/* package cyber.tf.authzforcepdpservice.benchmark;

import java.util.LinkedList;
import java.util.List;

public class Benchmark implements CSVSerializable {
    private List<BenchmarkResult> results;

    public Benchmark() {
        this.results = new LinkedList<>();
    }

    public void addResult(BenchmarkResult result) {
        results.add(result);
    }

    public String toCSV() {
        StringBuilder sb = new StringBuilder("id;time\n");

        results.forEach(result -> {
            sb.append(result.toCSV());
            sb.append("\n");
        });

        return sb.toString();
    }
}

*/
