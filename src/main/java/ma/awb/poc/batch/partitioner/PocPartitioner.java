package ma.awb.poc.batch.partitioner;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.item.ExecutionContext;

public class PocPartitioner extends SimplePartitioner {

	private static final Logger log = LoggerFactory.getLogger(PocPartitioner.class);

	private static final String PARTITION = "partition";

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> partitions = super.partition(gridSize);
		int i = 0;
		for (ExecutionContext context : partitions.values()) {
			context.put(PARTITION, PARTITION + (i++));
		}
		return partitions;
	}

}
