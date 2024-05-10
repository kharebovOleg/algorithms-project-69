package hexlet.code;

public class DocsIdFullAndPartialMatches {

    private String id;
    private long fullMatches;
    private long partialMatches;

    public DocsIdFullAndPartialMatches() {
    }

    public DocsIdFullAndPartialMatches(String id, long fullMatches, long partialMatches) {
        this.id = id;
        this.fullMatches = fullMatches;
        this.partialMatches = partialMatches;
    }

    public DocsIdFullAndPartialMatches(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getFullMatches() {
        return fullMatches;
    }

    public void setFullMatches(long fullMatches) {
        this.fullMatches = fullMatches;
    }

    public long getPartialMatches() {
        return partialMatches;
    }

    public void setPartialMatches(long partialMatches) {
        this.partialMatches = partialMatches;
    }

    @Override
    public String toString() {
        return "DocsIdFullAndPartialMatches{" +
                "id='" + id + '\'' +
                ", fullMatches=" + fullMatches +
                ", partialMatches=" + partialMatches +
                '}';
    }
}
