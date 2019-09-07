package packet.com.lockedappproject.models;

public class Req {
    private String lockId,houseId,fromUser,toUsers;

    public Req() {
    }

    public Req(String lockId, String houseId, String fromUser, String toUsers) {
        this.lockId = lockId;
        this.houseId = houseId;
        this.fromUser = fromUser;
        this.toUsers = toUsers;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUsers() {
        return toUsers;
    }

    public void setToUsers(String toUsers) {
        this.toUsers = toUsers;
    }


}
