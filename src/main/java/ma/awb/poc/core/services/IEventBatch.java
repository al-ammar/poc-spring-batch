package ma.awb.poc.core.services;

import ma.awb.poc.core.model.EventBatchDTO;

public interface IEventBatch {
	
	void upsert(EventBatchDTO eventBatchDTO);
	
	EventBatchDTO getEventBatchById(String id);
	

}
