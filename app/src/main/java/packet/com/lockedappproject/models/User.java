package packet.com.lockedappproject.models;

public class User{
    public String houseList,lockList,email,nick;

    public User() {
    }

    public User(String houseList, String lockList,String email, String nick) {
        this.houseList = houseList;
        this.lockList = lockList;
        this.email = email;
        this.nick = nick;
    }
}
