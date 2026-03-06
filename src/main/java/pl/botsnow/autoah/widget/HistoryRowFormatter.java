package pl.botsnow.autoah.widget;

import pl.botsnow.autoah.PurchaseRecord;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class HistoryRowFormatter {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    public static String format(PurchaseRecord record) {
        String when = record.timeEpochMs > 0 ? TIME_FORMAT.format(Instant.ofEpochMilli(record.timeEpochMs)) : "--:--:--";
        String seller = record.sellerName == null ? "?" : record.sellerName;
        return "§7[" + when + "] §a" + record.itemName + " §fza §6" + record.price + " §8| §b" + seller;
    }
}
