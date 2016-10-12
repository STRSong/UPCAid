package jwxt.cacher.cc.jwxt.info;

import java.io.Serializable;

/**
 * Created by xhaiben on 2016/10/7.
 */
public class BookInfo implements Serializable {
    private String bookName;
    private String authorName;
    private String borrowDate;
    private String returnDate;
    private String barCode;
    private String checkNum;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(String checkNum) {
        this.checkNum = checkNum;
    }

    public BookInfo() {
    }

    public BookInfo(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "bookName='" + bookName + '\'' +
                ", authorName='" + authorName + '\'' +
                ", borrowDate='" + borrowDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", barCode='" + barCode + '\'' +
                ", checkNum='" + checkNum + '\'' +
                '}';
    }
}
