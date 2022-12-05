package ma.awb.poc.core.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.jmapper.JMapper;

import ma.awb.poc.core.dao.repository.EventBatchRepository;
import ma.awb.poc.core.dao.vo.EventBatchVO;
import ma.awb.poc.core.model.EventBatchDTO;
import ma.awb.poc.core.services.IEventBatch;

@Service
@Transactional
public class EventBatchServiceImpl implements IEventBatch {

	@Autowired
	private EventBatchRepository repository;

	private JMapper<EventBatchDTO, EventBatchVO> mapper = new JMapper<EventBatchDTO, EventBatchVO>(EventBatchDTO.class,
			EventBatchVO.class);
	private JMapper<EventBatchVO, EventBatchDTO> mapperInv = new JMapper<EventBatchVO, EventBatchDTO>(
			EventBatchVO.class, EventBatchDTO.class);

	@Override
	public void upsert(EventBatchDTO eventBatchDTO) {
		repository.save(mapperInv.getDestination(eventBatchDTO));
	}

	@Override
	public EventBatchDTO getEventBatchById(String id) {
		return mapper.getDestination(repository.getReferenceById(id));
	}

}
