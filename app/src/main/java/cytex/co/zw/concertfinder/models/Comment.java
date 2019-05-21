package cytex.co.zw.concertfinder.models;

public class Comment {

    private String id;
    private String email;
    private String concertId;
    private String concert;
    private String time;
    private String comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConcertId() {
        return concertId;
    }

    public void setConcertId(String concertId) {
        this.concertId = concertId;
    }

    public String getConcert() {
        return concert;
    }

    public void setConcert(String concert) {
        this.concert = concert;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", concertId='" + concertId + '\'' +
                ", concert='" + concert + '\'' +
                ", time='" + time + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
