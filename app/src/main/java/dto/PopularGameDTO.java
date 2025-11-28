package dto;

public class PopularGameDTO {
    private int rank;
    private String gameName;
    private double share; // 점유율
    public PopularGameDTO(){}
    public PopularGameDTO(int rank, String gameName, double share) {
        this.rank = rank;
        this.gameName = gameName;
        this.share = share;
    }
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public double getShare() {
        return share;
    }

    public void setShare(double share) {
        this.share = share;
    }
}
