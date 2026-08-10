package com.startinpoint.lms.service;

import com.startinpoint.lms.dto.response.BorrowRecordResponseDto;
import com.startinpoint.lms.dto.response.UserResponseDto;
import com.startinpoint.lms.entity.BorrowStatus;
import com.startinpoint.lms.mapper.BorrowRecordMapper;
import com.startinpoint.lms.mapper.UserMapper;
import com.startinpoint.lms.repository.BorrowRecordRepository;
import com.startinpoint.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BorrowRecordMapper borrowRecordMapper;
    private final UserMapper userMapper;

    public Page<BorrowRecordResponseDto> getUserActiveBorrowRecords(
            String username, BorrowStatus status,
            int page, int pageSize, String sortField, String sortDir
    ) {
        Pageable pageable = createPageable(page, pageSize, sortField, sortDir);

        if (status == null) {
            return borrowRecordRepository.findByUserUsername(username, pageable)
                    .map(borrowRecordMapper::toResponseDto);
        }
        return borrowRecordRepository.findByUserUsernameAndStatus(username, status, pageable)
                .map(borrowRecordMapper::toResponseDto);
    }

    public Page<BorrowRecordResponseDto> fetchBorrowRecordByKeyword(
            String keyword, String username, BorrowStatus status,
            int page, int size, String sortField, String sortDir
    ) {
        Pageable pageable = createPageable(page, size, sortField, sortDir);

        if (status == null) {
            return borrowRecordRepository.findBorrowBookBykeyword(username, keyword, pageable)
                    .map(borrowRecordMapper::toResponseDto);
        }
        return borrowRecordRepository.findBorrowRecordByKeywordAndStatus(username, keyword, status, pageable)
                .map(borrowRecordMapper::toResponseDto);
    }

    // Get All Users
    public Page<UserResponseDto> getAllUsers(int pageNo, int pageSize, String sortField, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortField, sortDir);
        return userRepository.findAll(pageable).map(userMapper::toResponseDto);
    }

    // Fetch borrow Record by User
    public Page<BorrowRecordResponseDto> fetchBorrowRecordByUser(
            Long userId, BorrowStatus status,
            int pageNo, int pageSize, String sortField, String sortDir
    ) {
        Pageable pageable = createPageable(pageNo, pageSize, sortField, sortDir);

        if (status != null) {
            return borrowRecordRepository.findByUserIdAndStatus(userId, status, pageable)
                    .map(borrowRecordMapper::toResponseDto);
        }
        return borrowRecordRepository.findByUserId(userId, pageable)
                .map(borrowRecordMapper::toResponseDto);
    }

    // Fetch borrow Record by User ID + Keyword
    public Page<BorrowRecordResponseDto> fetchBorrowRecordByUserWithKeyword(
            Long userId, String keyword, BorrowStatus status,
            int page, int pageSize, String sortField, String sortDir
    ) {
        Pageable pageable = createPageable(page, pageSize, sortField, sortDir);

        if (status == null) {
            return borrowRecordRepository.fetchBorrowRecordByUserWithKeyword(userId, keyword, pageable)
                    .map(borrowRecordMapper::toResponseDto);
        }
        return borrowRecordRepository.fetchBorrowRecordByUserWithKeywordAndStatus(userId, keyword, status, pageable)
                .map(borrowRecordMapper::toResponseDto);
    }

    // Reusable Private Helper
    private Pageable createPageable(int page, int size, String sortField, String sortDir) {
        int pageIndex = (page < 1) ? 0 : page - 1;
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        return PageRequest.of(pageIndex, size, sort);
    }
}