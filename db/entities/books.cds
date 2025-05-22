namespace com.win.bookstore;

using {
    cuid,
    Currency,
    managed
} from '@sap/cds/common';

using {com.win.bookstore as bs} from '../index';

@fiori.draft.enabled
entity Books : cuid, managed {
    title        : localized bs.title;
    descr        : localized bs.description;
    stock        : Integer;
    price        : bs.price;
    currency     : Currency;
    rating       : bs.rating;
    reviews      : Association to many bs.Reviews on reviews.book = $self;
    isReviewable : bs.Tech_Boolean not null default true;
    status: Association to bs.status @readonly;
}

// input validation
annotate Books with {
    title @mandatory;
    stock @mandatory;
}

// entity CodeList {
//     key code : String;
//     text     : String;
// }