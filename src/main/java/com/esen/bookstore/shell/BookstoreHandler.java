package com.esen.bookstore.shell;


import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.Bookstore;
import com.esen.bookstore.service.BookStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Bookstore related commands")
@RequiredArgsConstructor
public class BookstoreHandler {
    private final BookStoreService bookStoreService;

    @ShellMethod(value = "Create a bookstore", key = "create bookstore")
    void createBookStore(String location,
                         @ShellOption(defaultValue = "1")Double priceModifier,
                         @ShellOption(defaultValue = ShellOption.NULL)Double moneyInCashRegister){
        bookStoreService.save(Bookstore
                .builder()
                .location(location)
                .priceModifier(priceModifier)
                .moneyInCashRegister(moneyInCashRegister)
                .build());
    }

    @ShellMethod(value = "List bookstores", key = "list bookstores")
    String listBookStores(){
        return bookStoreService.findAll().stream()
                .map(bookstore -> "ID: %s, Location: %s, Price modifier: %s, Money in cash register: %s, Inventory: %s"
                        .formatted(bookstore.getId(), bookstore.getLocation(), bookstore.getPriceModifier(), bookstore.getMoneyInCashRegister(),bookstore.getInventory()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Delete bookstore", key = "delete bookstore")
    void deleteBook(Long id){bookStoreService.deleteBookStore(id);}

    @ShellMethod(value = "Update bookstore", key = "update bookstore")
    void updateBookStore(Long id,
                         @ShellOption(defaultValue = ShellOption.NULL) String location,
                         @ShellOption(defaultValue = ShellOption.NULL) Double pricemodifier,
                         @ShellOption(defaultValue = ShellOption.NULL) Double moneyInCashRegister
                         ){
        bookStoreService.updateBookStore(id,location,pricemodifier,moneyInCashRegister);
    }

    @ShellMethod(key = "get stock", value = "Get stock")
    String getStock(Long id){
        return bookStoreService.getStock(id)
                .entrySet()
                .stream()
                .map(e -> "ID: %s, Title: %s, Stock: %s".formatted(e.getKey().getId(),e.getKey().getTitle(),e.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(key = "add stock", value = "add stock")
    void addStock(Long bookstoreId, Long bookId, Integer amount){
        bookStoreService.changeStock(bookstoreId,bookId, amount);
    }

}
