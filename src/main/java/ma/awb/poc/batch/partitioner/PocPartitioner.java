package ma.awb.poc.batch.partitioner;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class PocPartitioner implements Partitioner {

	private static final Logger log = LoggerFactory.getLogger(PocPartitioner.class);

	private static final String PARTITION = "Xpartition";

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> allContext = new HashMap<String, ExecutionContext>(gridSize);
		int index = 1;
		for (int i = 0; i < 10; i++) {
			ExecutionContext context = new ExecutionContext();
			context.put("criterion", " u.id like '" + i + "%'");
			context.put("file", PARTITION + index);
			allContext.put(PARTITION + index++, context);
		}
		for (char alph = 'a'; alph <= 'z'; alph++) {
			ExecutionContext context = new ExecutionContext();
			context.put("criterion", " u.id like '" + alph + "%'");
			context.put("file", PARTITION + index);
			allContext.put(PARTITION + index++, context);
		}
		log.info("Nombre de partitioner {}", index);
		return allContext;
	}

}
