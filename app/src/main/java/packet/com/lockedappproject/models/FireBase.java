package packet.com.lockedappproject.models;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FireBase {

    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static DatabaseReference ref = null, userRef = null, houseRef = null, lockRef = null, reqRef = null;
    private static User user = null;
    private static ArrayList<Lock> userLocks;
    private static ArrayList<House> userHouses;
    private static HashMap<String, Req> requests;
    private static HashMap<String, User> users;
    private static ArrayList<UpdateHouseData> updateHouse = new ArrayList<>();
    private static ArrayList<UpdateLockData> updateLocks = new ArrayList<>();
    private static ArrayList<UpdateRequests> updateRequests = new ArrayList<>();
    //LISTENERS:
    private static ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
            download();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private static ChildEventListener lockListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Lock lock = dataSnapshot.getValue(Lock.class);
            if (user.lockList.contains(lock.id)) {
                userLocks.add(lock);
                Collections.sort(userLocks);
                for (UpdateLockData u : updateLocks) {
                    u.Notify();
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Lock lock = dataSnapshot.getValue(Lock.class);
            if (lock.admin.contains(mAuth.getUid()) || lock.notAdmin.contains(mAuth.getUid())) {
                int i;
                for (i = 0; i < userLocks.size() && !userLocks.get(i).id.equalsIgnoreCase(lock.id); i++)
                    ;
                if (i < userLocks.size()) {
                    userLocks.set(i, lock);
                }
            } else {
                int i;
                for (i = 0; i < userLocks.size() && !userLocks.get(i).id.equalsIgnoreCase(lock.id); i++)
                    ;
                if (i < userLocks.size())
                    userLocks.remove(i);
            }
            Collections.sort(userLocks);
            for (UpdateLockData u : updateLocks) {
                u.Notify();
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Lock lock = dataSnapshot.getValue(Lock.class);
            int i;
            for (i = 0; i < userLocks.size() && !userLocks.get(i).id.equalsIgnoreCase(lock.id); i++)
                ;
            if (i < userLocks.size()) {
                userLocks.remove(i);
                for (UpdateLockData u : updateLocks) {
                    u.Notify();
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private static ChildEventListener houseListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            House house = dataSnapshot.getValue(House.class);
            if (user.houseList.contains(house.id) && !house.id.equalsIgnoreCase("")) {
                userHouses.add(house);
                Collections.sort(userHouses);
                for (UpdateHouseData u : updateHouse) {
                    u.Notify();
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            House house = dataSnapshot.getValue(House.class);
            if (house.admin.contains(mAuth.getUid())) {
                int i;
                for (i = 0; i < userHouses.size() && !userHouses.get(i).id.equalsIgnoreCase(house.id); i++)
                    ;
                if (i < userHouses.size()) {
                    userHouses.set(i, house);
                }
            } else {
                int i;
                for (i = 0; i < userHouses.size() && !userHouses.get(i).id.equalsIgnoreCase(house.id); i++)
                    ;
                if (i < userHouses.size())
                    userHouses.remove(i);
            }
            Collections.sort(userHouses);
            for (UpdateHouseData u : updateHouse) {
                u.Notify();
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            House house = dataSnapshot.getValue(House.class);
            int i;
            for (i = 0; i < userHouses.size() && !userHouses.get(i).id.equalsIgnoreCase(house.id); i++)
                ;
            if (i < userHouses.size()) {
                userHouses.remove(i);
                for (UpdateHouseData u : updateHouse) {
                    u.Notify();
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private static ChildEventListener reqListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Req req = dataSnapshot.getValue(Req.class);
            if (req.getToUsers().contains(getUid()))
                requests.put(dataSnapshot.getKey(), req);
            for (UpdateRequests u : updateRequests) {
                u.Notify(requests.size());
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Req r = dataSnapshot.getValue(Req.class);
            if (r.getToUsers().contains(getUid()))
                requests.put(dataSnapshot.getKey(), r);
            else if (requests.containsKey(dataSnapshot.getKey()))
                    requests.remove(dataSnapshot.getKey());
            for (UpdateRequests u : updateRequests)
                u.Notify(requests.size());
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            if (requests.containsKey(dataSnapshot.getKey()))
                requests.remove(dataSnapshot.getKey());
            for (UpdateRequests u : updateRequests) {
                u.Notify(requests.size());
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //GETTERS:
    //get current user
    public static User getUser() {
        return user;
    }

    //get user ID
    public static String getUid() {
        return mAuth.getUid();
    }

    //get all the user houses
    public static ArrayList<House> getHouses() {
        return userHouses;
    }

    //get all the user locks
    public static ArrayList<Lock> getLockes() {
        return userLocks;
    }

    //get locks from single house
    public static ArrayList<Lock> getLockFromList(String list) {
        String[] locks = list.split(",");
        ArrayList<Lock> arr = new ArrayList<>();
        for (String s : locks) {
            for (Lock l : userLocks) {
                if (l.id.equalsIgnoreCase(s)) {
                    arr.add(l);
                }
            }
        }
        Collections.sort(arr);
        return arr;
    }

    //get single house by id/name
    public static House getOneHouse(String str) {
        for (House h : userHouses)
            if (h.id.equalsIgnoreCase(str) || (h.name.equalsIgnoreCase(str)))
                return h;
        return null;
    }

    //get single house by lock
    public static House getHousebyLock(String str) {
        for (House h : userHouses)
            if (h.locks.contains(str))
                return h;
        return null;
    }

    //get lock from list by id
    public static Lock getLockByStr(String str) {
        for (Lock lock : userLocks)
            if (lock.id.equalsIgnoreCase(str) || lock.name.equalsIgnoreCase(str))
                return lock;
        return null;
    }

    //get user email from nick
    public static String getEmailFromNick(String nick) {
        for (String uId : users.keySet()) {
            User user = users.get(uId);
            if (user.nick.equalsIgnoreCase(nick))
                return user.email;
        }
        return null;
    }

    //get Requests
    public static HashMap<String, Req> getRequests() {
        return (requests != null && requests.size() > 0) ? requests : null;
    }

    //get another user
    public static User getAnotherUser(String key) {
        if (users.containsKey(key))
            return users.get(key);
        return null;
    }

    //METHODS:
    //Sign up
    public static void signUp(final String user, String pass, final UpdateUi u) {
        mAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener((Activity) u, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (userRef != null)
                                userRef.removeEventListener(userListener);
                            userRef = db.getReference("users").child(getUid());
                            userRef.addValueEventListener(userListener);
                            download();
                            u.Success();
                        } else {
                            // If sign in fails, display a message to the user.
                            u.Failed(task.getException());
                        }
                    }
                });
    }

    //Download data
    private static void download() {
        userHouses = new ArrayList<>();
        userLocks = new ArrayList<>();
        requests = new HashMap<>();
        if (houseRef != null) {
            houseRef.removeEventListener(houseListener);
            lockRef.removeEventListener(lockListener);
        }
        houseRef = db.getReference("house");
        houseRef.addChildEventListener(houseListener);
        lockRef = db.getReference("locks");
        lockRef.addChildEventListener(lockListener);
        reqRef = db.getReference("req");
        reqRef.addChildEventListener(reqListener);
    }

    //download all the users
    public static void getNicks() {
        users = new HashMap<>();
        ref = db.getReference("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User u = dataSnapshot.getValue(User.class);
                users.put(dataSnapshot.getKey(), u);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                users.put(dataSnapshot.getKey(), dataSnapshot.getValue(User.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (users.containsKey(dataSnapshot.getKey()))
                    users.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //checking if nick is taken
    public static boolean checkNick(String s) {
        Iterator it = users.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry data = (Map.Entry) it.next();
            User user = (User) data.getValue();
            if (user.nick.equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    //add listener for houses updates
    public static void addToUpdateHouse(UpdateHouseData adapter) {
        int place = updateHouse.indexOf(adapter);
        if (place > -1)
            updateHouse.set(place, adapter);
        else
            updateHouse.add(adapter);
    }

    //remove listener for houses updates
    public static void removeFromUpdateHouse(UpdateHouseData adapter) {
        int place = updateHouse.indexOf(adapter);
        if (updateHouse.contains(adapter)) {
            updateHouse.remove(adapter);
        }
    }

    //add listener for locks updates
    public static void addToUpdateLocks(UpdateLockData adapter) {
        int place = updateLocks.indexOf(adapter);
        if (place > -1)
            updateLocks.set(place, adapter);
        else
            updateLocks.add(adapter);
    }

    //remove listener for locks updates
    public static void removeFromUpdateLock(UpdateLockData adapter) {
        if (updateLocks.contains(adapter)) {
            updateLocks.remove(adapter);
        }
    }

    //add listener for requests updates
    public static void addToRequestsUpdates(UpdateRequests u) {
        int place = updateRequests.indexOf(u);
        if (place > -1)
            updateRequests.set(place, u);
        else
            updateRequests.add(u);
    }

    //remove listener for Requests updates
    public static void removeFromRequestsUpdates(UpdateRequests u) {
        updateRequests.remove(u);
    }

    //changing lock status
    public static void changeLockTo(Lock lock, String status) {
        lock.status = status;
        ref = db.getReference("locks").child(lock.id);
        ref.setValue(lock);
    }

    //looking for specific lock in all DB
    public static void searchGeneralLock(final String lId, final FindLock cb) {
        houseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    House house = data.getValue(House.class);
                    if (house.locks.contains(lId)) {
                        found = true;
                        findLockInDB(lId, house, cb);
                    }
                }
                if (!found)
                    cb.notFound(lId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //deleting lock process
    public static void deleteLock(String lName, String hId) {
        User user = getUser();
        Lock lock = getLockByStr(lName);
        House house = getOneHouse(hId);
        boolean dltLock = false, dltHouse = false, dltHfromU = true;

        //טיפול במנעול
        if (lock.admin.contains(getUid())) {
            //מחיקת המשתמש מרשימת האדמינים
            lock.admin = remove_id_from_string(lock.admin, getUid());
            //בדיקה אם אין עוד אדמינים
            if (lock.admin.equalsIgnoreCase("")) {
                //בדיקה אם יש עוד משתמשים רגילים והפיכה של אחד לאדמין
                if (!lock.notAdmin.equalsIgnoreCase("")) {
                    String temp[] = lock.notAdmin.split(",");
                    lock.admin = temp[0];
                    lock.notAdmin = remove_id_from_string(lock.notAdmin, temp[0]);
                } else {
                    house.locks = remove_id_from_string(house.locks, lock.id);
                    dltLock = true;
                    dltHouse = house.locks.equalsIgnoreCase("");
                }
            }
        } else
            //מחיקת המשתמש מרשימת היוזרים הרגילים
            lock.notAdmin = remove_id_from_string(lock.notAdmin, getUid());

        //מחיקת בקשות של אותו מנעול
        for (String key : requests.keySet()) {
            Req req = requests.get(key);
            if (req.getLockId().equalsIgnoreCase(lock.name))
                rjctReq(key);
        }

        //מחיקת המנעול מהרשימה של המשתמש
        user.lockList = remove_id_from_string(user.lockList, lock.id);

        //טיפול בבית
        //בדיקה אם יש עוד מנעולים לאותו משתמש בבית
        String locks[] = house.locks.split(",");
        for (String l : locks)
            if (l.length() > 0 && user.lockList.contains(l))
                dltHfromU = false;

        //אם אין למשתמש עוד מנעולים באותו בית - צריך למחוק את הבית
        if (dltHfromU) {
            user.houseList = remove_id_from_string(user.houseList, house.id);
            house.admin = remove_id_from_string(house.admin, getUid());
        }

        //עדכון או מחיקת המנעול
        if (dltLock) {
//            chkReq(lock);
            db.getReference("locks").child(lock.id).removeValue();
        } else
            db.getReference("locks").child(lock.id).setValue(lock);

        //עדכון או מחיקת הבית
        if (dltHouse)
            db.getReference("house").child(house.id).removeValue();
        else
            db.getReference("house").child(house.id).setValue(house);

        //עדכון המשתמש
        userRef.setValue(user);
    }

    //adding new lock to House
    public static void addNewLock(Lock lock, House house) {
        User user = getUser();
        user.lockList = add_id_to_string(user.lockList, lock.id);
        house.locks = add_id_to_string(house.locks, lock.id);

        //בדיקה אם הבית חדש
        if (house.id == null || house.id.length() == 0) {
            ref = houseRef.push();
            house.id = ref.getKey();
            user.houseList = add_id_to_string(user.houseList, house.id);
        }
        //כתיבת המידע
        userRef.setValue(user);
        houseRef.child(house.id).setValue(house);
        lockRef.child(lock.id).setValue(lock);
    }

    //adding req to DB
    public static void AddReq(final House house, final Lock lock) {
        boolean flg = false;
        reqRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i;
                boolean flg = true;
                Iterator it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot ds = (DataSnapshot) it.next();
                    Req req = ds.getValue(Req.class);
                    if (req.getLockId().equalsIgnoreCase(lock.id) && req.getFromUser().equalsIgnoreCase(getUid())) {
                        reqRef.child(ds.getKey()).setValue(new Req(lock.id, house.id, getUid(), lock.admin));
                        flg = false;
                        break;
                    }
                }
                if (flg)
                    reqRef.push().setValue(new Req(lock.id, house.id, getUid(), lock.admin));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        reqRef.push().setValue(new Req(lock.id, house.id, getUid(), lock.admin));
    }

    //updating ReqNum text
    public static int getReqNum() {
        return (requests != null && requests.size() > 0) ? requests.size() : 0;
    }

    //approve request
    public static void apprvReq(String houseId, String lockId, boolean admin, String reqId) {
        House house = getOneHouse(houseId);
        Lock lock = getLockByStr(lockId);
        Req req = requests.get(reqId);
        User user = users.get(req.getFromUser());

        //עדכון המנעול
        if (admin)
            lock.admin = add_id_to_string(lock.admin, req.getFromUser());
        else
            lock.notAdmin = add_id_to_string(lock.notAdmin, req.getFromUser());
        lockRef.child(lock.id).setValue(lock);
        user.lockList = add_id_to_string(user.lockList, lockId);

        //בדיקה ועדכון הבית והמשתמש לפי הצורך
        if (!user.houseList.contains(houseId)) {
            house.admin = add_id_to_string(house.admin, req.getFromUser());
            houseRef.child(houseId).setValue(house);
            user.houseList = add_id_to_string(user.houseList, houseId);
        }

        //עדכון המשתמש
        db.getReference().child("users").child(req.getFromUser()).setValue(user);

        //מחיקת הבקשה
        dltReq(reqId);
    }

    //reject request
    public static void rjctReq(String reqId) {
        Req req = requests.get(reqId);
        req.setToUsers(remove_id_from_string(req.getToUsers(), getUid()));
        if (req.getToUsers().equalsIgnoreCase(""))
            dltReq(reqId);
        else
            reqRef.child(reqId).setValue(req);
    }

    //checking if user signed to lock
    public static boolean findLockInList(String id) {
        for (Lock l : userLocks)
            if (l.id.equalsIgnoreCase(id))
                return true;
        return false;
    }

    //build string from list (sort + adding ",")
    public static String buildStringFromList(List<String> list, String split) {
        Collections.sort(list);
        String s = "";
        for (int i = 0; i < list.size(); i++)
            if (i == 0)
                s = list.get(i);
            else
                s += split + list.get(i);
        return s;
    }


    //PRIVATE METHODS:
    //add id to string
    private static String add_id_to_string(String src, String id) {
        src += (src.length() > 0) ? "," + id : id;
        return buildStringFromList(Arrays.asList(src.split(",")));
    }

    //remove id from String
    private static String remove_id_from_string(String src, String id) {
        List<String> list = new ArrayList<>(Arrays.asList(src.split(",")));
        list.remove(id);
        return buildStringFromList(list);
    }

    //build string from list (sort + adding ",")
    private static String buildStringFromList(List<String> list) {
        Collections.sort(list);
        String s = "";
        for (int i = 0; i < list.size(); i++)
            if (i == 0)
                s = list.get(i);
            else
                s += "," + list.get(i);
        return s;
    }

    //finding lock in the all DB
    private static void findLockInDB(final String str, final House house, final FindLock cb) {
        lockRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Lock lock = data.getValue(Lock.class);
                    if (lock.id.equalsIgnoreCase(str)) {
                        cb.found(house, lock);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //delete request
    private static void dltReq(String reqId) {
        reqRef.child(reqId).removeValue();
    }

    //using to delete relevant requests when deleting lock
    private static void chkReq(Lock lock) {
        Iterator it = requests.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry data = (Map.Entry) it.next();
            Req req = (Req) data.getValue();
            if (req.getLockId().equalsIgnoreCase(lock.id))
                rjctReq(data.getKey().toString());
        }
    }

    //INTERFACES:
    public interface UpdateUi {
        void Success();

        void Failed(Exception e);
    }

    public interface UpdateHouseData {
        void Notify();
    }

    public interface UpdateLockData {
        void Notify();
    }

    public interface FindLock {
        void found(House house, Lock lock);

        void notFound(String lId);
    }

    public interface UpdateRequests {
        void Notify(int size);
    }
}
