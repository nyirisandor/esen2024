package com.esen.bookstore.service;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.Bookstore;
import com.esen.bookstore.repository.BookstoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookStoreService {

    private final BookstoreRepository bookstoreRepository;

    @Transactional
    public void removeBookFromInventories(Book book){
        bookstoreRepository.findAll()
                .forEach(bookstore -> {
                    bookstore.getInventory().remove(book);
                    bookstoreRepository.save(bookstore);
                });
    }

    public void save(Bookstore bookstore){
        bookstoreRepository.save(bookstore);
    }

    public List<Bookstore> findAll(){
        return bookstoreRepository.findAll();
    }

    public void deleteBookStore(Long id) {
        var bookStore = bookstoreRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("Cannot find bookstore"));
        bookstoreRepository.delete(bookStore);
    }

    public void updateBookStore(Long id, String location, Double priceModifier, Double moneyInCashRegister) {
        if(Stream.of(location,priceModifier,moneyInCashRegister).allMatch(Objects::isNull)){
            throw new UnsupportedOperationException("There's nothing to update");
        }
        var bookStore = bookstoreRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("Cannot find bookstore"));

        if(location != null){
            bookStore.setLocation(location);
        }

        if(priceModifier != null){
            bookStore.setPriceModifier(priceModifier);
        }

        if(moneyInCashRegister != null){
            bookStore.setMoneyInCashRegister(moneyInCashRegister);
        }

        bookstoreRepository.save(bookStore);

    }
}
