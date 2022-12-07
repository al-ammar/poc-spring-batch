package ma.awb.poc.batch.writer;

import java.util.Map;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;

import ma.awb.poc.core.dao.vo.EventBatchVO;

public class EventBatchLineAggregator implements LineAggregator<EventBatchVO> {

	public static final String LINE_SEPARATOR = System.lineSeparator();
	public static final String FIELD_SEPARATOR = DelimitedLineTokenizer.DELIMITER_COMMA;

	private Map<String, LineAggregator<Object>> aggregators;

	@Override
	public String aggregate(EventBatchVO item) {
		StringBuilder result = new StringBuilder();
		result.append(aggregators.get("id").aggregate(item) + FIELD_SEPARATOR);
		result.append(aggregators.get("key").aggregate(item) + FIELD_SEPARATOR);
		result.append(aggregators.get("value").aggregate(item) + FIELD_SEPARATOR);
		result.append(aggregators.get("type").aggregate(item) + FIELD_SEPARATOR);
		return result.toString();
	}
	
	public void setAggregators(Map<String, LineAggregator<Object>> aggregators) {
		this.aggregators = aggregators;
	}

}
