package com.eralp.ecommerce.service;

import com.eralp.ecommerce.entity.IdempotencyOperationType;
import com.eralp.ecommerce.entity.IdempotencyRecord;
import com.eralp.ecommerce.entity.IdempotencyStatus;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.repository.IdempotencyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    @Transactional(readOnly = true)
    public Optional<IdempotencyRecord> findByKey(String idempotencyKey) {
        return idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public IdempotencyRecord createProcessingRecord(
            String idempotencyKey,
            Long userId,
            IdempotencyOperationType operationType,
            String requestHash
    ) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(idempotencyKey);
        record.setUserId(userId);
        record.setOperationType(operationType);
        record.setRequestHash(requestHash);
        record.setStatus(IdempotencyStatus.PROCESSING);
        return idempotencyRecordRepository.saveAndFlush(record);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(Long idempotencyRecordId, Long orderId) {
        IdempotencyRecord record = idempotencyRecordRepository.findById(idempotencyRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Idempotency record not found"));
        record.setStatus(IdempotencyStatus.SUCCESS);
        record.setResponseOrderId(orderId);
        idempotencyRecordRepository.save(record);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long idempotencyRecordId) {
        IdempotencyRecord record = idempotencyRecordRepository.findById(idempotencyRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Idempotency record not found"));
        record.setStatus(IdempotencyStatus.FAILED);
        idempotencyRecordRepository.save(record);
    }
}
