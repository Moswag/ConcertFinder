package cytex.co.zw.concertfinder.models;

public class Report {

    private String concert;
    private int likes;

    public Report() {
    }

    public Report(String concert, int likes) {
        this.concert = concert;
        this.likes = likes;
    }

    public String getConcert() {
        return concert;
    }

    public void setConcert(String concert) {
        this.concert = concert;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }


    @Override
    public String toString() {
        return "Report{" +
                "concert='" + concert + '\'' +
                ", likes=" + likes +
                '}';
    }
}
