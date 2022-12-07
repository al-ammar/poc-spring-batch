package ma.awb.poc.batch.partitioner;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class PocPartitioner implements Partitioner {

	private static final String PARTITION = "Partition";

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> allContext = new HashMap<>(gridSize);
		for (int i = 0; i < 10; i++) {
			ExecutionContext context = new ExecutionContext();
			context.put("criterion", " u.id like '" + i + "%'");
			context.put("file", PARTITION + "-" +i);
			allContext.put(PARTITION + "-" +i, context);
		}
		for (char alph = 'a'; alph <= 'z'; alph++) {
			ExecutionContext context = new ExecutionContext();
			context.put("criterion", " u.id like '" + alph + "%'");
			context.put("file", PARTITION + "-" + alph);
			allContext.put(PARTITION + "-" + alph, context);
		}
		return allContext;
	}
}
