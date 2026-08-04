package com.arkar.lms.service;

import com.arkar.lms.entity.Book;
import com.arkar.lms.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> listAllBooks(){
        return bookRepository.findAll();
    }

    @Transactional
    public void createOrUpdateBook(Book book){
        Book targetBook;
        if(book.getId() == null){
            targetBook = book;
        }else{
            targetBook = bookRepository.findById(book.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not exists"));
            targetBook.setTitle(book.getTitle());
            targetBook.setAuthor(book.getAuthor());
            targetBook.setStock(book.getStock());
        }
        targetBook.setAvailable(book.getStock() > 0);
        bookRepository.save(targetBook);
    }

    public void deleteBook(Long bookId){
        bookRepository.deleteById(bookId);
    }

}
