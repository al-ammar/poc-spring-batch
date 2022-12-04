package ma.awb.poc.batch.partitioner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class PocPartitioner implements Partitioner {

	private static final Logger log = LoggerFactory.getLogger(PocPartitioner.class);

	private static final String PARTITION = "Xpartition";

	private int maxResult;

	private int chunck;

	public PocPartitioner() {
	}

	public PocPartitioner(int maxResult, int chunck) {
		this.maxResult = maxResult;
		this.chunck = chunck;
	}

	private int current = 0;
	private int index = 1;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> allContext = new HashMap<String, ExecutionContext>(gridSize);
		int atraiter = 0;
//		do {
//			ExecutionContext context = new ExecutionContext();
//			log.info("index :" + atraiter);
//			context.put("current", atraiter);
//			context.put("file", PARTITION + index);
//			allContext.put(PARTITION + index++, context);
//			atraiter += current + chunck;
//		} while (atraiter <= maxResult);
		String notLike = "u.id not like '";
		String criteriaNotLike = "";
		int i;
		for (i = 0; i < 10; i++) {
			ExecutionContext context = new ExecutionContext();
			context.put("criteria", " u.id like '" + i + "%'");
			context.put("file", PARTITION + index);
//			context.put("current", atraiter);
//			atraiter += current + chunck;
			allContext.put(PARTITION + index++, context);

//			criteriaNotLike += notLike + i + "%' " + (i < 9 ? "and " : "");
		}

		for (char alph = 'a'; alph <= 'z'; alph++) {
			ExecutionContext context = new ExecutionContext();
			context.put("criteria", " u.id like '" + alph + "%'");
			context.put("file", PARTITION + index);
//			context.put("current", atraiter);
//			atraiter += current + chunck;
			allContext.put(PARTITION + index++, context);
		}

//		ExecutionContext context = new ExecutionContext();
//		context.put("criteria", criteriaNotLike);
//		context.put("file", PARTITION + index);
//		allContext.put(PARTITION + index, context);
		return allContext;
	}

}
