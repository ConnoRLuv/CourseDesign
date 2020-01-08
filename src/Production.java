import java.util.Arrays;
import java.util.Objects;

public class Production {
    private String left;
    private String[] right;
    private String string;
    private int position;


    public Production(String left, String[] right, String string) {
        this.left = left;
        this.right = right;
        this.string = string;
        position = 0;
    }

    public Production(Production production, int position) {
        this.string = production.getString();
        this.left = production.getLeft();
        this.right = production.getRight();
        this.position = position;
    }


    public String getLeft() {
        return left;
    }

    public String[] getRight() {
        return right;
    }

    public String getString() {
        return string;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return left + " -> " + Arrays.toString(right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return position == that.position &&
                Objects.equals(left, that.left) &&
                Arrays.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(left, string, position);
        result = 31 * result + Arrays.hashCode(right);
        return result;
    }
}
