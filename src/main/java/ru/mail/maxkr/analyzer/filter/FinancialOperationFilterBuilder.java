package ru.mail.maxkr.analyzer.filter;

import com.google.common.annotations.VisibleForTesting;
import ru.mail.maxkr.entity.FinancialOperation;
import ru.mail.maxkr.entity.Currency;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FinancialOperationFilterBuilder {
    private List<Filter> filters = new ArrayList<Filter>();
//    private float sum;
//    private String currency;
//    private String category;
//    private String source;
//    private Date operationDate;
//    private String description;

    public void addFiltrationBySum(final float sum, final Currency currency) {
        Filter filter = new Filter() {
            @Override
            public boolean isOperationMatches(FinancialOperation operation) {
                return operation.getSumWithCurrency().getValue() == sum &&
                        operation.getSumWithCurrency().getCurrency() == currency;

            }
        };
        filters.add(filter);
    }

    public void addFiltrationByDateRange(final Date from, final Date till) {
        filters.add(
            new Filter() {
                @Override
                public boolean isOperationMatches(FinancialOperation operation) {
                    long operationTime = operation.getOperationDate().getTime();
                    return (operationTime >= from.getTime() && operationTime < till.getTime());
                }
            }
        );
    }

    public void addFiltrationByDate(final Date date) {
        Date[] range = generateDateRangeForOneDay(date);
        addFiltrationByDateRange(range[0], range[1]);
    }


    @VisibleForTesting
    /* default */ Date[] generateDateRangeForOneDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date from = calendar.getTime();

        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        Date till = new Date(calendar.getTimeInMillis() + 1);

        return new Date[] {from, till};
    }

    public Filter build() {
        return new Filter() {
            @Override
            public boolean isOperationMatches(FinancialOperation operation) {
                for (Filter currentFilter : filters) {
                    if (!currentFilter.isOperationMatches(operation)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

}
