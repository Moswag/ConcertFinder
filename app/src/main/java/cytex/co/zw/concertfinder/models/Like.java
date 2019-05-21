package cytex.co.zw.concertfinder.models;

public class Like {

    private String id;
    private String email;
    private String concertId;
    private String concert;
    private String host;

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


    @Override
    public String toString() {
        return "Like{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", concertId='" + concertId + '\'' +
                ", concert='" + concert + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
