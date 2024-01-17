package reporting.service;

import java.math.BigDecimal;
import java.util.Objects;

public class LoggedTransaction {
    BigDecimal amount;
    String from, to;
    String token;

    public LoggedTransaction(BigDecimal amount, String from, String to, String token) {
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.token = token;
    }

    public LoggedTransaction() {

    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoggedTransaction that = (LoggedTransaction) o;
        return Objects.equals(amount, that.amount) && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, from, to, token);
    }
}
