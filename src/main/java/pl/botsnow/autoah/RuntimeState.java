package pl.botsnow.autoah;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class RuntimeState {
    private static final int MAX_PURCHASES = 5;
    private static final Deque<PurchaseRecord> LAST_PURCHASES = new ArrayDeque<>();

    public static void setPurchases(List<PurchaseRecord> records) {
        LAST_PURCHASES.clear();
        for (PurchaseRecord record : records) {
            addPurchase(record);
        }
    }

    public static void addPurchase(PurchaseRecord record) {
        LAST_PURCHASES.addFirst(record);
        while (LAST_PURCHASES.size() > MAX_PURCHASES) {
            LAST_PURCHASES.removeLast();
        }
    }

    public static List<PurchaseRecord> getPurchases() {
        return new ArrayList<>(LAST_PURCHASES);
    }
}
