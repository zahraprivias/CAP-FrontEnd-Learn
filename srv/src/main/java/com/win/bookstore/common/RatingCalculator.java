package com.win.bookstore.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.OptionalDouble;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.bookservice.BookService_;
import cds.gen.com.win.bookstore.Books;
import cds.gen.com.win.bookstore.Reviews;

@Component
public class RatingCalculator {

    @Autowired
    private PersistenceService db;

    public void initBookRatings() {
        Result result = db.run(Select.from(BookService_.BOOKS).columns(b -> b.ID()));
        for (Books book : result.listOf(Books.class)) {
            setBookRating(book.getId());
        }

    }

    public void setBookRating(String bookId) {
        Result run = db.run(Select.from(BookService_.BOOKS, b -> b.filter(b.ID().eq(bookId)).reviews()));

        Stream<Double> ratings = run.streamOf(Reviews.class).map(r -> r.getRating().doubleValue());
        BigDecimal rating = getAvgRating(ratings);
        db.run(Update.entity(BookService_.BOOKS, b -> b.matching(Books.create(bookId))).data(Books.RATING, rating));
    }

    static BigDecimal getAvgRating(Stream<Double> ratings) {
        OptionalDouble avg = ratings.mapToDouble(Double::doubleValue).average();
        if (!avg.isPresent()) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(avg.getAsDouble()).setScale(1, RoundingMode.HALF_UP);
    }
}
