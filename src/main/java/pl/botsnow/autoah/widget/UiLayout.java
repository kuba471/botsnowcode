package pl.botsnow.autoah.widget;

public class UiLayout {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public UiLayout(int centerX, int top, int width, int height) {
        this.left = centerX - (width / 2);
        this.top = top;
        this.right = left + width;
        this.bottom = top + height;
    }
}
