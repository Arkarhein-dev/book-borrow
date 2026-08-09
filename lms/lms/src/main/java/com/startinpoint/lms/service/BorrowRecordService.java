package com.startinpoint.lms.service;

import com.startinpoint.lms.entity.BorrowRecord;
import com.startinpoint.lms.entity.BorrowStatus;
import com.startinpoint.lms.entity.User;
import com.startinpoint.lms.repository.BookRepository;
import com.startinpoint.lms.repository.BorrowRecordRepository;
import com.startinpoint.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;

    public Page<BorrowRecord> getUserActiveBorrowRecords(
            String username, BorrowStatus status,
        int page,int pageSize, String sortField, String sortDir
    ){
        int pageIndex = (page < 1) ? 0 : page-1;
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        if(status == null){
            return borrowRecordRepository.findByUserUsername(username,pageable);
        }
        return borrowRecordRepository.findByUserUsernameAndStatus(username,status,pageable);
    }

    public Page<BorrowRecord> fetchBorrowRecordByKeyword(
            String keyword, String username, BorrowStatus status,
            int page, int size, String sortField, String sortDir
    ) {
        int pageIndex = (page < 1) ? 0 : page - 1;
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageIndex,size, sort);

        if(status == null){
            return borrowRecordRepository.findBorrowBookBykeyword(username,keyword,pageable);
        }
        return borrowRecordRepository.findBorrowRecordByKeywordAndStatus(username,keyword,status,pageable);
    }

    // Get All Users
    public Page<User> getAllUsers(int pageNo, int pageSize,  String sortField, String sortDir){
        int pageIndex = pageNo < 1 ? 0 : pageNo-1;

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageIndex, pageSize,sort);
        return userRepository.findAll(pageable);
    }


    // fetch borrow Record by User
    public Page<BorrowRecord> fetchBorrowRecordByUser(
            Long userId, BorrowStatus status,
            int pageNo, int pageSize,String sortField, String sortDir
    ){
        int pageIndex = (pageNo < 1) ? 0 : pageNo - 1;
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);

        if(status != null){
            return borrowRecordRepository.findByUserIdAndStatus(userId,status,pageable);
        }
        return borrowRecordRepository.findByUserId(userId,pageable);
    }


}
