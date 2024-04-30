package com.esen.bookstore.service;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.Bookstore;
import com.esen.bookstore.repository.BookRepository;
import com.esen.bookstore.repository.BookstoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookStoreService {

    private final BookstoreRepository bookstoreRepository;
    private final BookRepository bookRepository;

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

    public Map<Bookstore,Double> findPrices(Long id){
        var book = bookRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("Cannot find book"));
        var bookStores = bookstoreRepository.findAll().stream()
                .filter(bookStore -> bookStore.getInventory().containsKey(book))
                .toList();

        Map<Bookstore,Double> result = new HashMap<Bookstore, Double>();

        for(var bookStore : bookStores){
            Double price = book.getPrice() * bookStore.getPriceModifier();
            result.put(bookStore,price);
        }

        return result;
    }

    public Map<Book, Integer> getStock(Long id) {
        var bookStore = bookstoreRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("Cannot find bookstore"));
        return bookStore.getInventory();
    }

    public void changeStock(Long bookstoreId, Long bookId, int amount){
        var bookStore = bookstoreRepository.findById(bookstoreId)
                .orElseThrow(() ->  new RuntimeException("Cannot find bookstore"));
        var book = bookRepository.findById(bookId)
                .orElseThrow(() ->  new RuntimeException("Cannot find book"));



        if(bookStore.getInventory().containsKey(book)){
            var entry = bookStore.getInventory().get(book);

            if(entry + amount < 0) throw new UnsupportedOperationException("Invalid amount");

            bookStore.getInventory().replace(book,entry);
        }
        else{
            if(amount < 0) throw new UnsupportedOperationException("Invalid amount");

            bookStore.getInventory().put(book,amount);
        }

        bookstoreRepository.save(bookStore);

    }


}
